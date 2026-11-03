package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RegionDTO {
    private Long id;
    private String name;
    private String code;
}
