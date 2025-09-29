// Biblioteca PAP - Dashboard Module

const BibliotecaDashboard = {
    
    // Configuración
    config: {
        refreshInterval: 30000, // 30 segundos
        animationDuration: 600
    },
    
    // Estado
    state: {
        isRefreshing: false,
        lastUpdate: null,
        statsCache: {}
    },
    
    // Inicialización
    init: function() {
        this.setupAutoRefresh();
        this.setupEventListeners();
    },
    
    // Configurar auto-refresh
    setupAutoRefresh: function() {
        setInterval(() => {
            if (BibliotecaSPA.config.currentPage === 'dashboard') {
                this.refreshStats();
            }
        }, this.config.refreshInterval);
    },
    
    // Configurar event listeners
    setupEventListeners: function() {
        // Botón de refresh manual
        $(document).on('click', '#refreshStatsBtn', () => {
            this.refreshStats();
        });
        
        // Hover effects en cards
        $(document).on('mouseenter', '.stat-card', function() {
            $(this).addClass('hover-effect');
        });
        
        $(document).on('mouseleave', '.stat-card', function() {
            $(this).removeClass('hover-effect');
        });
    },
    
    // Cargar estadísticas del dashboard
    loadStats: function() {
        const userType = BibliotecaSPA.config.userSession?.userType;
        
        if (!userType) return;
        
        this.state.isRefreshing = true;
        this.showRefreshIndicator();
        
        const promises = [];
        
        if (userType === 'BIBLIOTECARIO') {
            promises.push(
                BibliotecaAPI.getLectorStats(),
                BibliotecaAPI.getPrestamoStats(),
                BibliotecaAPI.getDonacionStats()
            );
        } else {
            promises.push(
                BibliotecaAPI.getMisPrestamoStats()
            );
        }
        
        Promise.all(promises).then(results => {
            if (userType === 'BIBLIOTECARIO') {
                this.updateBibliotecarioStats(results[0], results[1], results[2]);
            } else {
                this.updateLectorStats(results[0]);
            }
            
            this.state.lastUpdate = new Date();
            this.hideRefreshIndicator();
            this.state.isRefreshing = false;
            
        }).catch(error => {
            console.error('Error cargando estadísticas:', error);
            BibliotecaSPA.showAlert('Error al cargar las estadísticas', 'danger');
            this.hideRefreshIndicator();
            this.state.isRefreshing = false;
        });
    },
    
    // Actualizar estadísticas para bibliotecario
    updateBibliotecarioStats: function(lectorStats, prestamoStats, donacionStats) {
        // Animación de números
        this.animateNumber('#totalLectores', lectorStats.total);
        this.animateNumber('#lectoresActivos', lectorStats.activos);
        this.animateNumber('#lectoresSuspendidos', lectorStats.suspendidos);
        
        this.animateNumber('#totalPrestamos', prestamoStats.total);
        this.animateNumber('#prestamosVencidos', prestamoStats.vencidos);
        this.animateNumber('#prestamosEnCurso', prestamoStats.enCurso);
        this.animateNumber('#prestamosPendientes', prestamoStats.pendientes);
        
        this.animateNumber('#totalLibros', donacionStats.libros);
        this.animateNumber('#totalArticulos', donacionStats.articulos);
        
        // Actualizar cache
        this.state.statsCache = {
            lectores: lectorStats,
            prestamos: prestamoStats,
            donaciones: donacionStats
        };
        
        // Mostrar indicador de última actualización
        this.showLastUpdate();
    },
    
    // Actualizar estadísticas para lector
    updateLectorStats: function(prestamoStats) {
        this.animateNumber('#misPrestamos', prestamoStats.total);
        this.animateNumber('#prestamosActivos', prestamoStats.activos);
        
        // Actualizar cache
        this.state.statsCache = {
            prestamos: prestamoStats
        };
        
        // Mostrar indicador de última actualización
        this.showLastUpdate();
    },
    
    // Animar números
    animateNumber: function(selector, targetValue) {
        const element = $(selector);
        const currentValue = parseInt(element.text()) || 0;
        
        if (currentValue === targetValue) return;
        
        const duration = this.config.animationDuration;
        const increment = (targetValue - currentValue) / (duration / 16);
        let current = currentValue;
        
        const timer = setInterval(() => {
            current += increment;
            
            if ((increment > 0 && current >= targetValue) || 
                (increment < 0 && current <= targetValue)) {
                current = targetValue;
                clearInterval(timer);
            }
            
            element.text(Math.floor(current));
        }, 16);
    },
    
    // Refrescar estadísticas
    refreshStats: function() {
        if (this.state.isRefreshing) return;
        
        this.loadStats();
        BibliotecaSPA.showAlert('Estadísticas actualizadas', 'success');
    },
    
    // Mostrar indicador de refresh
    showRefreshIndicator: function() {
        $('#refreshStatsBtn').prop('disabled', true).html('<span class="spinner"></span> Actualizando...');
    },
    
    // Ocultar indicador de refresh
    hideRefreshIndicator: function() {
        $('#refreshStatsBtn').prop('disabled', false).html('🔄 Actualizar');
    },
    
    // Mostrar última actualización
    showLastUpdate: function() {
        if (this.state.lastUpdate) {
            const timeString = this.state.lastUpdate.toLocaleTimeString('es-ES');
            let indicator = $('#lastUpdateIndicator');
            
            if (indicator.length === 0) {
                indicator = $('<div id="lastUpdateIndicator" class="text-center mt-2" style="color: #6c757d; font-size: 0.875rem;"></div>');
                $('.stats-grid').after(indicator);
            }
            
            indicator.text(`Última actualización: ${timeString}`);
        }
    },
    
    // Cargar alertas del sistema
    loadAlerts: function() {
        // Simular carga de alertas
        const alerts = [
            {
                type: 'warning',
                message: 'Hay 3 préstamos vencidos que requieren atención',
                priority: 'high'
            },
            {
                type: 'info',
                message: 'Se registraron 2 nuevas donaciones esta semana',
                priority: 'medium'
            },
            {
                type: 'success',
                message: 'El sistema está funcionando correctamente',
                priority: 'low'
            }
        ];
        
        this.renderAlerts(alerts);
    },
    
    // Renderizar alertas
    renderAlerts: function(alerts) {
        const container = $('#alertasContainer');
        
        if (container.length === 0) return;
        
        container.empty();
        
        alerts.forEach((alert, index) => {
            setTimeout(() => {
                const alertHtml = `
                    <div class="alert alert-${alert.type} fade-in-up" style="animation-delay: ${index * 0.1}s;">
                        <div class="d-flex justify-content-between align-items-center">
                            <span>${alert.message}</span>
                            <span class="badge badge-${this.getPriorityBadgeClass(alert.priority)}">${alert.priority}</span>
                        </div>
                    </div>
                `;
                container.append(alertHtml);
            }, index * 100);
        });
    },
    
    // Obtener clase de badge por prioridad
    getPriorityBadgeClass: function(priority) {
        switch (priority) {
            case 'high': return 'danger';
            case 'medium': return 'warning';
            case 'low': return 'success';
            default: return 'secondary';
        }
    },
    
    // Cargar gráficos (placeholder)
    loadCharts: function() {
        // En una implementación real, aquí se cargarían gráficos con Chart.js o similar
        console.log('Cargando gráficos del dashboard...');
    },
    
    // Exportar estadísticas
    exportStats: function() {
        const userType = BibliotecaSPA.config.userSession?.userType;
        const stats = this.state.statsCache;
        
        if (!stats || Object.keys(stats).length === 0) {
            BibliotecaSPA.showAlert('No hay estadísticas para exportar', 'warning');
            return;
        }
        
        // Crear datos para exportar
        const exportData = {
            fecha: new Date().toISOString(),
            tipoUsuario: userType,
            estadisticas: stats
        };
        
        // Simular exportación
        BibliotecaSPA.showAlert('Función de exportación en desarrollo', 'info');
        console.log('Datos para exportar:', exportData);
    },
    
    // Configurar widgets personalizables
    setupCustomizableWidgets: function() {
        // Hacer las cards arrastrables (placeholder)
        $('.stat-card').each(function() {
            $(this).attr('draggable', 'true');
        });
        
        // Event listeners para drag and drop
        $(document).on('dragstart', '.stat-card', function(e) {
            e.originalEvent.dataTransfer.setData('text/plain', $(this).attr('id'));
        });
        
        $(document).on('dragover', '.stats-grid', function(e) {
            e.preventDefault();
        });
        
        $(document).on('drop', '.stats-grid', function(e) {
            e.preventDefault();
            const draggedId = e.originalEvent.dataTransfer.getData('text/plain');
            const draggedElement = $('#' + draggedId);
            
            if (draggedElement.length > 0) {
                $(this).append(draggedElement);
            }
        });
    },
    
    // Mostrar notificaciones en tiempo real
    showRealtimeNotification: function(message, type = 'info') {
        const notification = $(`
            <div class="realtime-notification alert-${type}">
                <span>${message}</span>
                <button class="close-notification">&times;</button>
            </div>
        `);
        
        // Agregar estilos si no existen
        if (!$('#realtimeNotificationStyles').length) {
            const styles = `
                <style id="realtimeNotificationStyles">
                .realtime-notification {
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    padding: 1rem 1.5rem;
                    border-radius: 8px;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
                    z-index: 10000;
                    animation: slideInRight 0.3s ease-out;
                    max-width: 300px;
                }
                
                .realtime-notification.alert-info {
                    background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
                    border-left: 4px solid #17a2b8;
                    color: #0c5460;
                }
                
                .realtime-notification.alert-success {
                    background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
                    border-left: 4px solid #28a745;
                    color: #155724;
                }
                
                .realtime-notification.alert-warning {
                    background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
                    border-left: 4px solid #ffc107;
                    color: #856404;
                }
                
                .realtime-notification.alert-danger {
                    background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
                    border-left: 4px solid #dc3545;
                    color: #721c24;
                }
                
                .close-notification {
                    background: none;
                    border: none;
                    font-size: 1.2rem;
                    cursor: pointer;
                    margin-left: 1rem;
                    opacity: 0.7;
                }
                
                .close-notification:hover {
                    opacity: 1;
                }
                
                @keyframes slideInRight {
                    from {
                        opacity: 0;
                        transform: translateX(100%);
                    }
                    to {
                        opacity: 1;
                        transform: translateX(0);
                    }
                }
                </style>
            `;
            $('head').append(styles);
        }
        
        $('body').append(notification);
        
        // Auto-remove después de 5 segundos
        setTimeout(() => {
            notification.fadeOut(300, function() {
                $(this).remove();
            });
        }, 5000);
        
        // Botón de cerrar
        notification.find('.close-notification').click(function() {
            notification.fadeOut(300, function() {
                $(this).remove();
            });
        });
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaDashboard.init();
});

// Hacer disponible globalmente
window.BibliotecaDashboard = BibliotecaDashboard;
