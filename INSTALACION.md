# 📦 GUÍA DE INSTALACIÓN - Sistema Plan B

## 📁 RUTAS EXACTAS

### ARCHIVOS PARA HOSTINGER (tu servidor)

Sube estos archivos a `public_html/` en Hostinger:

```
public_html/
├── conexion.php                    ← YA EXISTE (tu archivo actual)
│
├── inc/
│   └── planb_funciones.php        ← NUEVO
│
├── api/
│   ├── planb_crear_pedido.php     ← NUEVO
│   ├── planb_marcar_pagado.php    ← NUEVO
│   ├── planb_avisos.php           ← NUEVO
│   └── planb_puedo_ayudar.php     ← NUEVO
│
├── admin/
│   ├── admin_login.php            ← NUEVO
│   ├── admin_facturas.php         ← NUEVO
│   ├── admin_descargar_factura.php← NUEVO
│   ├── admin_logout.php           ← NUEVO
│   └── generar_password.php       ← NUEVO (eliminar después de usar)
│
├── sql/
│   └── planb_facturacion.sql      ← NUEVO (ejecutar en phpMyAdmin)
│
└── storage/
    └── facturas/                   ← CREAR CARPETA (chmod 755)
        └── 2025/                   ← SE CREA AUTOMÁTICAMENTE
```

### ARCHIVOS PARA GITHUB (tu repo PWA)

Sube estos archivos a tu repositorio `web/`:

```
web/
├── planb_oferta.html              ← NUEVO
├── planb_comprar.html             ← NUEVO
├── planb_avisos.html              ← NUEVO
│
├── css/
│   └── planb.css                  ← NUEVO
│
└── js/
    ├── planb_pay.js               ← NUEVO
    └── planb_avisos.js            ← NUEVO
```

---

## 🔧 PASOS DE INSTALACIÓN

### PASO 1: Base de datos (phpMyAdmin)

1. Entra a phpMyAdmin en Hostinger
2. Selecciona la base de datos `u154469107_Nueva`
3. Ve a la pestaña **SQL**
4. Copia y pega el contenido de `sql/planb_facturacion.sql`
5. Clic en **Continuar** o **Ejecutar**

### PASO 2: Crear carpetas en Hostinger

Conéctate por FTP o usa el File Manager de Hostinger:

```bash
# Crear carpetas (si no existen)
mkdir -p public_html/inc
mkdir -p public_html/api
mkdir -p public_html/admin
mkdir -p public_html/sql
mkdir -p public_html/storage/facturas
chmod 755 public_html/storage/facturas
```

### PASO 3: Subir archivos PHP a Hostinger

Sube cada archivo a su carpeta correspondiente:

| Archivo | Ruta en Hostinger |
|---------|-------------------|
| `planb_funciones.php` | `public_html/inc/planb_funciones.php` |
| `planb_crear_pedido.php` | `public_html/api/planb_crear_pedido.php` |
| `planb_marcar_pagado.php` | `public_html/api/planb_marcar_pagado.php` |
| `planb_avisos.php` | `public_html/api/planb_avisos.php` |
| `planb_puedo_ayudar.php` | `public_html/api/planb_puedo_ayudar.php` |
| `admin_login.php` | `public_html/admin/admin_login.php` |
| `admin_facturas.php` | `public_html/admin/admin_facturas.php` |
| `admin_descargar_factura.php` | `public_html/admin/admin_descargar_factura.php` |
| `admin_logout.php` | `public_html/admin/admin_logout.php` |
| `generar_password.php` | `public_html/admin/generar_password.php` |

### PASO 4: Configurar contraseña del admin

1. Accede a: `https://buscandoadios-espana.com/admin/generar_password.php?key=BuscandoADios2025&pass=TU_CONTRASEÑA`
2. Copia el hash que aparece
3. En phpMyAdmin, ejecuta:
   ```sql
   UPDATE planb_admin SET pass_hash = 'EL_HASH_COPIADO' WHERE usuario = 'admin';
   ```
4. **ELIMINA** el archivo `generar_password.php`

### PASO 5: Subir archivos a GitHub

Sube los archivos de la PWA a tu repo:

| Archivo | Ruta en GitHub |
|---------|----------------|
| `planb_oferta.html` | `web/planb_oferta.html` |
| `planb_comprar.html` | `web/planb_comprar.html` |
| `planb_avisos.html` | `web/planb_avisos.html` |
| `planb.css` | `web/css/planb.css` |
| `planb_pay.js` | `web/js/planb_pay.js` |
| `planb_avisos.js` | `web/js/planb_avisos.js` |

### PASO 6: Verificar la API_BASE

En los archivos JavaScript, verifica que la URL sea correcta:

**web/js/planb_pay.js** (línea 6):
```javascript
const API_BASE = 'https://buscandoadios-espana.com/api';
```

**web/js/planb_avisos.js** (línea 6):
```javascript
const API_BASE = 'https://buscandoadios-espana.com/api';
```

---

## ✅ VERIFICACIÓN

### Probar APIs

1. **Crear pedido**: `POST https://buscandoadios-espana.com/api/planb_crear_pedido.php`
2. **Listar avisos**: `GET https://buscandoadios-espana.com/api/planb_avisos.php`

### Probar páginas

1. **Oferta**: `https://buscandoadios-espana.com/planb_oferta.html` (o desde la PWA)
2. **Admin**: `https://buscandoadios-espana.com/admin/admin_login.php`

### Credenciales admin por defecto

- **Usuario**: `admin`
- **Contraseña**: La que configuraste en el paso 4

---

## 🔐 SEGURIDAD

- [ ] Eliminar `generar_password.php` después de configurar
- [ ] Cambiar la contraseña por defecto
- [ ] La carpeta `admin/` debería estar protegida (opcional: .htaccess)
- [ ] Los avisos caducan automáticamente en 24h
- [ ] No se guardan datos personales de los usuarios

---

## 📞 DATOS DE BIZUM

- **Teléfono**: 600 767 209
- **Precio**: 5,00 € (IVA incluido)
- **Concepto**: EN RUTA + REFERENCIA

---

## 🆘 PROBLEMAS COMUNES

### "Error de conexión"
- Verifica que `conexion.php` esté en la ruta correcta
- Verifica que las APIs tengan `require_once __DIR__ . '/../inc/planb_funciones.php';`

### "CORS error"
- Las APIs ya incluyen headers CORS, pero si usas otro dominio, ajusta los headers

### "Tabla no existe"
- Ejecuta el SQL en phpMyAdmin

### "Permiso denegado en storage"
- Ejecuta: `chmod 755 public_html/storage/facturas`
