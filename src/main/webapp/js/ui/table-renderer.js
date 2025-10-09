/**
 * TableRenderer - Renderizador genérico de tablas
 * Elimina la duplicación de código al renderizar tablas dinámicamente
 */
class TableRenderer {
    /**
     * Constructor del renderizador de tablas
     * @param {string} tableSelector - Selector jQuery de la tabla
     * @param {Object} options - Opciones del renderizador
     */
    constructor(tableSelector, options = {}) {
        this.table = $(tableSelector);
        this.tbody = this.table.find('tbody');
        this.thead = this.table.find('thead');
        
        this.options = {
            emptyMessage: 'No hay datos para mostrar',
            loadingMessage: 'Cargando...',
            errorMessage: 'Error al cargar los datos',
            rowClass: '', // Clase CSS para las filas
            animateRows: true, // Animar la aparición de filas
            pagination: false, // Habilitar paginación
            itemsPerPage: 10,
            searchable: false, // Habilitar búsqueda
            sortable: false, // Habilitar ordenamiento
            ...options
        };

        this.currentData = [];
        this.currentPage = 1;
        this.sortColumn = null;
        this.sortDirection = 'asc';
    }

    /**
     * Renderiza los datos en la tabla
     * @param {Array<Object>} data - Datos a renderizar
     * @param {Array<Object>} columns - Definición de columnas
     * 
     * Ejemplo de columns:
     * [
     *   { field: 'id', header: 'ID', width: '50px' },
     *   { field: 'nombre', header: 'Nombre', render: (item) => item.nombre.toUpperCase() },
     *   { field: 'acciones', header: 'Acciones', render: (item) => `<button>...</button>` }
     * ]
     */
    render(data, columns) {
        this.currentData = data;
        this.columns = columns;
        
        // Limpiar tabla
        this.clear();

        // Si no hay datos, mostrar mensaje
        if (!data || data.length === 0) {
            this.showEmpty(columns.length);
            return;
        }

        // Renderizar encabezados si es necesario
        if (this.options.sortable && this.thead.find('th').length === 0) {
            this.renderHeaders(columns);
        }

        // Aplicar paginación si está habilitada
        const dataToRender = this.options.pagination 
            ? this.paginate(data)
            : data;

        // Renderizar filas
        dataToRender.forEach((item, index) => {
            const row = this.buildRow(item, columns, index);
            this.tbody.append(row);
        });

        // Animar filas si está habilitado
        if (this.options.animateRows) {
            this.animateRows();
        }

        // Renderizar controles de paginación
        if (this.options.pagination) {
            this.renderPagination();
        }
    }

    /**
     * Construye una fila de la tabla
     * @param {Object} item - Datos del item
     * @param {Array<Object>} columns - Columnas
     * @param {number} index - Índice de la fila
     * @returns {jQuery} Fila construida
     */
    buildRow(item, columns, index) {
        const cells = columns.map(col => this.buildCell(item, col)).join('');
        const rowClass = this.options.rowClass 
            ? (typeof this.options.rowClass === 'function' 
                ? this.options.rowClass(item, index) 
                : this.options.rowClass)
            : '';

        return $(`<tr class="${rowClass}" data-index="${index}">${cells}</tr>`);
    }

    /**
     * Construye una celda de la tabla
     * @param {Object} item - Datos del item
     * @param {Object} column - Definición de la columna
     * @returns {string} HTML de la celda
     */
    buildCell(item, column) {
        const { field, render, align = 'left', width, className = '' } = column;
        
        let content;
        
        // Usar función render personalizada si existe
        if (typeof render === 'function') {
            content = render(item);
        } 
        // Si es un campo simple, obtener el valor
        else if (field) {
            content = this.getNestedValue(item, field);
            if (content === null || content === undefined) {
                content = 'N/A';
            }
        } 
        // Si no hay ni render ni field
        else {
            content = '';
        }

        const styleAttr = width ? `style="width: ${width}; text-align: ${align};"` : `style="text-align: ${align};"`;
        
        return `<td class="${className}" ${styleAttr}>${content}</td>`;
    }

    /**
     * Obtiene un valor anidado de un objeto
     * @param {Object} obj - Objeto
     * @param {string} path - Ruta (ej: 'user.address.city')
     * @returns {*} Valor encontrado
     */
    getNestedValue(obj, path) {
        return path.split('.').reduce((current, key) => 
            current && current[key] !== undefined ? current[key] : undefined, obj);
    }

    /**
     * Renderiza los encabezados de la tabla (para ordenamiento)
     * @param {Array<Object>} columns - Columnas
     */
    renderHeaders(columns) {
        const headers = columns.map(col => {
            const { field, header, sortable = true, width } = col;
            const sortableClass = sortable && this.options.sortable ? 'sortable' : '';
            const styleAttr = width ? `style="width: ${width};"` : '';
            const onClick = sortable && this.options.sortable 
                ? `onclick="tableRenderer.sort('${field}')"` 
                : '';
            
            return `<th class="${sortableClass}" ${styleAttr} ${onClick}>${header}</th>`;
        }).join('');

        this.thead.html(`<tr>${headers}</tr>`);
    }

    /**
     * Limpia el contenido de la tabla
     */
    clear() {
        this.tbody.empty();
    }

    /**
     * Muestra mensaje de tabla vacía
     * @param {number} colspan - Número de columnas
     */
    showEmpty(colspan = 1) {
        this.tbody.html(`
            <tr>
                <td colspan="${colspan}" class="text-center" style="padding: 2rem;">
                    <div style="color: #999;">
                        <i class="fas fa-inbox" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                        <p>${this.options.emptyMessage}</p>
                    </div>
                </td>
            </tr>
        `);
    }

    /**
     * Muestra spinner de carga
     * @param {number} colspan - Número de columnas
     * @param {string} message - Mensaje personalizado
     */
    showLoading(colspan = 1, message = null) {
        this.tbody.html(`
            <tr>
                <td colspan="${colspan}" class="text-center" style="padding: 2rem;">
                    <div class="spinner"></div>
                    <p>${message || this.options.loadingMessage}</p>
                </td>
            </tr>
        `);
    }

    /**
     * Muestra mensaje de error
     * @param {string} errorMessage - Mensaje de error
     * @param {number} colspan - Número de columnas
     */
    showError(errorMessage, colspan = 1) {
        this.tbody.html(`
            <tr>
                <td colspan="${colspan}" class="text-center alert alert-danger" style="margin: 1rem;">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${errorMessage || this.options.errorMessage}
                </td>
            </tr>
        `);
    }

    /**
     * Anima la aparición de las filas
     */
    animateRows() {
        this.tbody.find('tr').each((index, row) => {
            $(row).css({
                opacity: 0,
                transform: 'translateY(10px)'
            }).delay(index * 50).animate({
                opacity: 1
            }, 300, function() {
                $(this).css('transform', 'translateY(0)');
            });
        });
    }

    /**
     * Aplica paginación a los datos
     * @param {Array} data - Datos completos
     * @returns {Array} Datos de la página actual
     */
    paginate(data) {
        const start = (this.currentPage - 1) * this.options.itemsPerPage;
        const end = start + this.options.itemsPerPage;
        return data.slice(start, end);
    }

    /**
     * Renderiza los controles de paginación
     */
    renderPagination() {
        const totalPages = Math.ceil(this.currentData.length / this.options.itemsPerPage);
        
        if (totalPages <= 1) return;

        const paginationHtml = `
            <div class="pagination-controls" style="display: flex; justify-content: center; align-items: center; margin-top: 1rem; gap: 0.5rem;">
                <button class="btn btn-sm btn-secondary" 
                        onclick="tableRenderer.goToPage(${this.currentPage - 1})"
                        ${this.currentPage === 1 ? 'disabled' : ''}>
                    ← Anterior
                </button>
                <span style="padding: 0 1rem;">
                    Página ${this.currentPage} de ${totalPages}
                </span>
                <button class="btn btn-sm btn-secondary" 
                        onclick="tableRenderer.goToPage(${this.currentPage + 1})"
                        ${this.currentPage === totalPages ? 'disabled' : ''}>
                    Siguiente →
                </button>
            </div>
        `;

        // Agregar o actualizar controles después de la tabla
        const $paginationContainer = this.table.parent().find('.pagination-controls');
        if ($paginationContainer.length > 0) {
            $paginationContainer.replaceWith(paginationHtml);
        } else {
            this.table.after(paginationHtml);
        }
    }

    /**
     * Navega a una página específica
     * @param {number} page - Número de página
     */
    goToPage(page) {
        const totalPages = Math.ceil(this.currentData.length / this.options.itemsPerPage);
        
        if (page < 1 || page > totalPages) return;
        
        this.currentPage = page;
        this.render(this.currentData, this.columns);
    }

    /**
     * Ordena los datos por una columna
     * @param {string} field - Campo por el cual ordenar
     */
    sort(field) {
        // Alternar dirección si es la misma columna
        if (this.sortColumn === field) {
            this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            this.sortColumn = field;
            this.sortDirection = 'asc';
        }

        // Ordenar datos
        this.currentData.sort((a, b) => {
            const aVal = this.getNestedValue(a, field);
            const bVal = this.getNestedValue(b, field);
            
            if (aVal === bVal) return 0;
            
            const comparison = aVal < bVal ? -1 : 1;
            return this.sortDirection === 'asc' ? comparison : -comparison;
        });

        // Re-renderizar
        this.render(this.currentData, this.columns);

        // Actualizar indicador visual de ordenamiento
        this.updateSortIndicators(field);
    }

    /**
     * Actualiza los indicadores visuales de ordenamiento
     * @param {string} field - Campo actual de ordenamiento
     */
    updateSortIndicators(field) {
        this.thead.find('th').each((index, th) => {
            const $th = $(th);
            $th.removeClass('sort-asc sort-desc');
            
            if ($th.attr('onclick')?.includes(field)) {
                $th.addClass(`sort-${this.sortDirection}`);
            }
        });
    }

    /**
     * Filtra los datos de la tabla
     * @param {Function|string} filter - Función de filtro o texto de búsqueda
     */
    filter(filter) {
        let filteredData;

        if (typeof filter === 'function') {
            filteredData = this.currentData.filter(filter);
        } else if (typeof filter === 'string') {
            const searchTerm = filter.toLowerCase();
            filteredData = this.currentData.filter(item => {
                return this.columns.some(col => {
                    const value = this.getNestedValue(item, col.field);
                    return value && String(value).toLowerCase().includes(searchTerm);
                });
            });
        } else {
            filteredData = this.currentData;
        }

        this.render(filteredData, this.columns);
    }

    /**
     * Agrega una fila a la tabla
     * @param {Object} item - Datos del nuevo item
     */
    addRow(item) {
        this.currentData.push(item);
        this.render(this.currentData, this.columns);
    }

    /**
     * Actualiza una fila existente
     * @param {number} index - Índice de la fila
     * @param {Object} newData - Nuevos datos
     */
    updateRow(index, newData) {
        if (index >= 0 && index < this.currentData.length) {
            this.currentData[index] = { ...this.currentData[index], ...newData };
            this.render(this.currentData, this.columns);
        }
    }

    /**
     * Elimina una fila
     * @param {number} index - Índice de la fila a eliminar
     */
    removeRow(index) {
        if (index >= 0 && index < this.currentData.length) {
            this.currentData.splice(index, 1);
            this.render(this.currentData, this.columns);
        }
    }

    /**
     * Obtiene los datos actuales
     * @returns {Array<Object>} Datos actuales
     */
    getData() {
        return this.currentData;
    }

    /**
     * Recarga la tabla con los datos actuales
     */
    reload() {
        this.render(this.currentData, this.columns);
    }

    /**
     * Exporta los datos a CSV
     * @param {string} filename - Nombre del archivo
     */
    exportToCSV(filename = 'export.csv') {
        const headers = this.columns
            .filter(col => col.field && col.header)
            .map(col => col.header);
        
        const rows = this.currentData.map(item => {
            return this.columns
                .filter(col => col.field)
                .map(col => {
                    const value = this.getNestedValue(item, col.field);
                    return `"${String(value || '').replace(/"/g, '""')}"`;
                })
                .join(',');
        });

        const csv = [headers.join(','), ...rows].join('\n');
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        
        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', filename);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }
}

/**
 * Helper para crear badges de estado
 * @param {string} estado - Estado
 * @param {Object} customClasses - Clases personalizadas
 * @returns {string} HTML del badge
 */
TableRenderer.getBadge = function(estado, customClasses = {}) {
    if (typeof BibliotecaFormatter !== 'undefined' && BibliotecaFormatter.getEstadoBadge) {
        return BibliotecaFormatter.getEstadoBadge(estado, customClasses);
    }
    
    // Fallback
    const defaultClasses = {
        'ACTIVO': 'badge-success',
        'DISPONIBLE': 'badge-success',
        'PRESTADO': 'badge-warning',
        'PENDIENTE': 'badge-warning',
        'VENCIDO': 'badge-danger',
        'DEVUELTO': 'badge-info',
        'INACTIVO': 'badge-secondary',
        ...customClasses
    };
    
    const badgeClass = defaultClasses[estado?.toUpperCase()] || 'badge-secondary';
    return `<span class="badge ${badgeClass}">${estado || 'N/A'}</span>`;
};

/**
 * Helper para crear botones de acción
 * @param {Array<Object>} actions - Acciones disponibles
 * @param {Object} item - Item actual
 * @returns {string} HTML de los botones
 */
TableRenderer.getActionButtons = function(actions, item) {
    return actions.map(action => {
        const {
            label,
            icon,
            onClick,
            className = 'btn-primary',
            condition = true
        } = action;
        
        // Evaluar condición
        const show = typeof condition === 'function' ? condition(item) : condition;
        if (!show) return '';
        
        const iconHtml = icon ? `<i class="${icon}"></i> ` : '';
        const onClickAttr = typeof onClick === 'function' 
            ? `onclick="${onClick.name}(${item.id})"` 
            : `onclick="${onClick}"`;
        
        return `
            <button class="btn btn-sm ${className}" ${onClickAttr} title="${label}">
                ${iconHtml}${label}
            </button>
        `;
    }).join(' ');
};

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = TableRenderer;
}



