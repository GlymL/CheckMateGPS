document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('confetti');
    
    // Solo ejecutamos el confeti si el contenedor existe en la página actual
    if (container) {
        const colors = ['#e060a0', '#f090c0', '#9b59d0', '#ffffff', '#c070e0', '#ff80b0'];
        
        for (let i = 0; i < 55; i++) {
            const el = document.createElement('div');
            el.className = 'confetti';
            el.style.cssText = `
                left: ${Math.random() * 100}%; 
                background: ${colors[Math.floor(Math.random() * colors.length)]};
                width: ${6 + Math.random() * 8}px; 
                height: ${6 + Math.random() * 8}px;
                border-radius: ${Math.random() > 0.5 ? '50%' : '2px'};
                animation-duration: ${2.5 + Math.random() * 2.5}s; 
                animation-delay: ${Math.random() * 1.2}s;
            `;
            container.appendChild(el);
        }
    }
});