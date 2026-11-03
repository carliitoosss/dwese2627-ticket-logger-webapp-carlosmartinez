package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de detalle de Provincia
 * Incluye la region a la que pertenece para poder navegar desde el detalle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProvinceDetailDTO {
    private Long id;
    private String code;
    private String name;

    /** Region asociada a la provincia (objeto embebido para el detalle).*/
    private RegionDTO region;
}
