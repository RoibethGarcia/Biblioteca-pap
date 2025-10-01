/**
 * Landing Page JavaScript
 * Funcionalidades para la página de bienvenida
 */

$(document).ready(function() {
    'use strict';
    
    // Smooth scrolling para enlaces internos
    $('a[href^="#"]').on('click', function(e) {
        e.preventDefault();
        
        const target = $(this.getAttribute('href'));
        if (target.length) {
            $('html, body').animate({
                scrollTop: target.offset().top - 80
            }, 800);
        }
    });
    
    // Efecto parallax suave en el hero
    $(window).scroll(function() {
        const scrolled = $(window).scrollTop();
        const parallax = $('.hero-illustration');
        const speed = 0.5;
        
        parallax.css('transform', `translateY(${scrolled * speed}px)`);
    });
    
    // Estadísticas removidas - ya no se necesitan animaciones de contadores
    
    // Animación de entrada para las tarjetas de servicios
    function animateServiceCards() {
        $('.service-card').each(function(index) {
            const $card = $(this);
            const cardTop = $card.offset().top;
            const cardBottom = cardTop + $card.outerHeight();
            const viewportTop = $(window).scrollTop();
            const viewportBottom = viewportTop + $(window).height();
            
            if (cardBottom > viewportTop && cardTop < viewportBottom) {
                if (!$card.hasClass('animate-in')) {
                    $card.addClass('animate-in');
                    $card.css({
                        'animation-delay': (index * 0.1) + 's'
                    });
                }
            }
        });
    }
    
    // Verificar animación de tarjetas
    animateServiceCards();
    $(window).on('scroll', animateServiceCards);
    
    // Efecto hover mejorado para las tarjetas de servicios
    $('.service-card').hover(
        function() {
            $(this).find('.service-icon').css('transform', 'scale(1.1) rotate(5deg)');
        },
        function() {
            $(this).find('.service-icon').css('transform', 'scale(1) rotate(0deg)');
        }
    );
    
    // Navegación móvil
    $('.nav-toggle').on('click', function() {
        $(this).toggleClass('active');
        $('.nav-links').toggleClass('active');
    });
    
    // Cerrar menú móvil al hacer clic en un enlace
    $('.nav-link').on('click', function() {
        $('.nav-toggle').removeClass('active');
        $('.nav-links').removeClass('active');
    });
    
    // Cambiar estilo del header al hacer scroll
    $(window).scroll(function() {
        const scroll = $(window).scrollTop();
        
        if (scroll >= 50) {
            $('.header').addClass('scrolled');
        } else {
            $('.header').removeClass('scrolled');
        }
    });
    
    // Efecto typing para el título principal
    function typeWriter(element, text, speed = 100) {
        let i = 0;
        element.text('');
        
        function type() {
            if (i < text.length) {
                element.text(element.text() + text.charAt(i));
                i++;
                setTimeout(type, speed);
            }
        }
        
        type();
    }
    
    // Activar efecto typing solo una vez
    let typingActivated = false;
    function checkTypingEffect() {
        const heroTitle = $('.hero-title');
        const heroTop = heroTitle.offset().top;
        const heroBottom = heroTop + heroTitle.outerHeight();
        const viewportTop = $(window).scrollTop();
        const viewportBottom = viewportTop + $(window).height();
        
        if (heroBottom > viewportTop && heroTop < viewportBottom && !typingActivated) {
            typingActivated = true;
            const originalText = heroTitle.text();
            typeWriter(heroTitle, originalText, 50);
        }
    }
    
    checkTypingEffect();
    $(window).on('scroll', checkTypingEffect);
    
    // Animación de entrada para elementos con fade-in
    function fadeInOnScroll() {
        $('.fade-in').each(function() {
            const elementTop = $(this).offset().top;
            const elementBottom = elementTop + $(this).outerHeight();
            const viewportTop = $(window).scrollTop();
            const viewportBottom = viewportTop + $(window).height();
            
            if (elementBottom > viewportTop && elementTop < viewportBottom) {
                $(this).addClass('visible');
            }
        });
    }
    
    fadeInOnScroll();
    $(window).on('scroll', fadeInOnScroll);
    
    // Preloader (opcional)
    $(window).on('load', function() {
        $('.preloader').fadeOut(500);
    });
    
    // Validación de formularios (si los hay)
    $('form').on('submit', function(e) {
        e.preventDefault();
        
        // Aquí se puede agregar validación de formularios
        console.log('Formulario enviado');
    });
    
    // Tooltips para iconos sociales
    $('.social-link').hover(
        function() {
            const platform = $(this).find('i').attr('class').split('fa-')[1];
            $(this).attr('title', `Síguenos en ${platform.charAt(0).toUpperCase() + platform.slice(1)}`);
        }
    );
    
    // Efecto de partículas en el fondo (opcional)
    function createParticles() {
        const hero = $('.hero');
        const particleCount = 50;
        
        for (let i = 0; i < particleCount; i++) {
            const particle = $('<div class="particle"></div>');
            particle.css({
                left: Math.random() * 100 + '%',
                animationDelay: Math.random() * 20 + 's',
                animationDuration: (Math.random() * 10 + 10) + 's'
            });
            hero.append(particle);
        }
    }
    
    // Activar partículas solo en pantallas grandes
    if ($(window).width() > 768) {
        createParticles();
    }
    
    // Lazy loading para imágenes (si se agregan)
    function lazyLoadImages() {
        $('img[data-src]').each(function() {
            const $img = $(this);
            const imgTop = $img.offset().top;
            const imgBottom = imgTop + $img.outerHeight();
            const viewportTop = $(window).scrollTop();
            const viewportBottom = viewportTop + $(window).height();
            
            if (imgBottom > viewportTop && imgTop < viewportBottom) {
                $img.attr('src', $img.data('src'));
                $img.removeAttr('data-src');
            }
        });
    }
    
    lazyLoadImages();
    $(window).on('scroll', lazyLoadImages);
    
    // Resize handler
    $(window).on('resize', function() {
        // Recalcular animaciones en resize
        setTimeout(() => {
            checkCountersVisibility();
            animateServiceCards();
            fadeInOnScroll();
        }, 100);
    });
    
    // Console log para debugging
    console.log('🚀 Landing Page cargada correctamente');
    console.log('📱 Biblioteca PAP - Sistema de Gestión');
});

// CSS adicional para animaciones
const additionalCSS = `
<style>
.service-card {
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.6s ease;
}

.service-card.animate-in {
    opacity: 1;
    transform: translateY(0);
    animation: slideInUp 0.6s ease forwards;
}

@keyframes slideInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.header.scrolled {
    background: rgba(255, 255, 255, 0.98);
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.fade-in {
    opacity: 0;
    transform: translateY(20px);
    transition: all 0.6s ease;
}

.fade-in.visible {
    opacity: 1;
    transform: translateY(0);
}

.particle {
    position: absolute;
    width: 2px;
    height: 2px;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    animation: float-particle linear infinite;
    pointer-events: none;
}

@keyframes float-particle {
    0% {
        transform: translateY(100vh) rotate(0deg);
        opacity: 0;
    }
    10% {
        opacity: 1;
    }
    90% {
        opacity: 1;
    }
    100% {
        transform: translateY(-100px) rotate(360deg);
        opacity: 0;
    }
}

.nav-links.active {
    display: flex;
    flex-direction: column;
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
    padding: 1rem;
}

.nav-toggle.active .hamburger {
    background: transparent;
}

.nav-toggle.active .hamburger::before {
    transform: rotate(45deg);
    top: 0;
}

.nav-toggle.active .hamburger::after {
    transform: rotate(-45deg);
    top: 0;
}

@media (max-width: 768px) {
    .nav-links {
        display: none;
    }
    
    .nav-actions {
        display: none;
    }
}
</style>
`;

// Inyectar CSS adicional
$('head').append(additionalCSS);
