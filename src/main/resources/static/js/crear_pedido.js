let platosSeleccionados = [];
let idMesaActual = null;

function initializeCrearPedido() {
    const inputMesaId = document.getElementById('pedidoTableId');
    if (inputMesaId) {
        idMesaActual = inputMesaId.value;
    } else {
        console.error('No se encontró el elemento con id "pedidoTableId"');
        return;
    }

    addCrearPedidoEventListeners();

}

function addCrearPedidoEventListeners() {
    const btnVolver = document.getElementById('btnVolverAMesas');
    if (btnVolver) {
        btnVolver.addEventListener('click', volverAVistaMesas);
    }

    const btnEnviar = document.getElementById('btnEnviarPedido');
    if (btnEnviar) {
        btnEnviar.addEventListener('click', enviarPedidoSimple);
    }

    const contenedorPlatos = document.getElementById('contenedorDePlatos');
    if (contenedorPlatos) {
        contenedorPlatos.addEventListener('click', function(e) {
            const platoCard = e.target.closest('.item-plato-menu');
            if (platoCard) {
                const plateId = platoCard.getAttribute('data-plate-id');
                if (plateId) {
                    togglePlateSelection(plateId, platoCard);
                }
            }
        });
    }
}

function togglePlateSelection(plateId, platoCardElement) {
    const index = platosSeleccionados.indexOf(plateId);

    if (index > -1) {
        platosSeleccionados.splice(index, 1);
        platoCardElement.classList.remove('seleccionado');
    } else {
        platosSeleccionados.push(plateId);
        platoCardElement.classList.add('seleccionado');
    }
    console.log("Platos seleccionados:", platosSeleccionados);
}

function volverAVistaMesas() {
    const mesasTab = document.querySelector('.nav-tab[data-tab="mesas"]');
    if (mesasTab) {
        mesasTab.click();
    } else {
        console.error("No se encontró la pestaña 'Mesas'");
    }
}

function enviarPedidoSimple() {
    if (platosSeleccionados.length === 0) {
        showToast("No has seleccionado ningún plato.", "warning");
        return;
    }

    if (!idMesaActual) {
        showToast("Error: No se pudo identificar la mesa.", "error");
        return;
    }

    const itemsParaEnviar = platosSeleccionados.map(pId => ({
        plateId: parseInt(pId, 10),
        cantidad: 1,
        notas: ""
    }));

    const requestBody = {
        items: itemsParaEnviar
    };

    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    if (!token || !header) {
        console.error("¡Token CSRF no encontrado!");
        showToast("Error de seguridad. Recargue la página.", "error");
        return;
    }

    const url = `/api/v1/mesas/${idMesaActual}/crear-pedido`;

    const btnEnviar = document.getElementById('btnEnviarPedido');
    if(btnEnviar) btnEnviar.disabled = true;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text || `Error ${response.status}`) });
            }
            return response.text();
        })
        .then(result => {
            console.log("Respuesta:", result);
            showToast("Pedido enviado a cocina.", "success");
            volverAVistaMesas();
        })
        .catch(error => {
            console.error("Error al enviar:", error);
            showToast(`Error al enviar pedido: ${error.message}`, "error");
            if(btnEnviar) btnEnviar.disabled = false;
        });
}

initializeCrearPedido();