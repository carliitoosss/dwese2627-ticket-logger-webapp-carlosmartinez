
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador único de la provincia.
    private Long id;

    // Código de la provincia.
    @NotEmpty(message = "{msg.province.code.notEmpty}")
    @Size(max = 10, message = "{msg.province.code.size}")
    @Column(name = "code", nullable = false,length = 2)
    private String code;

    // Nombre de la provincia.
    @NotEmpty(message = "{msg.province.name.notEmpty}")
    @Size(max = 100, message = "{msg.province.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Comunidad autónoma a la que pertenece la provincia.
    @NotNull(message = "{msg.province.region.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id",nullable = false)
    private Region region;

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
