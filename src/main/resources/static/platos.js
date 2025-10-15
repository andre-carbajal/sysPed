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
    document.querySelectorAll('.plato-card[data-cocinero="true"]').forEach(function(card) {
        card.addEventListener('click', function() {
            const plateId = card.getAttribute('data-plate-id');
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
    });
}

function updatePlateStatusInView(plate) {
    const card = document.querySelector(`.plato-card[data-plate-id="${plate.id}"]`);
    if (card) {
        card.setAttribute('data-active', plate.active);
        card.classList.toggle('plato-inactivo', !plate.active);
    }
}

// Llama a la función de conexión al cargar la página
window.addEventListener('DOMContentLoaded', function() {
    connectPlateStatusWebSocket();
});

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        initPlatosTabEvents();
        initPlatoCardToggleEvents();
    });
} else {
    initPlatosTabEvents();
    initPlatoCardToggleEvents();
}
