
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

/**
 * Configuración de la internacionalización de la aplicación.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    private static final Logger logger =
            LoggerFactory.getLogger(LocaleConfig.class);

    /**
     * Configura el idioma predeterminado de la aplicación.
     * Utiliza la sesión HTTP para recordar el idioma elegido.
     *
     * @return Resolutor de idioma configurado.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();

        // Español como idioma por defecto
        slr.setDefaultLocale(Locale.of("es"));

        logger.info("LocaleResolver configurado con el idioma predeterminado: es");

        return slr;
    }

    /**
     * Permite cambiar el idioma mediante el parámetro lang.
     *
     * @return Interceptor para cambiar el idioma.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor =
                new LocaleChangeInterceptor();

        // Ejemplo: ?lang=es o ?lang=en
        interceptor.setParamName("lang");

        logger.info("LocaleChangeInterceptor configurado con el parámetro 'lang'");

        return interceptor;
    }

    /**
     * Registra el interceptor para las peticiones HTTP.
     *
     * @param registry Registro de interceptores de Spring.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());

        logger.info("LocaleChangeInterceptor registrado correctamente.");
    }
}
