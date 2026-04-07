function openMenu(){
    document.getElementById("houseMenu").style.display="flex";
}

function closeMenu(){
    document.getElementById("houseMenu").style.display="none";
}

function validarDescripcion(textarea) {
    var regex = /^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:'"()\-\n\r]+$/;
    if (textarea.value === '') {
        textarea.setCustomValidity('Por favor, rellena este campo.');
    } else if (!regex.test(textarea.value)) {
        textarea.setCustomValidity('El formato de la descripción es incorrecto.');
    } else {
        textarea.setCustomValidity(''); 
    }
}
