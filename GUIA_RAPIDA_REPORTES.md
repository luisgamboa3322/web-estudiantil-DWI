# 🔧 GUÍA RÁPIDA: RESTAURAR SECCIÓN DE REPORTES

## ✅ **YA HICE:**
1. ✅ Restauré el archivo `dashboard.html` a su estado original
2. ✅ Creé el archivo `SECCION_REPORTES_COMPLETA.html` con todo el código necesario

## 📝 **LO QUE DEBES HACER (3 PASOS SIMPLES):**

### **PASO 1: Agregar el script de reportes**

**Ubicación:** Línea 12 de `dashboard.html`

**Buscar:**
```html
<script src="/js/dashboard-stats.js" defer></script>
```

**Agregar JUSTO DESPUÉS (nueva línea 13):**
```html
<script src="/js/reportes.js" defer></script>
```

---

### **PASO 2: Buscar dónde va la sección de reportes**

En `dashboard.html`, busca esta línea (aproximadamente línea 551):

```html
<!-- Vista Reportes (Oculta por defecto) -->
<main id="reportes-content" class="content-view hidden">
    <h1 class="text-3xl font-bold text-gray-800">Reportes</h1>
    <p class="mt-2 text-gray-600">Sección para generar reportes.</p>
</main>
```

---

### **PASO 3: Reemplazar la sección de reportes**

**ELIMINAR estas 5 líneas:**
```html
<!-- Vista Reportes (Oculta por defecto) -->
<main id="reportes-content" class="content-view hidden">
    <h1 class="text-3xl font-bold text-gray-800">Reportes</h1>
    <p class="mt-2 text-gray-600">Sección para generar reportes.</p>
</main>
```

**REEMPLAZAR CON:** 
Todo el contenido del archivo `SECCION_REPORTES_COMPLETA.html` (copiar y pegar completo)

---

## 🎯 **RESUMEN DE CAMBIOS:**

1. **Línea 13:** Agregar `<script src="/js/reportes.js" defer></script>`
2. **Línea ~551-555:** Reemplazar la sección de reportes vacía con la sección completa

---

## ✅ **VERIFICACIÓN:**

Después de hacer los cambios, tu `dashboard.html` debe tener:

1. ✅ El script `/js/reportes.js` en el head
2. ✅ Una sección de reportes con:
   - 4 tarjetas de reportes (Estudiantes, Profesores, Cursos, Asignaciones)
   - Una tabla de resultados con botones "PDF" y "Excel"

---

## 🚀 **DESPUÉS DE LOS CAMBIOS:**

1. **Guardar** el archivo (`Ctrl+S`)
2. **Reiniciar** la aplicación Spring Boot
3. **Probar:**
   - Ir a `http://localhost:8080/admin/dashboard`
   - Hacer clic en "Reportes" en el sidebar
   - Deberías ver 4 tarjetas de reportes
   - Hacer clic en "Ver Reporte" de cualquiera
   - Deberías ver los botones "PDF" (rojo) y "Excel" (verde)

---

## 📂 **ARCHIVOS CREADOS PARA TI:**

1. ✅ `SECCION_REPORTES_COMPLETA.html` - Código completo para copiar
2. ✅ `reportes.js` - Ya está en `/src/main/resources/static/js/`
3. ✅ `PdfExportService.java` - Ya creado
4. ✅ `ExcelExportService.java` - Ya creado
5. ✅ `ReporteController.java` - Ya actualizado

---

## ⚠️ **IMPORTANTE:**

- NO agregues Chart.js dos veces (ya está en línea 11)
- Copia TODO el contenido de `SECCION_REPORTES_COMPLETA.html`, no solo una parte
- Asegúrate de que la sección de reportes esté ANTES de la sección de configuración

---

## 💡 **TIPS:**

### Para encontrar dónde va la sección de reportes:
1. Presiona `Ctrl+F` en VS Code
2. Busca: `reportes-content`
3. Verás la sección vacía que debes reemplazar

### Para copiar la sección completa:
1. Abre `SECCION_REPORTES_COMPLETA.html`
2. Presiona `Ctrl+A` (seleccionar todo)
3. Presiona `Ctrl+C` (copiar)
4. Ve a `dashboard.html`
5. Selecciona las 5 líneas de la sección vacía
6. Presiona `Ctrl+V` (pegar)

---

## 🎉 **¡ESO ES TODO!**

Con estos cambios tendrás:
- ✅ Sistema de reportes completo
- ✅ Exportación a PDF funcionando
- ✅ Exportación a Excel funcionando
- ✅ Interfaz completa con botones

¿Necesitas ayuda con algún paso específico?
