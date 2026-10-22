
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Gestiona las peticiones a la página principal.
     *
     * @return Nombre de la plantilla Thymeleaf que se mostrará.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
