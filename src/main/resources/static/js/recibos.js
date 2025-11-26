let receiptsInitialized = false;
let allReceipts = [];
let filteredReceipts = [];
let currentPage = 1;
const receiptsPerPage = 10;
let currentReceiptDetail = null;

function initRecibosTabEvents() {
    if (receiptsInitialized) {
        console.log('Recibos tab already initialized');
        return;
    }
    receiptsInitialized = true;
    console.log('Initializing Recibos Tab...');

    // Event listeners para filtros
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const clearFiltersBtn = document.getElementById('clearFiltersBtn');

    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', applyFilters);
    }

    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', clearFilters);
    }

    // Event listeners para modal de detalle
    const closeReceiptDetailModal = document.getElementById('closeReceiptDetailModal');
    const closeReceiptDetail = document.getElementById('closeReceiptDetail');
    const printReceiptDetail = document.getElementById('printReceiptDetail');

    if (closeReceiptDetailModal) {
        closeReceiptDetailModal.addEventListener('click', closeReceiptDetailModalFunc);
    }

    if (closeReceiptDetail) {
        closeReceiptDetail.addEventListener('click', closeReceiptDetailModalFunc);
    }

    if (printReceiptDetail) {
        printReceiptDetail.addEventListener('click', printCurrentReceipt);
    }

    // Cargar recibos
    loadReceipts();
}

function loadReceipts() {
    fetch('/dashboard/receipt/all')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar recibos');
            }
            return response.json();
        })
        .then(data => {
            allReceipts = data;
            filteredReceipts = [...allReceipts];
            currentPage = 1;
            renderReceiptsTable();
            renderPagination();
        })
        .catch(error => {
            console.error('Error loading receipts:', error);
            const tbody = document.getElementById('receiptsTableBody');
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: red;">Error al cargar recibos</td></tr>';
            }
        });
}

function applyFilters() {
    const filterType = document.getElementById('filterReceiptType').value;
    const filterDateFrom = document.getElementById('filterDateFrom').value;
    const filterDateTo = document.getElementById('filterDateTo').value;
    const filterCustomer = document.getElementById('filterCustomer').value.toLowerCase();

    filteredReceipts = allReceipts.filter(receipt => {
        // Filtro por tipo
        if (filterType && receipt.receiptType !== filterType) {
            return false;
        }

        // Filtro por fecha desde
        if (filterDateFrom) {
            const receiptDate = new Date(receipt.orderDate);
            const fromDate = new Date(filterDateFrom);
            if (receiptDate < fromDate) {
                return false;
            }
        }

        // Filtro por fecha hasta
        if (filterDateTo) {
            const receiptDate = new Date(receipt.orderDate);
            const toDate = new Date(filterDateTo);
            toDate.setHours(23, 59, 59, 999); // Incluir todo el día
            if (receiptDate > toDate) {
                return false;
            }
        }

        // Filtro por cliente
        if (filterCustomer) {
            const customerName = receipt.customerName ? receipt.customerName.toLowerCase() : '';
            const dni = receipt.dni ? receipt.dni : '';
            const ruc = receipt.ruc ? receipt.ruc : '';
            if (!customerName.includes(filterCustomer) && !dni.includes(filterCustomer) && !ruc.includes(filterCustomer)) {
                return false;
            }
        }

        return true;
    });

    currentPage = 1;
    renderReceiptsTable();
    renderPagination();
}

function clearFilters() {
    document.getElementById('filterReceiptType').value = '';
    document.getElementById('filterDateFrom').value = '';
    document.getElementById('filterDateTo').value = '';
    document.getElementById('filterCustomer').value = '';

    filteredReceipts = [...allReceipts];
    currentPage = 1;
    renderReceiptsTable();
    renderPagination();
}

function renderReceiptsTable() {
    const tbody = document.getElementById('receiptsTableBody');
    if (!tbody) return;

    if (filteredReceipts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center;">No se encontraron recibos</td></tr>';
        return;
    }

    const startIndex = (currentPage - 1) * receiptsPerPage;
    const endIndex = startIndex + receiptsPerPage;
    const pageReceipts = filteredReceipts.slice(startIndex, endIndex);

    tbody.innerHTML = pageReceipts.map(receipt => {
        const receiptDate = new Date(receipt.orderDate);
        const formattedDate = receiptDate.toLocaleDateString('es-PE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        const customerDoc = receipt.receiptType === 'FACTURA'
            ? (receipt.ruc || '-')
            : (receipt.dni || '-');

        const customerName = receipt.customerName || 'Cliente General';

        const receiptTypeBadge = receipt.receiptType === 'FACTURA'
            ? '<span class="badge badge-factura">Factura</span>'
            : '<span class="badge badge-boleta">Boleta</span>';

        return `
            <tr>
                <td>${receipt.receiptId}</td>
                <td>${formattedDate}</td>
                <td>${receiptTypeBadge}</td>
                <td>${customerName}</td>
                <td>${customerDoc}</td>
                <td>${receipt.tableNumber}</td>
                <td>S/ ${receipt.total.toFixed(2)}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="viewReceiptDetail(${receipt.orderId})">Ver</button>
                    <button class="btn btn-sm btn-secondary" onclick="printReceipt(${receipt.orderId})">Imprimir</button>
                </td>
            </tr>
        `;
    }).join('');
}

function renderPagination() {
    const container = document.getElementById('paginationContainer');
    if (!container) return;

    const totalPages = Math.ceil(filteredReceipts.length / receiptsPerPage);

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let paginationHTML = '<div class="pagination">';

    // Botón anterior
    if (currentPage > 1) {
        paginationHTML += `<button class="btn btn-sm" onclick="changePage(${currentPage - 1})">Anterior</button>`;
    }

    // Números de página
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHTML += `<button class="btn btn-sm btn-primary">${i}</button>`;
        } else if (i === 1 || i === totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
            paginationHTML += `<button class="btn btn-sm" onclick="changePage(${i})">${i}</button>`;
        } else if (i === currentPage - 2 || i === currentPage + 2) {
            paginationHTML += '<span>...</span>';
        }
    }

    // Botón siguiente
    if (currentPage < totalPages) {
        paginationHTML += `<button class="btn btn-sm" onclick="changePage(${currentPage + 1})">Siguiente</button>`;
    }

    paginationHTML += '</div>';
    container.innerHTML = paginationHTML;
}

function changePage(page) {
    currentPage = page;
    renderReceiptsTable();
    renderPagination();
}

function viewReceiptDetail(orderId) {
    fetch(`/dashboard/receipt/${orderId}/print`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar detalle del recibo');
            }
            return response.json();
        })
        .then(data => {
            currentReceiptDetail = data;
            populateReceiptDetailModal(data);
            document.getElementById('receiptDetailModal').classList.add('active');
        })
        .catch(error => {
            console.error('Error loading receipt detail:', error);
            alert('Error al cargar el detalle del recibo');
        });
}

function populateReceiptDetailModal(receipt) {
    document.getElementById('detailReceiptId').textContent = receipt.receiptId;

    const typeBadge = document.getElementById('detailReceiptTypeBadge');
    typeBadge.textContent = receipt.receiptType;
    typeBadge.className = 'receipt-type-badge badge-' + receipt.receiptType.toLowerCase();

    document.getElementById('detailCustomerName').textContent = receipt.customerName || 'Cliente General';

    const docLabel = receipt.receiptType === 'FACTURA' ? 'RUC' : 'DNI';
    const docValue = receipt.receiptType === 'FACTURA' ? (receipt.ruc || '-') : (receipt.dni || '-');
    document.getElementById('detailCustomerDoc').innerHTML = `<strong>${docLabel}:</strong> <span id="detailCustomerDni">${docValue}</span>`;

    document.getElementById('detailOrderId').textContent = receipt.orderId;
    document.getElementById('detailTableNumber').textContent = receipt.tableNumber;

    const orderDate = new Date(receipt.orderDate);
    document.getElementById('detailOrderDate').textContent = orderDate.toLocaleString('es-PE');

    document.getElementById('detailWaiterName').textContent = receipt.waiterName;

    // Items
    const itemsBody = document.getElementById('detailItemsBody');
    itemsBody.innerHTML = receipt.items.map(item => `
        <tr>
            <td>${item.quantity}</td>
            <td>${item.plate.name}${item.notes ? '<br><small>' + item.notes + '</small>' : ''}</td>
            <td>S/ ${item.priceUnit.toFixed(2)}</td>
            <td>S/ ${item.totalPrice.toFixed(2)}</td>
        </tr>
    `).join('');

    // Totales
    if (receipt.discount && receipt.discount > 0) {
        document.getElementById('detailDiscountLine').style.display = 'flex';
        document.getElementById('detailDiscount').textContent = receipt.discount.toFixed(2);
    } else {
        document.getElementById('detailDiscountLine').style.display = 'none';
    }

    document.getElementById('detailSubtotal').textContent = receipt.subtotal.toFixed(2);
    document.getElementById('detailIgv').textContent = receipt.igv.toFixed(2);
    document.getElementById('detailTotal').textContent = receipt.total.toFixed(2);
}

function closeReceiptDetailModalFunc() {
    document.getElementById('receiptDetailModal').classList.remove('active');
    currentReceiptDetail = null;
}

function printCurrentReceipt() {
    if (currentReceiptDetail) {
        populatePrintContainer(currentReceiptDetail);
        window.print();
    }
}

function printReceipt(orderId) {
    fetch(`/dashboard/receipt/${orderId}/print`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar recibo para imprimir');
            }
            return response.json();
        })
        .then(data => {
            populatePrintContainer(data);
            window.print();
        })
        .catch(error => {
            console.error('Error printing receipt:', error);
            alert('Error al imprimir el recibo');
        });
}

function populatePrintContainer(receipt) {
    // Tipo de comprobante
    document.getElementById('receipt-type').textContent =
        receipt.receiptType === 'FACTURA' ? 'FACTURA DE VENTA' : 'BOLETA DE VENTA';

    // Formato del ID del recibo
    const receiptIdFormatted = String(receipt.receiptId).padStart(6, '0');
    const serie = receipt.receiptType === 'FACTURA' ? 'F001' : 'B001';
    document.getElementById('receipt-id').textContent = `${serie}-${receiptIdFormatted}`;

    // Cliente
    document.getElementById('customer-name').textContent = receipt.customerName || 'Cliente General';

    const docElement = document.getElementById('customer-document');
    if (receipt.receiptType === 'FACTURA') {
        docElement.innerHTML = `<strong>RUC:</strong> <span id="customer-dni">${receipt.ruc || '-'}</span>`;
    } else {
        docElement.innerHTML = `<strong>DNI:</strong> <span id="customer-dni">${receipt.dni || '-'}</span>`;
    }

    // Info del pedido
    document.getElementById('order-id').textContent = receipt.orderId;
    document.getElementById('table-number').textContent = receipt.tableNumber;

    const orderDate = new Date(receipt.orderDate);
    document.getElementById('order-date').textContent = orderDate.toLocaleString('es-PE');

    document.getElementById('waiter-name').textContent = receipt.waiterName;

    // Items
    const itemsBody = document.getElementById('receipt-items-body');
    itemsBody.innerHTML = receipt.items.map(item => `
        <tr>
            <td>${item.quantity}</td>
            <td>${item.plate.name}</td>
            <td>S/ ${item.priceUnit.toFixed(2)}</td>
            <td>S/ ${item.totalPrice.toFixed(2)}</td>
        </tr>
    `).join('');

    // Descuento
    const discountSection = document.getElementById('receipt-discount-section');
    if (receipt.discount && receipt.discount > 0) {
        discountSection.style.display = 'block';
        document.getElementById('receipt-discount').textContent = `S/ ${receipt.discount.toFixed(2)}`;
    } else {
        discountSection.style.display = 'none';
    }

    // Totales
    document.getElementById('receipt-subtotal').textContent = `S/ ${receipt.subtotal.toFixed(2)}`;
    document.getElementById('receipt-igv').textContent = `S/ ${receipt.igv.toFixed(2)}`;
    document.getElementById('receipt-total').textContent = `S/ ${receipt.total.toFixed(2)}`;
}

function cleanupRecibos() {
    receiptsInitialized = false;
    allReceipts = [];
    filteredReceipts = [];
    currentPage = 1;
    currentReceiptDetail = null;
    console.log('Recibos tab cleaned up');
}

