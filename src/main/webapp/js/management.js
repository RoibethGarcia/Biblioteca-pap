// Biblioteca PAP - Management Module

const BibliotecaManagement = {
    
    // Estado
    state: {
        currentFilter: {},
        searchTimeout: null,
        selectedItems: []
    },
    
    // Inicialización
    init: function() {
        this.setupEventListeners();
        this.setupSearch();
        this.setupFilters();
    },
    
    // Configurar event listeners
    setupEventListeners: function() {
        // Búsqueda con debounce
        $(document).on('input', '#searchInput', (e) => {
            clearTimeout(this.state.searchTimeout);
            this.state.searchTimeout = setTimeout(() => {
                this.performSearch();
            }, 300);
        });
        
        // Filtros
        $(document).on('change', '#estadoFilter, #zonaFilter', () => {
            this.applyFilters();
        });
        
        // Botón de búsqueda
        $(document).on('click', '#searchBtn', (e) => {
            e.preventDefault();
            this.performSearch();
        });
        
        // Selección múltiple
        $(document).on('change', '.select-all', (e) => {
            const isChecked = $(e.target).is(':checked');
            $('.item-checkbox').prop('checked', isChecked);
            this.updateSelection();
        });
        
        $(document).on('change', '.item-checkbox', () => {
            this.updateSelection();
        });
        
        // Acciones en lote
        $(document).on('click', '#bulkActionBtn', () => {
            this.performBulkAction();
        });
        
        // Modal de confirmación
        $(document).on('click', '.confirm-action', (e) => {
            e.preventDefault();
            const action = $(e.target).data('action');
            const itemId = $(e.target).data('item-id');
            this.showConfirmationModal(action, itemId);
        });
    },
    
    // Configurar búsqueda
    setupSearch: function() {
        // Highlight de resultados
        $(document).on('keyup', '#searchInput', function() {
            const searchTerm = $(this).val().toLowerCase();
            if (searchTerm.length > 2) {
                BibliotecaManagement.highlightSearchResults(searchTerm);
            } else {
                BibliotecaManagement.clearHighlights();
            }
        });
    },
    
    // Configurar filtros
    setupFilters: function() {
        // Filtros avanzados (placeholder)
        $(document).on('click', '#advancedFiltersBtn', () => {
            this.toggleAdvancedFilters();
        });
        
        // Limpiar filtros
        $(document).on('click', '#clearFiltersBtn', () => {
            this.clearFilters();
        });
    },
    
    // Realizar búsqueda
    performSearch: function() {
        const searchTerm = $('#searchInput').val().trim();
        const currentData = this.getCurrentData();
        
        if (!searchTerm) {
            this.renderData(currentData);
            return;
        }
        
        const filteredData = currentData.filter(item => {
            return this.matchesSearch(item, searchTerm);
        });
        
        this.renderData(filteredData);
        this.updateSearchResults(filteredData.length, currentData.length);
    },
    
    // Aplicar filtros
    applyFilters: function() {
        const estado = $('#estadoFilter').val();
        const zona = $('#zonaFilter').val();
        const searchTerm = $('#searchInput').val().trim();
        
        this.state.currentFilter = { estado, zona, searchTerm };
        
        const currentData = this.getCurrentData();
        let filteredData = currentData;
        
        // Aplicar filtros
        if (estado) {
            filteredData = filteredData.filter(item => item.estado === estado);
        }
        
        if (zona) {
            filteredData = filteredData.filter(item => item.zona === zona);
        }
        
        if (searchTerm) {
            filteredData = filteredData.filter(item => this.matchesSearch(item, searchTerm));
        }
        
        this.renderData(filteredData);
        this.updateFilterResults(filteredData.length, currentData.length);
    },
    
    // Verificar si un item coincide con la búsqueda
    matchesSearch: function(item, searchTerm) {
        const searchableFields = ['nombre', 'apellido', 'email', 'telefono', 'zona'];
        const itemString = searchableFields
            .map(field => item[field] || '')
            .join(' ')
            .toLowerCase();
        
        return itemString.includes(searchTerm.toLowerCase());
    },
    
    // Obtener datos actuales (placeholder)
    getCurrentData: function() {
        // En una implementación real, esto vendría de la API o estado
        return [
            {
                id: 1,
                nombre: 'Juan',
                apellido: 'Pérez',
                email: 'juan.perez@email.com',
                telefono: '+598 99 123 456',
                zona: 'Centro',
                estado: 'ACTIVO'
            },
            {
                id: 2,
                nombre: 'María',
                apellido: 'González',
                email: 'maria.gonzalez@email.com',
                telefono: '+598 98 765 432',
                zona: 'Norte',
                estado: 'ACTIVO'
            },
            {
                id: 3,
                nombre: 'Carlos',
                apellido: 'Rodríguez',
                email: 'carlos.rodriguez@email.com',
                telefono: '+598 97 654 321',
                zona: 'Sur',
                estado: 'SUSPENDIDO'
            }
        ];
    },
    
    // Renderizar datos
    renderData: function(data) {
        const tbody = $('#lectoresTable tbody');
        tbody.empty();
        
        if (data.length === 0) {
            tbody.html('<tr><td colspan="7" class="text-center">No se encontraron resultados</td></tr>');
            return;
        }
        
        data.forEach(item => {
            const row = this.createDataRow(item);
            tbody.append(row);
        });
        
        this.addRowAnimations();
    },
    
    // Crear fila de datos
    createDataRow: function(item) {
        const estadoBadge = item.estado === 'ACTIVO' ? 
            '<span class="badge badge-success">Activo</span>' : 
            '<span class="badge badge-warning">Suspendido</span>';
        
        return $(`
            <tr data-id="${item.id}" class="data-row">
                <td>
                    <input type="checkbox" class="item-checkbox" value="${item.id}">
                    ${item.id}
                </td>
                <td>${item.nombre} ${item.apellido}</td>
                <td>${item.email}</td>
                <td>${item.telefono}</td>
                <td>${item.zona}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="btn-group">
                        <button class="btn btn-primary btn-sm" onclick="BibliotecaManagement.viewItem(${item.id})">
                            👁️
                        </button>
                        <button class="btn btn-secondary btn-sm" onclick="BibliotecaManagement.editItem(${item.id})">
                            ✏️
                        </button>
                        <button class="btn btn-warning btn-sm confirm-action" data-action="change-status" data-item-id="${item.id}">
                            🔄
                        </button>
                        <button class="btn btn-danger btn-sm confirm-action" data-action="delete" data-item-id="${item.id}">
                            🗑️
                        </button>
                    </div>
                </td>
            </tr>
        `);
    },
    
    // Agregar animaciones a las filas
    addRowAnimations: function() {
        $('.data-row').each(function(index) {
            $(this).css('animation-delay', `${index * 0.05}s`).addClass('fade-in-up');
        });
    },
    
    // Actualizar selección
    updateSelection: function() {
        const selectedItems = $('.item-checkbox:checked').map(function() {
            return $(this).val();
        }).get();
        
        this.state.selectedItems = selectedItems;
        
        const count = selectedItems.length;
        const total = $('.item-checkbox').length;
        
        if (count > 0) {
            $('#selectionInfo').text(`${count} de ${total} seleccionados`);
            $('#bulkActions').show();
        } else {
            $('#selectionInfo').text('');
            $('#bulkActions').hide();
        }
        
        // Actualizar checkbox "Seleccionar todo"
        const selectAll = $('.select-all');
        if (count === total) {
            selectAll.prop('checked', true).prop('indeterminate', false);
        } else if (count > 0) {
            selectAll.prop('indeterminate', true);
        } else {
            selectAll.prop('checked', false).prop('indeterminate', false);
        }
    },
    
    // Realizar acción en lote
    performBulkAction: function() {
        const action = $('#bulkActionSelect').val();
        const selectedItems = this.state.selectedItems;
        
        if (selectedItems.length === 0) {
            BibliotecaSPA.showAlert('Seleccione al menos un elemento', 'warning');
            return;
        }
        
        if (!action) {
            BibliotecaSPA.showAlert('Seleccione una acción', 'warning');
            return;
        }
        
        this.showBulkConfirmationModal(action, selectedItems);
    },
    
    // Mostrar modal de confirmación
    showConfirmationModal: function(action, itemId) {
        const actionText = this.getActionText(action);
        const modalHtml = `
            <div class="modal show" id="confirmationModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>Confirmar Acción</h3>
                        <span class="close" onclick="$('#confirmationModal').remove()">&times;</span>
                    </div>
                    <div class="modal-body">
                        <p>¿Está seguro de que desea <strong>${actionText}</strong> este elemento?</p>
                        <p class="text-muted">Esta acción no se puede deshacer.</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="$('#confirmationModal').remove()">Cancelar</button>
                        <button class="btn btn-danger" onclick="BibliotecaManagement.executeAction('${action}', [${itemId}])">
                            Confirmar
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        $('body').append(modalHtml);
    },
    
    // Mostrar modal de confirmación en lote
    showBulkConfirmationModal: function(action, itemIds) {
        const actionText = this.getActionText(action);
        const modalHtml = `
            <div class="modal show" id="bulkConfirmationModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>Confirmar Acción en Lote</h3>
                        <span class="close" onclick="$('#bulkConfirmationModal').remove()">&times;</span>
                    </div>
                    <div class="modal-body">
                        <p>¿Está seguro de que desea <strong>${actionText}</strong> <strong>${itemIds.length}</strong> elementos?</p>
                        <p class="text-muted">Esta acción no se puede deshacer.</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="$('#bulkConfirmationModal').remove()">Cancelar</button>
                        <button class="btn btn-danger" onclick="BibliotecaManagement.executeAction('${action}', [${itemIds.join(',')}])">
                            Confirmar
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        $('body').append(modalHtml);
    },
    
    // Obtener texto de acción
    getActionText: function(action) {
        const actions = {
            'delete': 'eliminar',
            'change-status': 'cambiar el estado de',
            'export': 'exportar',
            'activate': 'activar',
            'deactivate': 'desactivar'
        };
        
        return actions[action] || action;
    },
    
    // Ejecutar acción
    executeAction: function(action, itemIds) {
        // Cerrar modales
        $('.modal').remove();
        
        this.showLoading();
        
        // Simular ejecución de acción
        setTimeout(() => {
            this.hideLoading();
            
            switch (action) {
                case 'delete':
                    this.deleteItems(itemIds);
                    break;
                case 'change-status':
                    this.changeStatusItems(itemIds);
                    break;
                case 'export':
                    this.exportItems(itemIds);
                    break;
                default:
                    BibliotecaSPA.showAlert(`Acción "${action}" ejecutada exitosamente`, 'success');
            }
            
            // Limpiar selección
            $('.item-checkbox').prop('checked', false);
            this.updateSelection();
            
        }, 1000);
    },
    
    // Eliminar elementos
    deleteItems: function(itemIds) {
        BibliotecaSPA.showAlert(`${itemIds.length} elementos eliminados exitosamente`, 'success');
        this.refreshData();
    },
    
    // Cambiar estado de elementos
    changeStatusItems: function(itemIds) {
        BibliotecaSPA.showAlert(`Estado de ${itemIds.length} elementos cambiado exitosamente`, 'success');
        this.refreshData();
    },
    
    // Exportar elementos
    exportItems: function(itemIds) {
        BibliotecaSPA.showAlert(`${itemIds.length} elementos exportados exitosamente`, 'success');
    },
    
    // Refrescar datos
    refreshData: function() {
        this.performSearch();
    },
    
    // Mostrar loading
    showLoading: function() {
        $('#lectoresTable tbody').html('<tr><td colspan="7" class="text-center"><div class="spinner"></div> Procesando...</td></tr>');
    },
    
    // Ocultar loading
    hideLoading: function() {
        // El contenido se actualizará con los nuevos datos
    },
    
    // Actualizar resultados de búsqueda
    updateSearchResults: function(filteredCount, totalCount) {
        let indicator = $('#searchResultsIndicator');
        
        if (indicator.length === 0) {
            indicator = $('<div id="searchResultsIndicator" class="text-center mt-2" style="color: #6c757d; font-size: 0.875rem;"></div>');
            $('.table-responsive').after(indicator);
        }
        
        if (filteredCount < totalCount) {
            indicator.text(`Mostrando ${filteredCount} de ${totalCount} resultados`);
        } else {
            indicator.text('');
        }
    },
    
    // Actualizar resultados de filtros
    updateFilterResults: function(filteredCount, totalCount) {
        this.updateSearchResults(filteredCount, totalCount);
    },
    
    // Resaltar resultados de búsqueda
    highlightSearchResults: function(searchTerm) {
        $('.data-row').each(function() {
            const row = $(this);
            const text = row.text().toLowerCase();
            
            if (text.includes(searchTerm.toLowerCase())) {
                row.addClass('search-highlight');
            } else {
                row.removeClass('search-highlight');
            }
        });
    },
    
    // Limpiar resaltados
    clearHighlights: function() {
        $('.data-row').removeClass('search-highlight');
    },
    
    // Limpiar filtros
    clearFilters: function() {
        $('#estadoFilter').val('');
        $('#zonaFilter').val('');
        $('#searchInput').val('');
        
        this.state.currentFilter = {};
        this.performSearch();
        BibliotecaSPA.showAlert('Filtros limpiados', 'info');
    },
    
    // Alternar filtros avanzados
    toggleAdvancedFilters: function() {
        let advancedFilters = $('#advancedFilters');
        
        if (advancedFilters.length === 0) {
            advancedFilters = $(`
                <div id="advancedFilters" class="card mt-3" style="display: none;">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Filtros Avanzados</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="dateFrom">Fecha desde:</label>
                                    <input type="date" id="dateFrom" class="form-control">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="dateTo">Fecha hasta:</label>
                                    <input type="date" id="dateTo" class="form-control">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="sortBy">Ordenar por:</label>
                                    <select id="sortBy" class="form-control">
                                        <option value="">Seleccionar...</option>
                                        <option value="nombre">Nombre</option>
                                        <option value="email">Email</option>
                                        <option value="fecha">Fecha</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `);
            
            $('.card:first').after(advancedFilters);
        }
        
        advancedFilters.slideToggle();
    },
    
    // Métodos específicos de elementos
    viewItem: function(itemId) {
        BibliotecaSPA.showAlert(`Ver detalles del elemento ID: ${itemId}`, 'info');
    },
    
    editItem: function(itemId) {
        BibliotecaSPA.showAlert(`Editar elemento ID: ${itemId}`, 'info');
    }
};

// Estilos CSS adicionales para management
const managementStyles = `
<style>
.search-highlight {
    background-color: #fff3cd !important;
    animation: highlightPulse 1s ease-in-out;
}

@keyframes highlightPulse {
    0%, 100% { background-color: #fff3cd; }
    50% { background-color: #ffeaa7; }
}

.bulk-actions {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background: white;
    padding: 1rem;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    z-index: 1000;
    display: none;
}

.selection-info {
    color: #6c757d;
    font-size: 0.875rem;
    margin-bottom: 0.5rem;
}

.btn-group .btn {
    margin: 0 0.125rem;
}

.data-row {
    transition: all 0.3s ease;
}

.data-row:hover {
    transform: translateX(5px);
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.table th {
    position: sticky;
    top: 0;
    background: white;
    z-index: 10;
}
</style>
`;

// Agregar estilos
$('head').append(managementStyles);

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaManagement.init();
});

// Hacer disponible globalmente
window.BibliotecaManagement = BibliotecaManagement;
