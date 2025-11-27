let cajaInitialized = false;
let cajaOrders = [];
let cajaRefreshInterval = null;
let currentPaymentOrder = null;

function initCajaTabEvents() {
    const closeCancelModalBtn = document.getElementById('closeCancelModal');
    const cancelCancelBtn = document.getElementById('cancelCancelAction');
    const confirmCancelBtn = document.getElementById('confirmCancelAction');

    if (closeCancelModalBtn) closeCancelModalBtn.addEventListener('click', closeCancelModal);
    if (cancelCancelBtn) cancelCancelBtn.addEventListener('click', closeCancelModal);
    if (confirmCancelBtn) confirmCancelBtn.addEventListener('click', executeCancelOrder);

    const closePaymentModalBtn = document.getElementById('closePaymentModal');
    const cancelPaymentBtn = document.getElementById('cancelPayment');
    const confirmPaymentBtn = document.getElementById('confirmPayment');

    if (closePaymentModalBtn) closePaymentModalBtn.addEventListener('click', closePaymentModal);
    if (cancelPaymentBtn) cancelPaymentBtn.addEventListener('click', closePaymentModal);
    if (confirmPaymentBtn) confirmPaymentBtn.addEventListener('click', confirmPayment);

    const receiptTypeBoleta = document.getElementById('receiptTypeBoleta');
    const receiptTypeFactura = document.getElementById('receiptTypeFactura');

    if (receiptTypeBoleta) {
        receiptTypeBoleta.addEventListener('change', function () {
            if (this.checked) {
                document.getElementById('boletaFields').style.display = 'block';
                document.getElementById('facturaFields').style.display = 'none';
                clearAllErrors();
            }
        });
    }

    if (receiptTypeFactura) {
        receiptTypeFactura.addEventListener('change', function () {
            if (this.checked) {
                document.getElementById('boletaFields').style.display = 'none';
                document.getElementById('facturaFields').style.display = 'block';
                clearAllErrors();
            }
        });
    }

    const discountInput = document.getElementById('paymentDiscount');
    if (discountInput) {
        discountInput.addEventListener('input', updatePaymentSummary);
    }

    const closeSuccessModalBtn = document.getElementById('closeSuccessModal');
    const viewReceiptBtn = document.getElementById('viewReceipt');
    const printReceiptBtn = document.getElementById('printReceipt');

    if (closeSuccessModalBtn) closeSuccessModalBtn.addEventListener('click', closeSuccessModal);
    if (viewReceiptBtn) viewReceiptBtn.addEventListener('click', viewReceipt);
    if (printReceiptBtn) printReceiptBtn.addEventListener('click', printReceipt);

    loadCajaOrders();

    if (cajaRefreshInterval) {
        clearInterval(cajaRefreshInterval);
    }
    cajaRefreshInterval = setInterval(loadCajaOrders, 30000);

    cajaInitialized = true;
}

function loadCajaOrders() {
    const container = document.getElementById('cajaOrdersContainer');
    if (!container) return;

    fetch('/dashboard/orders?status=PENDIENTE,EN_PREPARACION,LISTO')
        .then(res => {
            if (!res.ok) {
                throw new Error('Error al cargar pedidos: ' + res.status);
            }
            return res.json();
        })
        .then(orders => {
            cajaOrders = orders;
            renderCajaOrders();
        })
        .catch(err => {
            console.error('Error cargando pedidos:', err);
            container.innerHTML = '<p style="color:red;">Error al cargar pedidos. Por favor, intente nuevamente.</p>';
        });
}

function renderCajaOrders() {
    const container = document.getElementById('cajaOrdersContainer');
    if (!container) return;

    if (!cajaOrders || cajaOrders.length === 0) {
        container.innerHTML = '<p>No hay pedidos pendientes de pago</p>';
        return;
    }

    container.innerHTML = cajaOrders.map(order => createCajaOrderCard(order)).join('');
}

function createCajaOrderCard(order) {
    const statusClass = `order-${order.status.toLowerCase().replace('_', '-')}`;
    const badgeClass = `badge-${order.status.toLowerCase().replace('_', '-')}`;
    const statusText = order.status.replace('_', ' ');

    const itemsHtml = (order.items || []).map(item => `
        <li>${item.quantity}x ${item.plate.name} - S/ ${item.priceUnit.toFixed(2)}
            ${item.notes ? `<br><small style="color:#666;">Nota: ${item.notes}</small>` : ''}
        </li>
    `).join('');

    const pagarButton = order.status === 'LISTO' ? `
        <button class="btn-pagar" onclick="openPaymentModal(${order.id})">
            Pagar
        </button>
    ` : '';

    return `
        <div class="order-card ${statusClass}">
            <div style="display:flex; justify-content:space-between; align-items:start;">
                <div>
                    <strong>Pedido #${order.id}</strong>
                    <span class="order-status-badge ${badgeClass}">${statusText}</span>
                    <div style="margin-top:4px; color:#666;">Mesa: ${order.tableNumber}</div>
                </div>
                <div style="text-align:right;">
                    <div style="font-size:0.9em; color:#666;">${order.orderDate || order.dateAndTimeOrder || ''}</div>
                </div>
            </div>
            <div style="margin-top:8px;">
                <strong>Items:</strong>
                <ul style="margin:4px 0; padding-left:20px;">
                    ${itemsHtml}
                </ul>
            </div>
            <div class="order-total">
                Total: S/ ${(order.priceTotal || order.totalPrice || 0).toFixed(2)}
            </div>
            <div class="order-actions">
                ${pagarButton}
                <button class="btn-cancelar" onclick="confirmCancelOrder(${order.id})">
                    Cancelar Pedido
                </button>
            </div>
        </div>
    `;
}

function openPaymentModal(orderId) {
    const order = cajaOrders.find(o => o.id === orderId);
    if (!order) {
        alert('No se encontró el pedido');
        return;
    }

    if (order.status !== 'LISTO') {
        alert('Solo se pueden procesar pagos de pedidos en estado LISTO');
        return;
    }

    currentPaymentOrder = order;

    document.getElementById('paymentOrderId').value = orderId;
    document.getElementById('paymentTableNumber').textContent = order.tableNumber;
    document.getElementById('paymentOrderTotal').textContent = (order.priceTotal || order.totalPrice || 0).toFixed(2);

    resetPaymentForm();
    updatePaymentSummary();

    document.getElementById('paymentModal').style.display = 'flex';
}

function resetPaymentForm() {
    document.getElementById('receiptTypeBoleta').checked = true;
    document.getElementById('receiptTypeFactura').checked = false;

    document.getElementById('customerDni').value = '';
    document.getElementById('customerName').value = '';
    document.getElementById('customerRuc').value = '';
    document.getElementById('customerRazonSocial').value = '';
    document.getElementById('paymentDiscount').value = '0';

    document.getElementById('boletaFields').style.display = 'block';
    document.getElementById('facturaFields').style.display = 'none';

    clearAllErrors();
}

function updatePaymentSummary() {
    if (!currentPaymentOrder) return;

    const totalPedido = currentPaymentOrder.priceTotal || currentPaymentOrder.totalPrice || 0;
    const descuento = parseFloat(document.getElementById('paymentDiscount').value) || 0;

    const totalConDescuento = totalPedido - descuento;
    const subtotal = totalConDescuento / 1.18;
    const igv = totalConDescuento - subtotal;

    document.getElementById('summarySubtotal').textContent = subtotal.toFixed(2);
    document.getElementById('summaryIgv').textContent = igv.toFixed(2);
    document.getElementById('summaryDiscount').textContent = descuento.toFixed(2);
    document.getElementById('summaryTotal').textContent = totalConDescuento.toFixed(2);
}

function validatePaymentForm() {
    clearAllErrors();

    const descuento = parseFloat(document.getElementById('paymentDiscount').value) || 0;
    const totalPedido = currentPaymentOrder.priceTotal || currentPaymentOrder.totalPrice || 0;

    if (descuento < 0) {
        showFieldError('discountError', 'El descuento no puede ser negativo');
        return false;
    }

    if (descuento > totalPedido) {
        showFieldError('discountError', 'El descuento no puede ser mayor que el total del pedido');
        return false;
    }

    const decimalPart = descuento.toString().split('.')[1];
    if (decimalPart && decimalPart.length > 2) {
        showFieldError('discountError', 'El descuento debe tener máximo 2 decimales');
        return false;
    }

    const receiptType = document.querySelector('input[name="receiptType"]:checked').value;

    if (receiptType === 'FACTURA') {
        const ruc = document.getElementById('customerRuc').value.trim();
        const razonSocial = document.getElementById('customerRazonSocial').value.trim();

        if (!ruc) {
            showFieldError('rucError', 'El RUC es obligatorio para facturas');
            return false;
        }

        if (!/^\d{11}$/.test(ruc)) {
            showFieldError('rucError', 'El RUC debe tener exactamente 11 dígitos');
            return false;
        }

        if (!razonSocial) {
            showFieldError('razonSocialError', 'La razón social es obligatoria para facturas');
            return false;
        }

        if (razonSocial.length > 120) {
            showFieldError('razonSocialError', 'La razón social no puede exceder 120 caracteres');
            return false;
        }
    } else if (receiptType === 'BOLETA') {
        const dni = document.getElementById('customerDni').value.trim();
        const nombre = document.getElementById('customerName').value.trim();

        if (dni && !/^\d{8}$/.test(dni)) {
            showFieldError('dniError', 'El DNI debe tener exactamente 8 dígitos');
            return false;
        }

        if (dni && !nombre) {
            showFieldError('nameError', 'Si proporciona DNI, el nombre es obligatorio');
            return false;
        }

        if (nombre && nombre.length > 120) {
            showFieldError('nameError', 'El nombre no puede exceder 120 caracteres');
            return false;
        }
    }

    return true;
}

function confirmPayment() {
    if (!validatePaymentForm()) {
        return;
    }

    const orderId = parseInt(document.getElementById('paymentOrderId').value);
    const receiptType = document.querySelector('input[name="receiptType"]:checked').value;
    const descuento = parseFloat(document.getElementById('paymentDiscount').value) || 0;

    let requestData = {
        receiptType: receiptType,
        discount: descuento
    };

    if (receiptType === 'FACTURA') {
        requestData.ruc = document.getElementById('customerRuc').value.trim();
        requestData.customerName = document.getElementById('customerRazonSocial').value.trim();
    } else if (receiptType === 'BOLETA') {
        const dni = document.getElementById('customerDni').value.trim();
        const nombre = document.getElementById('customerName').value.trim();

        if (dni) {
            requestData.dni = dni;
        }
        if (nombre) {
            requestData.customerName = nombre;
        }
    }

    const confirmBtn = document.getElementById('confirmPayment');
    confirmBtn.disabled = true;
    confirmBtn.textContent = 'Procesando...';

    fetch(`/dashboard/receipt/${orderId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(text || 'Error al procesar el pago');
                });
            }
            return res.json();
        })
        .then(receipt => {
            closePaymentModal();
            showPaymentSuccess(receipt);
            loadCajaOrders();
        })
        .catch(err => {
            console.error('Error al procesar pago:', err);
            showCajaMessage('Error: ' + err.message, 'error');
        })
        .finally(() => {
            confirmBtn.disabled = false;
            confirmBtn.textContent = 'Confirmar Pago';
        });
}

function showPaymentSuccess(receipt) {
    document.getElementById('successOrderId').textContent = receipt.orderId;
    document.getElementById('successReceiptId').textContent = receipt.receiptId;
    document.getElementById('successReceiptType').textContent = receipt.receiptType;
    document.getElementById('successCustomerName').textContent = receipt.customerName || 'Cliente General';
    document.getElementById('successTotal').textContent = receipt.total.toFixed(2);

    document.getElementById('paymentSuccessModal').style.display = 'flex';
}

function closePaymentModal() {
    document.getElementById('paymentModal').style.display = 'none';
    currentPaymentOrder = null;
}

function closeSuccessModal() {
    document.getElementById('paymentSuccessModal').style.display = 'none';
}

async function viewReceipt() {
    try {
        // Obtener el orderId del modal de éxito
        const orderId = document.getElementById('successOrderId').textContent;
        
        // Obtener datos completos del recibo
        const response = await fetch(`/dashboard/receipt/${orderId}/print`);
        if (!response.ok) {
            throw new Error('Error al obtener datos del recibo');
        }
        const receiptData = await response.json();
        
        // Poblar el HTML del comprobante
        populateReceiptData(receiptData);
        
        // Mostrar el contenedor para vista previa (sin imprimir)
        const container = document.getElementById('receiptPrintContainer');
        container.style.display = 'block';
        container.style.position = 'fixed';
        container.style.top = '50%';
        container.style.left = '50%';
        container.style.transform = 'translate(-50%, -50%)';
        container.style.backgroundColor = 'white';
        container.style.padding = '20px';
        container.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
        container.style.zIndex = '10000';
        container.style.maxHeight = '80vh';
        container.style.overflowY = 'auto';
        container.style.border = '1px solid #ddd';
        
        // Agregar botón de cerrar si no existe
        let closeBtn = container.querySelector('.close-preview-btn');
        if (!closeBtn) {
            closeBtn = document.createElement('button');
            closeBtn.className = 'close-preview-btn';
            closeBtn.innerHTML = '&times;';
            closeBtn.style.position = 'absolute';
            closeBtn.style.top = '10px';
            closeBtn.style.right = '10px';
            closeBtn.style.fontSize = '24px';
            closeBtn.style.border = 'none';
            closeBtn.style.background = 'none';
            closeBtn.style.cursor = 'pointer';
            closeBtn.style.color = '#666';
            closeBtn.onclick = function() {
                container.style.display = 'none';
                container.style.position = '';
                container.style.top = '';
                container.style.left = '';
                container.style.transform = '';
                container.style.backgroundColor = '';
                container.style.padding = '';
                container.style.boxShadow = '';
                container.style.zIndex = '';
                container.style.maxHeight = '';
                container.style.overflowY = '';
                container.style.border = '';
            };
            container.insertBefore(closeBtn, container.firstChild);
        }
        
    } catch (error) {
        console.error('Error al ver recibo:', error);
        alert('Error al cargar el recibo. Por favor, intente nuevamente.');
    }
}

async function printReceipt() {
    try {
        // 1. Obtener el orderId del modal de éxito
        const orderId = document.getElementById('successOrderId').textContent;
        
        // 2. Obtener datos completos del recibo
        const response = await fetch(`/dashboard/receipt/${orderId}/print`);
        if (!response.ok) {
            throw new Error('Error al obtener datos del recibo');
        }
        const receiptData = await response.json();
        
        // 3. Poblar el HTML del comprobante
        populateReceiptData(receiptData);
        
        // 4. Mostrar el contenedor y abrir diálogo de impresión
        const container = document.getElementById('receiptPrintContainer');
        container.style.display = 'block';
        
        // Esperar un momento para que el DOM se actualice
        setTimeout(() => {
            window.print();
            // Ocultar el contenedor después de imprimir
            container.style.display = 'none';
        }, 100);
        
    } catch (error) {
        console.error('Error al imprimir:', error);
        alert('Error al generar el comprobante. Por favor, intente nuevamente.');
    }
}

function populateReceiptData(data) {
    // Tipo de comprobante y número
    document.getElementById('receipt-type').textContent =
        data.receiptType === 'FACTURA' ? 'FACTURA' : 'BOLETA DE VENTA';
    document.getElementById('receipt-id').textContent =
        String(data.receiptId).padStart(8, '0');
    
    // Información del cliente
    document.getElementById('customer-name').textContent = data.customerName || 'Cliente General';
    
    const customerDoc = document.getElementById('customer-document');
    if (data.receiptType === 'FACTURA' && data.ruc) {
        customerDoc.innerHTML = `<strong>RUC:</strong> <span>${data.ruc}</span>`;
        customerDoc.style.display = 'block';
    } else if (data.dni) {
        customerDoc.innerHTML = `<strong>DNI:</strong> <span>${data.dni}</span>`;
        customerDoc.style.display = 'block';
    } else {
        customerDoc.style.display = 'none';
    }
    
    // Información del pedido
    document.getElementById('order-id').textContent = data.orderId;
    document.getElementById('table-number').textContent = data.tableNumber;
    document.getElementById('waiter-name').textContent = data.waiterName;
    
    // Formatear fecha
    const orderDate = new Date(data.orderDate);
    const formattedDate = `${orderDate.getDate().toString().padStart(2, '0')}/${
        (orderDate.getMonth() + 1).toString().padStart(2, '0')}/${
        orderDate.getFullYear()} ${
        orderDate.getHours().toString().padStart(2, '0')}:${
        orderDate.getMinutes().toString().padStart(2, '0')}`;
    document.getElementById('order-date').textContent = formattedDate;
    
    // Poblar items
    const itemsBody = document.getElementById('receipt-items-body');
    itemsBody.innerHTML = '';
    
    data.items.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.quantity}</td>
            <td>
                ${item.plate.name}
                ${item.notes ? `<br><small>${item.notes}</small>` : ''}
            </td>
            <td>S/ ${item.priceUnit.toFixed(2)}</td>
            <td>S/ ${item.totalPrice.toFixed(2)}</td>
        `;
        itemsBody.appendChild(row);
    });
    
    // Mostrar descuento si existe
    const discountSection = document.getElementById('receipt-discount-section');
    if (data.discount && data.discount > 0) {
        discountSection.style.display = 'block';
        document.getElementById('receipt-discount').textContent =
            `S/ ${data.discount.toFixed(2)}`;
    } else {
        discountSection.style.display = 'none';
    }
    
    // Totales
    document.getElementById('receipt-subtotal').textContent = `S/ ${data.subtotal.toFixed(2)}`;
    document.getElementById('receipt-igv').textContent = `S/ ${data.igv.toFixed(2)}`;
    document.getElementById('receipt-total').textContent = `S/ ${data.total.toFixed(2)}`;
}

function confirmCancelOrder(orderId) {
    const order = cajaOrders.find(o => o.id === orderId);
    if (!order) {
        alert('No se encontró el pedido');
        return;
    }

    document.getElementById('cancelOrderId').value = orderId;

    const totalPrice = order.priceTotal || order.totalPrice || 0;
    document.getElementById('cancelMessage').innerHTML = `¿Está seguro que desea cancelar el pedido #${orderId} de la mesa ${order.tableNumber}?<br><strong>Total: S/ ${totalPrice.toFixed(2)}</strong>`;
    document.getElementById('confirmCancelModal').style.display = 'flex';
}

function executeCancelOrder() {
    const orderId = document.getElementById('cancelOrderId').value;

    if (!orderId) {
        alert('Datos incompletos');
        return;
    }

    const confirmBtn = document.getElementById('confirmCancelAction');
    confirmBtn.disabled = true;
    confirmBtn.textContent = 'Procesando...';

    const requestData = {
        orderId: parseInt(orderId),
        status: 'CANCELADO'
    };

    fetch('/dashboard/receipt/change-status', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(text || 'Error al cancelar el pedido');
                });
            }
            return res.json();
        })
        .then(() => {
            closeCancelModal();
            loadCajaOrders();
            showCajaMessage('Pedido cancelado exitosamente', 'success');
        })
        .catch(err => {
            console.error('Error al cancelar pedido:', err);
            showCajaMessage('Error: ' + err.message, 'error');
        })
        .finally(() => {
            confirmBtn.disabled = false;
            confirmBtn.textContent = 'Confirmar';
        });
}

function closeCancelModal() {
    document.getElementById('confirmCancelModal').style.display = 'none';
}


function showCajaMessage(message, type = 'info') {
    if (typeof showToast === 'function') {
        showToast(message, type);
    } else {
        alert(message);
    }
}

function cleanupCaja() {
    if (cajaRefreshInterval) {
        clearInterval(cajaRefreshInterval);
        cajaRefreshInterval = null;
    }
    cajaInitialized = false;
}

function initializeCaja() {
    if (!cajaInitialized) {
        initCajaTabEvents();
    }
}

window.openPaymentModal = openPaymentModal;
window.confirmCancelOrder = confirmCancelOrder;

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
        if (!cajaInitialized) {
            initializeCaja();
        }
    });
} else {
    if (!cajaInitialized) {
        initializeCaja();
    }
}