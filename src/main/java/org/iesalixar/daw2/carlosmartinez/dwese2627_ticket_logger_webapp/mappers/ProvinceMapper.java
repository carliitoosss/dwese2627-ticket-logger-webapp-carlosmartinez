package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.mappers;


import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceUpdateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;

import java.util.List;

/**
 * Mapper utilitario entre la entidad Province y sus DTOs.
 * Implementación simple sin frameworks de mapeo.
 */
public class ProvinceMapper {

    // ==========================================
    // Entity -> DTO (listado)
    // ==========================================

    /**
     * Convierte una entidad Province a ProvinceDTO.
     */
    public static ProvinceDTO toDTO(Province entity) {
        if (entity == null) return null;

        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());

        // Solo mostramos el nombre de la región en el listado.
        dto.setRegionName(entity.getRegion().getName());

        return dto;
    }

    /**
     * Convierte una lista de Province a una lista de ProvinceDTO.
     */
    public static List<ProvinceDTO> toDTOList(List<Province> entities) {
        if (entities == null) return List.of();

        return entities.stream()
                .map(ProvinceMapper::toDTO)
                .toList();
    }

    // ==========================================
    // Entity -> DTO (detalle y edición)
    // ==========================================

    /**
     * Convierte una Province a ProvinceDetailDTO,
     * incluyendo la región asociada.
     */
    public static ProvinceDetailDTO toDetailDTO(Province entity) {
        if (entity == null) return null;

        ProvinceDetailDTO dto = new ProvinceDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegion(RegionMapper.toDTO(entity.getRegion()));

        return dto;
    }

    /**
     * Convierte una Province a ProvinceUpdateDTO
     * para precargar el formulario de edición.
     */
    public static ProvinceUpdateDTO toUpdateDTO(Province entity) {
        if (entity == null) return null;

        ProvinceUpdateDTO dto = new ProvinceUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegionId(
                entity.getRegion() != null
                        ? entity.getRegion().getId()
                        : null
        );

        return dto;
    }

    // ==========================================
    // DTO (create/update) -> Entity
    // ==========================================

    /**
     * Crea una nueva entidad Province desde ProvinceCreateDTO.
     * El ID se deja null para autogenerarse.
     */
    public static Province toEntity(ProvinceCreateDTO dto) {
        if (dto == null) return null;

        Province e = new Province();
        e.setCode(dto.getCode());
        e.setName(dto.getName());

        // Creamos una región con el ID recibido.
        Region region = new Region();
        region.setId(dto.getRegionId());

        e.setRegion(region);

        return e;
    }

    /**
     * Crea una entidad Province desde ProvinceUpdateDTO.
     * Útil para trabajar con actualizaciones por reemplazo.
     */
    public static Province toEntity(ProvinceUpdateDTO dto) {
        if (dto == null) return null;

        Province e = new Province();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());

        // Asociamos la región mediante su ID.
        Region region = new Region();
        region.setId(dto.getRegionId());

        e.setRegion(region);

        return e;
    }

    /**
     * Copia los campos editables de ProvinceUpdateDTO
     * a una entidad existente.
     *
     * No modifica el ID de la provincia.
     */
    public static void copyToExistingEntity(
            ProvinceUpdateDTO dto, Province entity) {

        if (dto == null || entity == null) return;

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());

        // Actualizamos la referencia a la región.
        Region region = new Region();
        region.setId(dto.getRegionId());

        entity.setRegion(region);

        // No tocar entity.setId(...)
    }
}

