# 🔧 INSTRUCCIONES MANUALES PARA ACTUALIZAR DASHBOARD.HTML

## ✅ **CAMBIOS NECESARIOS (2 SIMPLES EDICIONES)**

### **CAMBIO 1: Agregar script de reportes**

**Ubicación:** Línea 12 del archivo `dashboard.html`

**Buscar esta línea:**
```html
<script src="/js/dashboard-stats.js" defer></script>
```

**Agregar JUSTO DESPUÉS:**
```html
<script src="/js/reportes.js" defer></script>
```

**Resultado final (líneas 11-13):**
```html
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script src="/js/dashboard-stats.js" defer></script>
<script src="/js/reportes.js" defer></script>
```

---

### **CAMBIO 2: Agregar botones de exportación**

**Ubicación:** Buscar la sección "Tabla de Resultados" (aproximadamente línea 623)

**Buscar este bloque:**
```html
<!-- Tabla de Resultados -->
<div id="reporte-resultados" class="mt-8 hidden">
    <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex justify-between items-center mb-4">
            <h2 id="reporte-titulo" class="text-xl font-bold text-gray-800"></h2>
            <button onclick="cerrarReporte()" class="text-gray-500 hover:text-gray-700">
                <i data-lucide="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div id="reporte-contenido" class="overflow-x-auto"></div>
    </div>
</div>
```

**REEMPLAZAR CON:**
```html
<!-- Tabla de Resultados -->
<div id="reporte-resultados" class="mt-8 hidden">
    <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex justify-between items-center mb-4">
            <h2 id="reporte-titulo" class="text-xl font-bold text-gray-800"></h2>
            <div class="flex space-x-2">
                <!-- Botones de exportación -->
                <button onclick="exportarPDF()" 
                        class="flex items-center space-x-2 bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition-colors">
                    <i data-lucide="file-text" class="w-4 h-4"></i>
                    <span>PDF</span>
                </button>
                <button onclick="exportarExcel()" 
                        class="flex items-center space-x-2 bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition-colors">
                    <i data-lucide="file-spreadsheet" class="w-4 h-4"></i>
                    <span>Excel</span>
                </button>
                <button onclick="cerrarReporte()" class="text-gray-500 hover:text-gray-700">
                    <i data-lucide="x" class="w-6 h-6"></i>
                </button>
            </div>
        </div>
        <div id="reporte-contenido" class="overflow-x-auto"></div>
    </div>
</div>
```

---

## 📝 **RESUMEN DE CAMBIOS**

1. ✅ **Línea 12:** Agregar `<script src="/js/reportes.js" defer></script>`
2. ✅ **Línea ~623-632:** Reemplazar el div de "reporte-resultados" con la versión que incluye botones PDF y Excel

---

## 🎯 **CÓMO HACER LOS CAMBIOS**

### **Opción 1: Editar manualmente en VS Code**
1. Abrir `dashboard.html`
2. Ir a línea 12 → Agregar el script
3. Buscar "Tabla de Resultados" (Ctrl+F)
4. Reemplazar el bloque completo

### **Opción 2: Usar Buscar y Reemplazar**
1. Abrir `dashboard.html`
2. Presionar `Ctrl+H` (Buscar y Reemplazar)
3. Pegar el código "BUSCAR" y "REEMPLAZAR" de arriba

---

## ✅ **VERIFICACIÓN**

Después de hacer los cambios, verifica que:

1. ✅ El archivo tiene la línea: `<script src="/js/reportes.js" defer></script>`
2. ✅ La sección de resultados tiene 2 botones: "PDF" (rojo) y "Excel" (verde)
3. ✅ No hay errores de sintaxis (VS Code te avisará con líneas rojas)

---

## 🚀 **DESPUÉS DE LOS CAMBIOS**

1. **Guardar el archivo** (`Ctrl+S`)
2. **Reiniciar la aplicación Spring Boot**
3. **Ir a:** `http://localhost:8080/admin/dashboard`
4. **Probar:**
   - Ir a "Reportes"
   - Hacer clic en "Ver Reporte" de cualquier tarjeta
   - Verás los botones "PDF" y "Excel"
   - Hacer clic en "PDF" → Descarga el PDF
   - Hacer clic en "Excel" → Descarga el Excel

---

## 🎉 **¡LISTO!**

Con estos 2 cambios simples, tendrás:
- ✅ Exportación a PDF funcionando
- ✅ Exportación a Excel funcionando
- ✅ Botones visibles en la interfaz
- ✅ Descarga automática de archivos

---

## ⚠️ **SI ALGO SALE MAL**

Si el archivo se corrompe o hay problemas:

```bash
# Restaurar el archivo original
git checkout HEAD -- demo/src/main/resources/templates/administrador/dashboard.html

# Volver a intentar los cambios
```

---

## 📞 **¿NECESITAS AYUDA?**

Si tienes problemas con los cambios manuales, puedo:
1. Crear un archivo HTML completo ya modificado
2. Guiarte paso a paso con capturas
3. Crear un script que haga los cambios automáticamente

¡Avísame si necesitas ayuda!
