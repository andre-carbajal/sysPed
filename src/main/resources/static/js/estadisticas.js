// Variables globales
let revenueChart = null;
let peakDaysChart = null;
let currentFilters = {
    startDate: null,
    endDate: null,
    period: 'daily'
};

/**
 * Inicializa la pestaña de estadísticas
 */
function initializeEstadisticas() {
    console.log('Inicializando estadísticas...');
    
    // Configurar fechas por defecto (último mes)
    setDefaultDates();
    
    // Cargar datos iniciales
    loadStatisticsSummary();
    loadRevenueData();
    loadPeakDaysData();
    loadTopPlatesData('quantity');
    
    // Configurar event listeners
    setupEventListeners();
}

/**
 * Configura las fechas por defecto (último mes)
 */
function setDefaultDates() {
    const today = new Date();
    const lastMonth = new Date(today);
    lastMonth.setMonth(lastMonth.getMonth() - 1);
    
    const startDateInput = document.getElementById('filterStartDate');
    const endDateInput = document.getElementById('filterEndDate');
    
    if (startDateInput && endDateInput) {
        startDateInput.value = formatDateForInput(lastMonth);
        endDateInput.value = formatDateForInput(today);
        
        currentFilters.startDate = formatDateForInput(lastMonth);
        currentFilters.endDate = formatDateForInput(today);
    }
}

/**
 * Formatea fecha para input type="date"
 */
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * Configura event listeners
 */
function setupEventListeners() {
    // Botón aplicar filtros
    const applyBtn = document.getElementById('applyFilters');
    if (applyBtn) {
        applyBtn.addEventListener('click', applyFilters);
    }
    
    // Botón restablecer filtros
    const resetBtn = document.getElementById('resetFilters');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }
    
    // Botones de ordenamiento de platos
    const sortByQuantityBtn = document.getElementById('sortByQuantity');
    const sortByRevenueBtn = document.getElementById('sortByRevenue');
    
    if (sortByQuantityBtn) {
        sortByQuantityBtn.addEventListener('click', () => {
            sortByQuantityBtn.classList.add('active');
            sortByRevenueBtn.classList.remove('active');
            loadTopPlatesData('quantity');
        });
    }
    
    if (sortByRevenueBtn) {
        sortByRevenueBtn.addEventListener('click', () => {
            sortByRevenueBtn.classList.add('active');
            sortByQuantityBtn.classList.remove('active');
            loadTopPlatesData('revenue');
        });
    }
}

/**
 * Aplica los filtros seleccionados
 */
function applyFilters() {
    const startDate = document.getElementById('filterStartDate').value;
    const endDate = document.getElementById('filterEndDate').value;
    const period = document.getElementById('filterPeriod').value;
    
    if (!startDate || !endDate) {
        showToast('Por favor seleccione ambas fechas', 'error');
        return;
    }
    
    if (new Date(startDate) > new Date(endDate)) {
        showToast('La fecha de inicio debe ser anterior a la fecha de fin', 'error');
        return;
    }
    
    currentFilters = { startDate, endDate, period };
    
    // Recargar todos los datos
    loadRevenueData();
    loadPeakDaysData();
    loadTopPlatesData('quantity');
    
    showToast('Filtros aplicados correctamente', 'success');
}

/**
 * Restablece los filtros a valores por defecto
 */
function resetFilters() {
    setDefaultDates();
    document.getElementById('filterPeriod').value = 'daily';
    currentFilters.period = 'daily';
    applyFilters();
}

/**
 * Carga el resumen de estadísticas
 */
async function loadStatisticsSummary() {
    try {
        const response = await fetch('/dashboard/statistics/summary');
        if (!response.ok) throw new Error('Error al cargar resumen');
        
        const data = await response.json();
        
        // Actualizar tarjetas
        document.getElementById('todayRevenue').textContent = formatCurrency(data.todayRevenue);
        document.getElementById('monthRevenue').textContent = formatCurrency(data.monthRevenue);
        document.getElementById('todayOrders').textContent = data.todayOrders;
        document.getElementById('monthOrders').textContent = data.monthOrders;
        document.getElementById('averageTicket').textContent = formatCurrency(data.averageTicket);
        document.getElementById('topPlate').textContent = data.topSellingPlate || '-';
    } catch (error) {
        console.error('Error cargando resumen:', error);
        showToast('Error al cargar resumen de estadísticas', 'error');
    }
}

/**
 * Carga datos de ingresos por período
 */
async function loadRevenueData() {
    try {
        const { startDate, endDate, period } = currentFilters;
        const endpoint = period === 'daily' ? 'daily' : 'monthly';
        
        const response = await fetch(
            `/dashboard/statistics/revenue/${endpoint}?startDate=${startDate}&endDate=${endDate}`
        );
        if (!response.ok) throw new Error('Error al cargar ingresos');
        
        const data = await response.json();
        
        // Actualizar gráfico
        updateRevenueChart(data, period);
    } catch (error) {
        console.error('Error cargando ingresos:', error);
        showToast('Error al cargar datos de ingresos', 'error');
    }
}

/**
 * Actualiza el gráfico de ingresos
 */
function updateRevenueChart(data, period) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;
    
    // Destruir gráfico anterior si existe
    if (revenueChart) {
        revenueChart.destroy();
    }
    
    const labels = data.map(item => {
        if (period === 'daily') {
            return formatDate(item.date);
        } else {
            return item.period; // "YYYY-MM"
        }
    });
    
    const revenues = data.map(item => item.totalRevenue);
    const orders = data.map(item => item.orderCount);
    
    revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Ingresos (S/)',
                    data: revenues,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    yAxisID: 'y',
                    tension: 0.1
                },
                {
                    label: 'Cantidad de Pedidos',
                    data: orders,
                    borderColor: 'rgb(255, 99, 132)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    yAxisID: 'y1',
                    tension: 0.1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            scales: {
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Ingresos (S/)'
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Cantidad de Pedidos'
                    },
                    grid: {
                        drawOnChartArea: false,
                    }
                }
            }
        }
    });
}

/**
 * Carga datos de días pico
 */
async function loadPeakDaysData() {
    try {
        const { startDate, endDate } = currentFilters;
        
        const response = await fetch(
            `/dashboard/statistics/peak-days?startDate=${startDate}&endDate=${endDate}&limit=10`
        );
        if (!response.ok) throw new Error('Error al cargar días pico');
        
        const data = await response.json();
        
        // Actualizar gráfico
        updatePeakDaysChart(data);
    } catch (error) {
        console.error('Error cargando días pico:', error);
        showToast('Error al cargar días con más ventas', 'error');
    }
}

/**
 * Actualiza el gráfico de días pico
 */
function updatePeakDaysChart(data) {
    const ctx = document.getElementById('peakDaysChart');
    if (!ctx) return;
    
    // Destruir gráfico anterior si existe
    if (peakDaysChart) {
        peakDaysChart.destroy();
    }
    
    const labels = data.map(item => formatDate(item.date));
    const quantities = data.map(item => item.orderCount);
    
    peakDaysChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Platos Vendidos',
                data: quantities,
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgb(54, 162, 235)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Cantidad de Platos'
                    }
                }
            }
        }
    });
}

/**
 * Carga datos de platos más vendidos
 */
async function loadTopPlatesData(sortBy) {
    try {
        const { startDate, endDate } = currentFilters;
        
        const response = await fetch(
            `/dashboard/statistics/top-plates/${sortBy}?startDate=${startDate}&endDate=${endDate}&limit=10`
        );
        if (!response.ok) throw new Error('Error al cargar platos');
        
        const data = await response.json();
        
        // Actualizar tabla
        updateTopPlatesTable(data);
    } catch (error) {
        console.error('Error cargando platos:', error);
        showToast('Error al cargar platos más vendidos', 'error');
    }
}

/**
 * Actualiza la tabla de platos más vendidos
 */
function updateTopPlatesTable(data) {
    const tbody = document.getElementById('topPlatesBody');
    if (!tbody) return;
    
    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="no-data">No hay datos disponibles</td></tr>';
        return;
    }
    
    tbody.innerHTML = data.map((plate, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${plate.plateName}</td>
            <td>${plate.categoryName}</td>
            <td>${plate.quantitySold}</td>
            <td>${formatCurrency(plate.totalRevenue)}</td>
            <td>${plate.percentageOfTotal.toFixed(2)}%</td>
        </tr>
    `).join('');
}

/**
 * Formatea moneda en formato peruano
 */
function formatCurrency(value) {
    if (value === null || value === undefined) return 'S/ 0.00';
    return `S/ ${parseFloat(value).toFixed(2)}`;
}

/**
 * Formatea fecha para visualización
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString + 'T00:00:00');
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

/**
 * Muestra un mensaje toast
 */
function showToast(message, type = 'info') {
    // Implementación simple de toast usando alert
    // En producción, se puede usar una librería de toast más sofisticada
    console.log(`[${type.toUpperCase()}] ${message}`);
    
    if (type === 'error') {
        alert('❌ ' + message);
    } else if (type === 'success') {
        alert('✅ ' + message);
    } else {
        alert('ℹ️ ' + message);
    }
}

/**
 * Limpieza al salir de la pestaña
 */
function cleanupEstadisticas() {
    if (revenueChart) {
        revenueChart.destroy();
        revenueChart = null;
    }
    if (peakDaysChart) {
        peakDaysChart.destroy();
        peakDaysChart = null;
    }
}

// Exportar funciones para uso global
window.initializeEstadisticas = initializeEstadisticas;
window.cleanupEstadisticas = cleanupEstadisticas;