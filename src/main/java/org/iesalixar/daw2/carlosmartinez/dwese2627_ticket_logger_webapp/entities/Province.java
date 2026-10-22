
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una provincia española dentro del sistema.
 *
 * Cada provincia tiene un identificador, un código, un nombre
 * y una comunidad autónoma a la que pertenece.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province {

    // Identificador único de la provincia.
    private Long id;

    // Código de la provincia.
    @NotEmpty(message = "{msg.province.code.notEmpty}")
    @Size(max = 10, message = "{msg.province.code.size}")
    private String code;

    // Nombre de la provincia.
    @NotEmpty(message = "{msg.province.name.notEmpty}")
    @Size(max = 100, message = "{msg.province.name.size}")
    private String name;

    // Comunidad autónoma a la que pertenece la provincia.
    private Region region;

    /**
     * Comprueba que se haya seleccionado una comunidad autónoma
     * válida y que esta tenga un identificador.
     *
     * @return true si la región es válida.
     */
    @AssertTrue(message = "{msg.province.region.notNull}")
    public boolean isRegionSelected() {
        return region != null && region.getId() != null;
    }

    /**
     * Constructor para crear provincias sin identificador.
     *
     * @param code Código de la provincia.
     * @param name Nombre de la provincia.
     * @param region Comunidad autónoma asociada.
     */
    public Province(String code, String name, Region region) {
        this.code = code;
        this.name = name;
        this.region = region;
    }
}
