function initPlatosTabEvents() {
    document.querySelectorAll('.category-content').forEach(function (content) {
        content.style.maxHeight = '0';
        content.dataset.transitioning = 'false';
    });
    document.querySelectorAll('.category-section > h3').forEach(function (h3) {
        h3.addEventListener('click', function () {
            const idx = h3.getAttribute('data-cat-idx');
            const content = document.querySelector('.category-content[data-cat-idx="' + idx + '"]');
            if (!content) {
                return;
            }
            if (content.dataset.transitioning === 'true') {
                return;
            }
            content.dataset.transitioning = 'true';
            if (content.style.maxHeight && content.style.maxHeight !== '0px') {
                content.style.maxHeight = '0';
            } else {
                content.style.maxHeight = content.scrollHeight + 'px';
            }
            content.addEventListener('transitionend', function handler() {
                content.dataset.transitioning = 'false';
                content.removeEventListener('transitionend', handler);
            });
        });
    });
}

function initPlatoCardToggleEvents() {
    document.querySelectorAll('.plato-card').forEach(function(card) {
        card.addEventListener('click', function() {
            const isCocinero = card.hasAttribute('data-cocinero');
            const plateId = card.getAttribute('data-plate-id');

            if (isCocinero) {
                const currentActive = card.getAttribute('data-active') === 'true';
                const newActive = !currentActive;
                if (!confirm('¿Deseas guardar el cambio?')) {
                    return;
                }
                card.style.pointerEvents = 'none';
                fetch('/dashboard/plate/set-active', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `plateId=${plateId}&active=${newActive}`
                })
                .then(response => response.text())
                .then(result => {
                    if (result === 'OK') {
                        card.setAttribute('data-active', newActive);
                        if (newActive) {
                            card.classList.remove('plato-inactivo');
                        } else {
                            card.classList.add('plato-inactivo');
                        }
                    } else {
                        alert(result);
                    }
                    card.style.pointerEvents = '';
                })
                .catch(() => {
                    alert('Error de red');
                    card.style.pointerEvents = '';
                });
            } else {
                openUpdatePlateModal(card);
            }
        });
    });
}

// --- WebSocket para actualización en tiempo real de platos ---
let stompClient = null;

function connectPlateStatusWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/plate-status', function (message) {
            const plate = JSON.parse(message.body);
            updatePlateStatusInView(plate);
        });
        stompClient.subscribe('/topic/plate-updates', function (message) {
            const plate = JSON.parse(message.body);
            updatePlateInView(plate);
        });
    });
}

function updatePlateStatusInView(plate) {
    const card = document.querySelector(`.plato-card[data-plate-id="${plate.id}"]`);
    if (card) {
        card.setAttribute('data-active', plate.active);
        card.classList.toggle('plato-inactivo', !plate.active);
    }
}

function openUpdatePlateModal(card) {
    const overlay = document.getElementById('updatePlateModal');
    const form = document.getElementById('updatePlateForm');
    document.getElementById('plateName').value = card.getAttribute('data-name') || '';
    document.getElementById('plateDescription').value = card.getAttribute('data-description') || '';
    document.getElementById('platePrice').value = card.getAttribute('data-price') || '';
    document.getElementById('plateActive').checked = card.getAttribute('data-active') === 'true';
    form.setAttribute('data-plate-id', card.getAttribute('data-plate-id'));
    overlay.style.display = 'flex';
}

function closeUpdatePlateModal() {
    const overlay = document.getElementById('updatePlateModal');
    overlay.style.display = 'none';
}

function updatePlateInView(plate) {
    const card = document.querySelector(`.plato-card[data-plate-id="${plate.id}"]`);
    if (card) {
        card.setAttribute('data-name', plate.name);
        card.setAttribute('data-description', plate.description);
        card.setAttribute('data-price', plate.price);
        card.setAttribute('data-active', plate.active);
        card.setAttribute('data-image-base64', plate.imageBase64 || '');

        const img = card.querySelector('.plato-img');
        if (plate.imageBase64) {
            img.src = 'data:image/jpeg;base64,' + plate.imageBase64;
            img.style.display = 'block';
        } else {
            img.style.display = 'none';
        }

        const nameEl = card.querySelector('h3');
        nameEl.textContent = plate.name;

        const descEl = card.querySelector('p');
        descEl.textContent = plate.description;

        const priceEl = card.querySelector('.plato-price span');
        priceEl.textContent = new Intl.NumberFormat('es-PE', { minimumFractionDigits: 2 }).format(plate.price);

        card.classList.toggle('plato-inactivo', !plate.active);
    }
}

window.addEventListener('DOMContentLoaded', function() {
    connectPlateStatusWebSocket();
});

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        initPlatosTabEvents();
        initPlatoCardToggleEvents();
        initModalEvents();
    });
} else {
    initPlatosTabEvents();
    initPlatoCardToggleEvents();
    initModalEvents();
}

function initModalEvents() {
    document.getElementById('closeUpdatePlateModal').addEventListener('click', closeUpdatePlateModal);
    document.getElementById('cancelUpdate').addEventListener('click', closeUpdatePlateModal);
    document.getElementById('updatePlateModal').addEventListener('click', function(event) {
        if (event.target === this) {
            closeUpdatePlateModal();
        }
    });
    document.getElementById('updatePlateForm').addEventListener('submit', function(event) {
        event.preventDefault();
        savePlateUpdate();
    });
}

function savePlateUpdate() {
    const form = document.getElementById('updatePlateForm');
    const plateId = form.getAttribute('data-plate-id');
    const formData = new FormData(form);

    const imageFile = formData.get('image');
    if (imageFile && imageFile.size > 0) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const base64Image = e.target.result.split(',')[1]; // Remove data:image/... prefix
            formData.set('imageBase64', base64Image);
            formData.delete('image');
            submitPlateUpdate(plateId, formData);
        };
        reader.readAsDataURL(imageFile);
    } else {
        const existingImage = document.querySelector(`.plato-card[data-plate-id="${plateId}"]`).getAttribute('data-image-base64') || '';
        formData.set('imageBase64', existingImage);
        formData.delete('image');
        submitPlateUpdate(plateId, formData);
    }
}

function submitPlateUpdate(plateId, formData) {
    const data = {
        id: plateId,
        price: parseFloat(formData.get('price')),
        imageBase64: formData.get('imageBase64'),
        active: formData.get('active') === 'on'
    };

    fetch('/dashboard/plate/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `id=${data.id}&price=${data.price}&imageBase64=${encodeURIComponent(data.imageBase64)}&active=${data.active}`
    })
    .then(response => response.text())
    .then(result => {
        if (result === 'OK') {
            closeUpdatePlateModal();
        } else {
            alert(result);
        }
    })
    .catch(() => {
        alert('Error de red');
    });
}
