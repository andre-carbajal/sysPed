function initPersonalTabEvents() {
    const currentRolInput = document.getElementById('currentRol');
    const currentRol = currentRolInput ? currentRolInput.value.toUpperCase() : '';

    const staffEditModal = document.getElementById('staffEditModal');
    const closeStaffEditModal = document.getElementById('closeStaffEditModal');
    const cancelarStaffEdit = document.getElementById('cancelarStaffEdit');
    const staffEditForm = document.getElementById('staffEditForm');

    const personalTbody = document.getElementById('personalTbody');
    if (personalTbody) {
        personalTbody.addEventListener('click', function(e) {
            const btn = e.target.closest('.btn-modificar-staff');
            if (!btn) return;
            const staffRol = btn.getAttribute('data-rol').toUpperCase();

            document.getElementById('editDni').value = btn.getAttribute('data-dni');
            document.getElementById('editName').value = btn.getAttribute('data-name');
            const rolValue = btn.getAttribute('data-rol');
            const rolSelect = document.getElementById('editRol');
            const rolReadonly = document.getElementById('editRolReadonly');
            const rolHidden = document.getElementById('editRolHidden');

            if (rolSelect) {
                for (let i = 0; i < rolSelect.options.length; i++) {
                    const option = rolSelect.options[i];
                    const optionValue = option.value.toUpperCase();
                    if (currentRol === 'ADMINISTRADOR' && (optionValue === 'JEFE' || optionValue === 'ADMINISTRADOR')) {
                        option.style.display = 'none';
                    } else {
                        option.style.display = '';
                    }
                }
            }

            let soloLectura = false;
            if ((currentRol === 'JEFE' && staffRol === 'JEFE') ||
                (currentRol === 'ADMINISTRADOR' && (staffRol === 'JEFE' || staffRol === 'ADMINISTRADOR'))) {
                soloLectura = true;
            }

            if (soloLectura) {
                rolSelect.style.display = 'none';
                rolReadonly.style.display = '';
                rolReadonly.value = rolValue;
                rolHidden.value = rolValue;
            } else {
                rolSelect.style.display = '';
                rolReadonly.style.display = 'none';
                rolHidden.value = '';
                if (rolSelect) {
                    rolSelect.value = rolValue;
                }
            }
            document.getElementById('editPassword').value = '';
            staffEditModal.style.display = 'flex';
        });
    }

    if (closeStaffEditModal && cancelarStaffEdit) {
        closeStaffEditModal.onclick = cancelarStaffEdit.onclick = function() {
            staffEditModal.style.display = 'none';
        };
    }

    if (staffEditForm) {
        staffEditForm.onsubmit = function(e) {
            e.preventDefault();
            this.submit();
        };
    }

    const btnAgregarPersonal = document.getElementById('btnAgregarPersonal');
    const staffCreateModal = document.getElementById('staffCreateModal');
    const createDni = document.getElementById('createDni');
    const createName = document.getElementById('createName');
    const createRol = document.getElementById('createRol');
    const createPassword = document.getElementById('createPassword');
    const editDni = document.getElementById('editDni');

    function validarDniInput(input) {
        input.value = input.value.replace(/[^0-9]/g, '').slice(0, 8);
    }

    if (createDni) {
        createDni.addEventListener('input', function() {
            validarDniInput(createDni);
        });
    }
    if (editDni) {
        editDni.addEventListener('input', function() {
            validarDniInput(editDni);
        });
    }

    if (document.getElementById('staffCreateForm')) {
        document.getElementById('staffCreateForm').addEventListener('submit', function(e) {
            if (!/^\d{8}$/.test(createDni.value)) {
                alert('El DNI debe contener exactamente 8 dígitos numéricos.');
                createDni.focus();
                e.preventDefault();
            }
        });
    }
    if (document.getElementById('staffEditForm')) {
        document.getElementById('staffEditForm').addEventListener('submit', function(e) {
            if (!/^\d{8}$/.test(editDni.value)) {
                alert('El DNI debe contener exactamente 8 dígitos numéricos.');
                editDni.focus();
                e.preventDefault();
            }
        });
    }

    if (btnAgregarPersonal) {
        btnAgregarPersonal.addEventListener('click', () => {
            createDni.value = '';
            createName.value = '';
            createRol.value = '';
            createPassword.value = '';
            staffCreateModal.style.display = 'flex';
        });
    }

    const closeStaffCreateModal = document.getElementById('closeStaffCreateModal');
    if (closeStaffCreateModal) {
        closeStaffCreateModal.addEventListener('click', () => {
            staffCreateModal.style.display = 'none';
        });
    }

    const cancelarStaffCreate = document.getElementById('cancelarStaffCreate');
    if (cancelarStaffCreate) {
        cancelarStaffCreate.addEventListener('click', () => {
            staffCreateModal.style.display = 'none';
        });
    }
}
