// Variable global para almacenar el tipo de reporte actual
let tipoReporteActual = '';

async function generarReporte(tipo) {
    try {
        tipoReporteActual = tipo;
        const response = await fetch(`/admin/reportes/${tipo}`);
        if (!response.ok) throw new Error('Error al generar reporte');

        const data = await response.json();
        mostrarReporte(tipo, data);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al generar el reporte');
    }
}

function mostrarReporte(tipo, data) {
    const resultados = document.getElementById('reporte-resultados');
    const titulo = document.getElementById('reporte-titulo');
    const contenido = document.getElementById('reporte-contenido');

    // Configurar título
    const titulos = {
        'estudiantes': 'Reporte de Estudiantes',
        'profesores': 'Reporte de Profesores',
        'cursos': 'Reporte de Cursos',
        'asignaciones': 'Reporte de Asignaciones'
    };
    titulo.textContent = titulos[tipo];

    // Generar tabla según el tipo
    let html = '<table class="w-full text-sm text-left text-gray-500">';

    if (tipo === 'estudiantes') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Código</th>
                    <th class="px-6 py-3">Email</th>
                    <th class="px-6 py-3">Cursos Activos</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.codigo}</td>
                    <td class="px-6 py-4">${item.email}</td>
                    <td class="px-6 py-4">${item.totalCursosActivos}</td>
                </tr>
            `;
        });
    } else if (tipo === 'profesores') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Especialidad</th>
                    <th class="px-6 py-3">Cursos Asignados</th>
                    <th class="px-6 py-3">Total Estudiantes</th>
                    <th class="px-6 py-3">Carga</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.especialidad}</td>
                    <td class="px-6 py-4">${item.numeroCursosAsignados}</td>
                    <td class="px-6 py-4">${item.totalEstudiantesBajoSuCargo}</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.nivelCarga === 'ALTA' ? 'bg-red-100 text-red-800' :
                            item.nivelCarga === 'MEDIA' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-green-100 text-green-800'
                        }">${item.nivelCarga}</span>
                    </td>
                </tr>
            `;
        });
    } else if (tipo === 'cursos') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Código</th>
                    <th class="px-6 py-3">Profesor</th>
                    <th class="px-6 py-3">Estudiantes</th>
                    <th class="px-6 py-3">Ocupación</th>
                    <th class="px-6 py-3">Demanda</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.codigo}</td>
                    <td class="px-6 py-4">${item.nombreProfesor}</td>
                    <td class="px-6 py-4">${item.numeroEstudiantesInscritos}</td>
                    <td class="px-6 py-4">${item.tasaOcupacion.toFixed(1)}%</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.nivelDemanda === 'ALTA' ? 'bg-green-100 text-green-800' :
                            item.nivelDemanda === 'MEDIA' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-red-100 text-red-800'
                        }">${item.nivelDemanda}</span>
                    </td>
                </tr>
            `;
        });
    } else if (tipo === 'asignaciones') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Estudiante</th>
                    <th class="px-6 py-3">Curso</th>
                    <th class="px-6 py-3">Profesor</th>
                    <th class="px-6 py-3">Estado</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombreEstudiante}</td>
                    <td class="px-6 py-4">${item.nombreCurso}</td>
                    <td class="px-6 py-4">${item.nombreProfesor}</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.estadoAsignacion === 'ACTIVO' ? 'bg-green-100 text-green-800' :
                            'bg-gray-100 text-gray-800'
                        }">${item.estadoAsignacion}</span>
                    </td>
                </tr>
            `;
        });
    }

    html += '</tbody></table>';
    contenido.innerHTML = html;
    resultados.classList.remove('hidden');

    // Actualizar iconos
    lucide.createIcons();
}

// Función para exportar a PDF
function exportarPDF() {
    if (!tipoReporteActual) {
        alert('No hay ningún reporte generado');
        return;
    }
    
    window.location.href = `/admin/reportes/${tipoReporteActual}/pdf`;
}

// Función para exportar a Excel
function exportarExcel() {
    if (!tipoReporteActual) {
        alert('No hay ningún reporte generado');
        return;
    }
    
    window.location.href = `/admin/reportes/${tipoReporteActual}/excel`;
}

function cerrarReporte() {
    document.getElementById('reporte-resultados').classList.add('hidden');
    tipoReporteActual = '';
}
