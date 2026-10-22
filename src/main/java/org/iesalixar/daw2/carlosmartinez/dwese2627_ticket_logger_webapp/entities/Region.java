
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Representa una comunidad autónoma de España.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {


    private Long id;

    // Validaciones del código
    @NotEmpty(message = "El código no puede estar vacío")
    @Size(max = 2, message = "El código no puede tener más de 2 caracteres")
    private String code;

    // Validaciones del nombre
    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;


    /**
     * Constructor para crear regiones sin ID.
     *
     * @param code Código de la región.
     * @param name Nombre de la región.
     */
    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
