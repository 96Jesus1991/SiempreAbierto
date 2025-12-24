/**
 * planb_pay.js
 * Lógica para el proceso de pago con Bizum
 */

// Configuración - CAMBIAR ESTA URL A TU DOMINIO
const API_BASE = 'https://buscandoadios-espana.com/api';

// Estado
let pedidoActual = null;

// =============================================
// INICIALIZACIÓN
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    crearPedido();
});

// =============================================
// CREAR PEDIDO
// =============================================
async function crearPedido() {
    mostrarEstado('cargando');
    
    try {
        const response = await fetch(`${API_BASE}/planb_crear_pedido.php`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (!data.success) {
            throw new Error(data.error || 'Error al crear pedido');
        }
        
        pedidoActual = data.pedido;
        
        // Actualizar UI
        document.getElementById('ref-code').textContent = pedidoActual.ref;
        document.getElementById('telefono').textContent = formatearTelefono(pedidoActual.bizum_telefono);
        document.getElementById('concepto').textContent = pedidoActual.concepto;
        
        mostrarEstado('pago');
        
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('error-mensaje').textContent = error.message;
        mostrarEstado('error');
    }
}

// =============================================
// CONFIRMAR PAGO
// =============================================
async function confirmarPago() {
    if (!pedidoActual) {
        alert('No hay pedido activo');
        return;
    }
    
    const btn = document.getElementById('btn-confirmar');
    btn.disabled = true;
    btn.innerHTML = '<span>⏳ Enviando...</span>';
    
    try {
        const response = await fetch(`${API_BASE}/planb_marcar_pagado.php`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ref: pedidoActual.ref
            })
        });
        
        const data = await response.json();
        
        if (!data.success) {
            throw new Error(data.error || 'Error al confirmar');
        }
        
        // Mostrar estado confirmado
        document.getElementById('ref-final').textContent = pedidoActual.ref;
        mostrarEstado('confirmado');
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error: ' + error.message);
        btn.disabled = false;
        btn.innerHTML = '<span>✅ He hecho el Bizum</span>';
    }
}

// =============================================
// REINTENTAR
// =============================================
function reintentar() {
    crearPedido();
}

// =============================================
// COPIAR AL PORTAPAPELES
// =============================================
function copiarRef() {
    copiarAlPortapapeles(pedidoActual?.ref || '', 'Referencia copiada');
}

function copiarTelefono() {
    copiarAlPortapapeles(pedidoActual?.bizum_telefono || '', 'Teléfono copiado');
}

function copiarConcepto() {
    copiarAlPortapapeles(pedidoActual?.concepto || '', 'Concepto copiado');
}

function copiarAlPortapapeles(texto, mensaje) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(texto).then(() => {
            mostrarToast(mensaje);
        });
    } else {
        // Fallback
        const input = document.createElement('input');
        input.value = texto;
        document.body.appendChild(input);
        input.select();
        document.execCommand('copy');
        document.body.removeChild(input);
        mostrarToast(mensaje);
    }
}

// =============================================
// UTILIDADES
// =============================================
function mostrarEstado(estado) {
    const estados = ['cargando', 'pago', 'confirmado', 'error'];
    estados.forEach(e => {
        const el = document.getElementById(`estado-${e}`);
        if (el) {
            el.style.display = e === estado ? 'block' : 'none';
        }
    });
}

function formatearTelefono(tel) {
    // 600767209 -> 600 767 209
    return tel.replace(/(\d{3})(\d{3})(\d{3})/, '$1 $2 $3');
}

function mostrarToast(mensaje) {
    // Crear toast simple
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
