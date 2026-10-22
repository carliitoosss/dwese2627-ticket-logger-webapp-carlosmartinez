
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // Logger para registrar mensajes de la aplicación.
    private static final Logger logger =
            LoggerFactory.getLogger(HelloController.class);

    /**
     * Responde a peticiones GET en la ruta /hello.
     *
     * @param name Nombre de la persona a saludar.
     * @return Mensaje de saludo personalizado.
     */
    @GetMapping("/hello")
    public String hello(
            @RequestParam(value = "name", defaultValue = "World") String name) {

        // Registramos la petición recibida.
        logger.info("Request received to /hello endpoint with parameter name: {}", name);

        // Creamos el mensaje de saludo.
        String greeting = String.format("Hello %s!", name);

        // Registramos el mensaje antes de devolverlo.
        logger.debug("Greeting message to be returned: {}", greeting);

        return greeting;
    }
}
