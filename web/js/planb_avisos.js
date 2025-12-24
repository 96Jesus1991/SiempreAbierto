/**
 * planb_avisos.js
 * Lógica para el tablón de avisos "tirado en carretera"
 */

// Configuración - CAMBIAR ESTA URL A TU DOMINIO
const API_BASE = 'https://buscandoadios-espana.com/api';

// Estado
let avisoCreado = null;

// =============================================
// INICIALIZACIÓN
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    cargarAvisos();
    
    // Recargar cada minuto
    setInterval(cargarAvisos, 60000);
});

// =============================================
// CARGAR AVISOS
// =============================================
async function cargarAvisos() {
    const lista = document.getElementById('lista-avisos');
    const sinAvisos = document.getElementById('sin-avisos');
    const loading = document.getElementById('loading-avisos');
    
    if (loading) loading.style.display = 'block';
    
    try {
        const response = await fetch(`${API_BASE}/planb_avisos.php`);
        const data = await response.json();
        
        if (loading) loading.style.display = 'none';
        
        if (!data.success) {
            throw new Error(data.error || 'Error al cargar avisos');
        }
        
        const avisos = data.avisos || [];
        
        if (avisos.length === 0) {
            lista.innerHTML = '';
            sinAvisos.style.display = 'block';
            return;
        }
        
        sinAvisos.style.display = 'none';
        lista.innerHTML = avisos.map(a => renderAviso(a)).join('');
        
    } catch (error) {
        console.error('Error:', error);
        if (loading) loading.style.display = 'none';
        lista.innerHTML = `<p style="text-align:center;color:#c00;">Error al cargar avisos</p>`;
    }
}

// =============================================
// RENDERIZAR AVISO
// =============================================
function renderAviso(aviso) {
    const tipoTexto = {
        'tirado': 'Tirado/Avería',
        'corte': 'Corte de carretera',
        'trafico': 'Tráfico',
        'peligro': 'Peligro',
        'otro': 'Aviso'
    };
    
    return `
        <div class="aviso-card" data-id="${aviso.id}">
            <div class="aviso-header">
                <div class="aviso-tipo">
                    <span class="aviso-tipo-icon">${aviso.tipo_emoji}</span>
                    <span class="aviso-tipo-text">${tipoTexto[aviso.tipo] || aviso.tipo}</span>
                </div>
                <span class="aviso-tiempo">${aviso.hace} · Caduca en ${aviso.caduca_en}</span>
            </div>
            
            <p class="aviso-zona">📍 ${escapeHtml(aviso.zona)}</p>
            
            ${aviso.necesidad ? `<p class="aviso-necesidad">🔧 ${escapeHtml(aviso.necesidad)}</p>` : ''}
            ${aviso.descripcion ? `<p class="aviso-descripcion">${escapeHtml(aviso.descripcion)}</p>` : ''}
            
            <div class="aviso-footer">
                <span class="aviso-ayudantes">
                    ${aviso.ayudantes > 0 ? `✅ ${aviso.ayudantes} persona${aviso.ayudantes > 1 ? 's' : ''} puede${aviso.ayudantes > 1 ? 'n' : ''} ayudar` : ''}
                </span>
                <button class="btn-ayudar" onclick="puedoAyudar(${aviso.id})">
                    🤝 Puedo ayudar
                </button>
            </div>
        </div>
    `;
}

// =============================================
// FORMULARIO
// =============================================
function mostrarFormulario() {
    document.getElementById('form-aviso').style.display = 'block';
    document.querySelector('.btn-crear-aviso').style.display = 'none';
}

function ocultarFormulario() {
    document.getElementById('form-aviso').style.display = 'none';
    document.querySelector('.btn-crear-aviso').style.display = 'block';
    limpiarFormulario();
}

function limpiarFormulario() {
    document.getElementById('aviso-tipo').value = 'tirado';
    document.getElementById('aviso-zona').value = '';
    document.getElementById('aviso-necesidad').value = '';
    document.getElementById('aviso-descripcion').value = '';
}

// =============================================
// PUBLICAR AVISO
// =============================================
async function publicarAviso() {
    const tipo = document.getElementById('aviso-tipo').value;
    const zona = document.getElementById('aviso-zona').value.trim();
    const necesidad = document.getElementById('aviso-necesidad').value.trim();
    const descripcion = document.getElementById('aviso-descripcion').value.trim();
    
    // Validaciones
    if (!zona) {
        alert('La ubicación es obligatoria');
        return;
    }
    
    if (zona.length < 10) {
        alert('Describe mejor la ubicación (mínimo 10 caracteres)');
        return;
    }
    
    // Verificar direcciones exactas
    const prohibido = /\b(calle|c\/|avenida|avda|portal|piso|número|nº|mi casa|domicilio|urbanización)\b/i;
    if (prohibido.test(zona) || prohibido.test(descripcion)) {
        alert('⚠️ Por seguridad, usa puntos públicos (gasolinera, área de servicio, km). No publiques direcciones exactas.');
        return;
    }
    
    const btn = document.querySelector('.btn-publicar');
    btn.disabled = true;
    btn.textContent = 'Publicando...';
    
    try {
        const response = await fetch(`${API_BASE}/planb_avisos.php`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                tipo,
                zona,
                necesidad,
                descripcion
            })
        });
        
        const data = await response.json();
        
        if (!data.success) {
            throw new Error(data.error || 'Error al publicar');
        }
        
        // Guardar datos del aviso creado
        avisoCreado = {
            id: data.aviso_id,
            token: data.token,
            editUrl: `${window.location.origin}/planb_aviso_editar.html?id=${data.aviso_id}&token=${data.token}`
        };
        
        // Mostrar modal con URL de edición
        document.getElementById('edit-url').value = avisoCreado.editUrl;
        document.getElementById('modal-exito').style.display = 'flex';
        
        // Ocultar formulario y recargar avisos
        ocultarFormulario();
        cargarAvisos();
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    } finally {
        btn.disabled = false;
        btn.textContent = '📤 Publicar';
    }
}

// =============================================
// PUEDO AYUDAR
// =============================================
async function puedoAyudar(avisoId) {
    try {
        const response = await fetch(`${API_BASE}/planb_puedo_ayudar.php`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                aviso_id: avisoId
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            mostrarToast('¡Gracias por ofrecerte a ayudar!');
            cargarAvisos(); // Recargar para actualizar contador
        } else {
            alert(data.error || 'Error');
        }
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}

// =============================================
// MODAL
// =============================================
function cerrarModal() {
    document.getElementById('modal-exito').style.display = 'none';
}

function copiarEditUrl() {
    const input = document.getElementById('edit-url');
    input.select();
    document.execCommand('copy');
    mostrarToast('Enlace copiado');
}

// =============================================
// UTILIDADES
// =============================================
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function mostrarToast(mensaje) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.style.cssText = `
            position: fixed;
            bottom: 100px;
            left: 50%;
            transform: translateX(-50%);
            background: #333;
            color: white;
            padding: 12px 24px;
            border-radius: 8px;
            font-size: 14px;
            z-index: 9999;
            opacity: 0;
            transition: opacity 0.3s;
        `;
        document.body.appendChild(toast);
    }
    
    toast.textContent = mensaje;
    toast.style.opacity = '1';
    
    setTimeout(() => {
        toast.style.opacity = '0';
    }, 2000);
}

// Cerrar modal al hacer clic fuera
document.addEventListener('click', (e) => {
    const modal = document.getElementById('modal-exito');
    if (e.target === modal) {
        cerrarModal();
    }
});
