
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una comunidad autónoma.
 * Incluye validaciones con mensajes internacionalizados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    private Long id;

    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;

    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;

    /**
     * Constructor para crear una región sin identificador.
     *
     * @param code Código de la región.
     * @param name Nombre de la región.
     */
    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
