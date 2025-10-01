/**
 * Biblioteca PAP - Lazy Loading y Optimización de Rendimiento
 */

const BibliotecaPerformance = {
    
    // Configuración
    config: {
        lazyImagesSelector: 'img[data-src]',
        lazyThreshold: 50, // px antes de cargar
        imagePlaceholder: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPkNhcmdhbmRvLi4uPC90ZXh0Pjwvc3ZnPg=='
    },
    
    // Inicialización
    init: function() {
        this.setupLazyLoading();
        this.setupIntersectionObserver();
        this.optimizeImages();
        this.setupPreloading();
    },
    
    // Configurar lazy loading
    setupLazyLoading: function() {
        // Agregar placeholder a todas las imágenes lazy
        $(this.config.lazyImagesSelector).each((index, img) => {
            if (!img.src) {
                img.src = this.config.imagePlaceholder;
            }
        });
    },
    
    // Configurar Intersection Observer para lazy loading
    setupIntersectionObserver: function() {
        if ('IntersectionObserver' in window) {
            const imageObserver = new IntersectionObserver((entries, observer) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        this.loadImage(img);
                        observer.unobserve(img);
                    }
                });
            }, {
                rootMargin: `${this.config.lazyThreshold}px`
            });
            
            // Observar todas las imágenes lazy
            $(this.config.lazyImagesSelector).each((index, img) => {
                imageObserver.observe(img);
            });
        } else {
            // Fallback para navegadores que no soportan IntersectionObserver
            this.loadAllImages();
        }
    },
    
    // Cargar imagen
    loadImage: function(img) {
        const src = img.dataset.src;
        if (src) {
            const tempImg = new Image();
            tempImg.onload = () => {
                img.src = src;
                img.classList.add('loaded');
                this.addFadeInEffect(img);
            };
            tempImg.onerror = () => {
                img.classList.add('error');
                console.warn('Error cargando imagen:', src);
            };
            tempImg.src = src;
        }
    },
    
    // Cargar todas las imágenes (fallback)
    loadAllImages: function() {
        $(this.config.lazyImagesSelector).each((index, img) => {
            this.loadImage(img);
        });
    },
    
    // Agregar efecto fade-in a las imágenes
    addFadeInEffect: function(img) {
        img.style.opacity = '0';
        img.style.transition = 'opacity 0.3s ease-in-out';
        
        setTimeout(() => {
            img.style.opacity = '1';
        }, 100);
    },
    
    // Optimizar imágenes
    optimizeImages: function() {
        // Comprimir imágenes grandes
        $('img').each((index, img) => {
            if (img.naturalWidth > 800) {
                img.style.maxWidth = '100%';
                img.style.height = 'auto';
            }
        });
    },
    
    // Preloading de recursos críticos
    setupPreloading: function() {
        // Preload de CSS crítico
        this.preloadCSS();
        
        // Preload de fuentes
        this.preloadFonts();
        
        // Preload de imágenes críticas
        this.preloadCriticalImages();
    },
    
    // Preload CSS crítico
    preloadCSS: function() {
        const criticalCSS = [
            'css/style.css',
            'css/spa.css'
        ];
        
        criticalCSS.forEach(href => {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.as = 'style';
            link.href = href;
            document.head.appendChild(link);
        });
    },
    
    // Preload de fuentes
    preloadFonts: function() {
        const fontURLs = [
            'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap'
        ];
        
        fontURLs.forEach(url => {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.as = 'style';
            link.href = url;
            document.head.appendChild(link);
        });
    },
    
    // Preload de imágenes críticas
    preloadCriticalImages: function() {
        const criticalImages = [
            // Agregar URLs de imágenes críticas aquí
        ];
        
        criticalImages.forEach(src => {
            const img = new Image();
            img.src = src;
        });
    },
    
    // Optimizar rendimiento de scroll
    optimizeScroll: function() {
        let ticking = false;
        
        const updateScroll = () => {
            // Aquí se pueden agregar optimizaciones de scroll
            ticking = false;
        };
        
        window.addEventListener('scroll', () => {
            if (!ticking) {
                requestAnimationFrame(updateScroll);
                ticking = true;
            }
        });
    },
    
    // Debounce para funciones costosas
    debounce: function(func, wait, immediate) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                timeout = null;
                if (!immediate) func(...args);
            };
            const callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func(...args);
        };
    },
    
    // Throttle para funciones que se ejecutan frecuentemente
    throttle: function(func, limit) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },
    
    // Medir rendimiento
    measurePerformance: function() {
        if ('performance' in window) {
            window.addEventListener('load', () => {
                setTimeout(() => {
                    const perfData = performance.getEntriesByType('navigation')[0];
                    console.log('Performance metrics:', {
                        loadTime: perfData.loadEventEnd - perfData.loadEventStart,
                        domContentLoaded: perfData.domContentLoadedEventEnd - perfData.domContentLoadedEventStart,
                        firstPaint: performance.getEntriesByType('paint')[0]?.startTime || 'N/A'
                    });
                }, 0);
            });
        }
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaPerformance.init();
});

// Hacer disponible globalmente
window.BibliotecaPerformance = BibliotecaPerformance;
