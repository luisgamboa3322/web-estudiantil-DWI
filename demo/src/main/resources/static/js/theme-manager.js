/**
 * SISTEMA DE MODO OSCURO - UTP+class
 * Maneja el cambio entre tema claro y oscuro con persistencia en localStorage
 */

(function() {
    'use strict';

    // Constantes
    const THEME_KEY = 'utp-theme';
    const THEME_DARK = 'dark';
    const THEME_LIGHT = 'light';

    /**
     * Obtiene el tema guardado o el preferido del sistema
     */
    function getSavedTheme() {
        const savedTheme = localStorage.getItem(THEME_KEY);
        
        if (savedTheme) {
            return savedTheme;
        }

        // Si no hay tema guardado, usar preferencia del sistema
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            return THEME_DARK;
        }

        return THEME_LIGHT;
    }

    /**
     * Aplica el tema al documento
     */
    function applyTheme(theme) {
        if (theme === THEME_DARK) {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }

        // Actualizar todos los toggles en la página
        const toggles = document.querySelectorAll('.theme-toggle input[type="checkbox"]');
        toggles.forEach(toggle => {
            toggle.checked = (theme === THEME_DARK);
        });

        // Guardar en localStorage
        localStorage.setItem(THEME_KEY, theme);

        // Disparar evento personalizado para que otros componentes puedan reaccionar
        window.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme } }));
    }

    /**
     * Alterna entre tema claro y oscuro
     */
    function toggleTheme() {
        const currentTheme = getSavedTheme();
        const newTheme = currentTheme === THEME_DARK ? THEME_LIGHT : THEME_DARK;
        applyTheme(newTheme);
    }

    /**
     * Inicializa el sistema de temas
     */
    function initTheme() {
        // Aplicar tema guardado inmediatamente (antes de que cargue la página)
        const savedTheme = getSavedTheme();
        applyTheme(savedTheme);

        // Escuchar cambios en la preferencia del sistema
        if (window.matchMedia) {
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
                // Solo cambiar si el usuario no ha establecido una preferencia manual
                if (!localStorage.getItem(THEME_KEY)) {
                    applyTheme(e.matches ? THEME_DARK : THEME_LIGHT);
                }
            });
        }
    }

    /**
     * Configura los event listeners para los toggles
     */
    function setupToggleListeners() {
        const toggles = document.querySelectorAll('.theme-toggle input[type="checkbox"]');
        
        toggles.forEach(toggle => {
            toggle.addEventListener('change', function() {
                toggleTheme();
            });
        });
    }

    // Inicializar tema INMEDIATAMENTE (antes de DOMContentLoaded)
    initTheme();

    // Configurar listeners cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', setupToggleListeners);
    } else {
        setupToggleListeners();
    }

    // Exponer funciones globalmente para uso en otras partes de la aplicación
    window.ThemeManager = {
        toggle: toggleTheme,
        set: applyTheme,
        get: getSavedTheme,
        isDark: () => getSavedTheme() === THEME_DARK,
        isLight: () => getSavedTheme() === THEME_LIGHT
    };

    // Log para debugging (remover en producción)
    console.log('🎨 Theme Manager initialized. Current theme:', getSavedTheme());

})();
