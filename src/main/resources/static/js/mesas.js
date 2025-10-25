function actualizarResumen() {
    const mesasEls = Array.from(document.querySelectorAll('.mesa'));
    const countV = mesasEls.filter(el => el.classList.contains('mesa-verde')).length;
    const countA = mesasEls.filter(el => el.classList.contains('mesa-azul')).length;
    const countAm = mesasEls.filter(el => el.classList.contains('mesa-amarillo')).length;
    const countR = mesasEls.filter(el => el.classList.contains('mesa-rojo')).length;

    document.getElementById('countVerde').textContent = countV;
    document.getElementById('countAzul').textContent = countA;
    document.getElementById('countAmarillo').textContent = countAm;
    document.getElementById('countRojo').textContent = countR;
}

function initMesasFromDOM() {
    document.querySelectorAll('.mesa').forEach(el => {
        if (!el.dataset.estado) el.dataset.estado = 'gris';

        const estado = el.dataset.estado;
        if (estado && estado !== 'gris') {
            el.classList.add('mesa-' + estado);
            const estadoLabel = el.querySelector('.mesa-estado');
            if (estadoLabel) estadoLabel.classList.add('estado-' + estado);
        }
    });

    actualizarResumen();
}

document.addEventListener('DOMContentLoaded', () => {
    initMesasFromDOM();
});