// Dashboard Statistics
let chartEstudiantesCurso, chartCursosEstado, chartTendenciaInscripciones;

document.addEventListener('DOMContentLoaded', () => {
    // Cargar estadísticas cuando se muestra el dashboard
    const dashboardLink = document.querySelector('[data-target="dashboard"]');
    if (dashboardLink) {
        dashboardLink.addEventListener('click', () => {
            loadDashboardStats();
        });
    }

    // Cargar estadísticas al inicio si el dashboard está visible
    if (!document.getElementById('dashboard-content').classList.contains('hidden')) {
        loadDashboardStats();
    }
});

async function loadDashboardStats() {
    try {
        const response = await fetch('/admin/dashboard/stats');
        if (!response.ok) {
            throw new Error('Error al cargar estadísticas');
        }
        
        const stats = await response.json();
        console.log('Estadísticas cargadas:', stats);
        
        // Actualizar métricas clave
        document.getElementById('total-estudiantes').textContent = stats.totalEstudiantesActivos || 0;
        document.getElementById('total-profesores').textContent = stats.totalProfesores || 0;
        document.getElementById('total-cursos').textContent = stats.totalCursosActivos || 0;
        document.getElementById('total-asignaciones').textContent = stats.totalAsignacionesActivas || 0;
        document.getElementById('tasa-ocupacion').textContent = (stats.tasaOcupacionCursos || 0).toFixed(1) + '%';
        document.getElementById('promedio-estudiantes').textContent = (stats.promedioEstudiantesPorCurso || 0).toFixed(1);
        
        // Renderizar gráficos
        renderChartEstudiantesCurso(stats.estudiantesPorCurso || {});
        renderChartCursosEstado(stats.cursosPorEstado || {});
        renderChartTendenciaInscripciones(stats.tendenciaInscripciones || []);
        
        // Mostrar actividad reciente
        renderActividadReciente(stats.actividadReciente || []);
        
        // Actualizar iconos de Lucide
        lucide.createIcons();
        
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
        alert('Error al cargar las estadísticas del dashboard');
    }
}

function renderChartEstudiantesCurso(data) {
    const ctx = document.getElementById('chart-estudiantes-curso');
    if (!ctx) return;
    
    // Destruir gráfico anterior si existe
    if (chartEstudiantesCurso) {
        chartEstudiantesCurso.destroy();
    }
    
    const labels = Object.keys(data);
    const values = Object.values(data);
    
    chartEstudiantesCurso = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Estudiantes',
                data: values,
                backgroundColor: 'rgba(59, 130, 246, 0.8)',
                borderColor: 'rgba(59, 130, 246, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Estudiantes: ' + context.parsed.y;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                },
                x: {
                    ticks: {
                        maxRotation: 45,
                        minRotation: 45
                    }
                }
            }
        }
    });
}

function renderChartCursosEstado(data) {
    const ctx = document.getElementById('chart-cursos-estado');
    if (!ctx) return;
    
    // Destruir gráfico anterior si existe
    if (chartCursosEstado) {
        chartCursosEstado.destroy();
    }
    
    const labels = Object.keys(data);
    const values = Object.values(data);
    
    chartCursosEstado = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    'rgba(34, 197, 94, 0.8)',
                    'rgba(239, 68, 68, 0.8)'
                ],
                borderColor: [
                    'rgba(34, 197, 94, 1)',
                    'rgba(239, 68, 68, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return label + ': ' + value + ' (' + percentage + '%)';
                        }
                    }
                }
            }
        }
    });
}

function renderChartTendenciaInscripciones(data) {
    const ctx = document.getElementById('chart-tendencia-inscripciones');
    if (!ctx) return;
    
    // Destruir gráfico anterior si existe
    if (chartTendenciaInscripciones) {
        chartTendenciaInscripciones.destroy();
    }
    
    const labels = data.map(item => item.fecha);
    const values = data.map(item => item.cantidad);
    
    chartTendenciaInscripciones = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Inscripciones',
                data: values,
                fill: true,
                backgroundColor: 'rgba(168, 85, 247, 0.2)',
                borderColor: 'rgba(168, 85, 247, 1)',
                borderWidth: 2,
                tension: 0.4,
                pointBackgroundColor: 'rgba(168, 85, 247, 1)',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Inscripciones: ' + context.parsed.y;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

function renderActividadReciente(actividades) {
    const container = document.getElementById('actividad-reciente');
    if (!container) return;
    
    if (actividades.length === 0) {
        container.innerHTML = `
            <div class="flex items-center justify-center py-8 text-gray-500">
                <i data-lucide="inbox" class="w-6 h-6 mr-2"></i>
                <span>No hay actividad reciente</span>
            </div>
        `;
        lucide.createIcons();
        return;
    }
    
    const iconMap = {
        'ESTUDIANTE': 'user-plus',
        'PROFESOR': 'user-check',
        'CURSO': 'book-marked',
        'ASIGNACION': 'book-open'
    };
    
    const colorMap = {
        'ESTUDIANTE': 'blue',
        'PROFESOR': 'green',
        'CURSO': 'orange',
        'ASIGNACION': 'purple'
    };
    
    container.innerHTML = actividades.map(actividad => {
        const icon = actividad.icono || iconMap[actividad.tipo] || 'circle';
        const color = colorMap[actividad.tipo] || 'gray';
        const fecha = new Date(actividad.fecha);
        const fechaFormateada = formatearFechaRelativa(fecha);
        
        return `
            <div class="flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div class="bg-${color}-100 p-2 rounded-full flex-shrink-0">
                    <i data-lucide="${icon}" class="w-5 h-5 text-${color}-600"></i>
                </div>
                <div class="flex-1 min-w-0">
                    <p class="text-sm text-gray-900">${actividad.descripcion}</p>
                    <p class="text-xs text-gray-500 mt-1">${fechaFormateada}</p>
                </div>
            </div>
        `;
    }).join('');
    
    lucide.createIcons();
}

function formatearFechaRelativa(fecha) {
    const ahora = new Date();
    const diff = ahora - fecha;
    const segundos = Math.floor(diff / 1000);
    const minutos = Math.floor(segundos / 60);
    const horas = Math.floor(minutos / 60);
    const dias = Math.floor(horas / 24);
    
    if (segundos < 60) return 'Hace un momento';
    if (minutos < 60) return `Hace ${minutos} minuto${minutos > 1 ? 's' : ''}`;
    if (horas < 24) return `Hace ${horas} hora${horas > 1 ? 's' : ''}`;
    if (dias < 7) return `Hace ${dias} día${dias > 1 ? 's' : ''}`;
    
    return fecha.toLocaleDateString('es-ES', { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric' 
    });
}
