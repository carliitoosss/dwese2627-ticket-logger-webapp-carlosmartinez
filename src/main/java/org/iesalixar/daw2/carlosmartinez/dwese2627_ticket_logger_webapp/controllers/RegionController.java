
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.RegionCreateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.RegionDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.RegionDetailDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.mappers.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * Controlador para gestionar las operaciones CRUD de regiones.
 */
@Controller
@RequestMapping("/regions")
public class RegionController {

    private static final Logger logger =
            LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionDAO regionDAO;

    // Permite obtener mensajes según el idioma seleccionado.
    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado de todas las regiones.
     *
     * @param model Modelo de la vista.
     * @return Vista con el listado de regiones.
     */
    @GetMapping
    public String listRegions(Model model) {
        logger.info("Obteniendo el listado de regiones.");
        List<Region> listRegions=null;
        List<RegionDTO> listRegionsDTOs=null;
        try {
            listRegions = regionDAO.listAllRegions();
            listRegionsDTOs = RegionMapper.toDTOList(listRegions);
            logger.info("Se han cargado {} regiones.", listRegionsDTOs.size());

        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());

            model.addAttribute(
                    "errorMessage",
                    "Error al obtener el listado de regiones."
            );
        }
        model.addAttribute("listReions", listRegionsDTOs);
        return "views/region/region-list";
    }

    /**
     * Muestra el formulario para crear una región.
     *
     * @param model Modelo de la vista.
     * @return Vista del formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario de nueva región.");

        model.addAttribute("region", new RegionCreateDTO(   ));

        return "views/region/region-form";
    }

    /**
     * Inserta una nueva región, validando los datos y
     * comprobando que el código no esté duplicado.
     *
     * @param regionDTO Región recibida desde el formulario.
     * @param result Resultado de las validaciones.
     * @param redirectAttributes Mensajes para la redirección.
     * @param locale Idioma activo.
     * @return Vista del formulario o redirección al listado.
     */
    @PostMapping("/insert")
    public String insertRegion(
            @Valid @ModelAttribute("region") RegionCreateDTO regionDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Insertando región con código {}", regionDTO.getCode());



        try {
            // Comprobar los errores de validación.
            if (result.hasErrors()) {
                logger.warn("Errores de validación al insertar la región.");
                return "views/region/region-form";
            }
            // Comprobar si el código ya existe.
            if (regionDAO.existsRegionByCode(regionDTO.getCode())) {

                logger.warn("Ya existe una región con código {}",
                        regionDTO.getCode());

                String mensaje = messageSource.getMessage(
                        "msg.region-controller.insert.codeExist",
                        null,
                        locale
                );

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        mensaje
                );

                return "redirect:/regions/new";
            }
            Region region = RegionMapper.toEntity(regionDTO);
            // Insertar la nueva región.
            regionDAO.insertRegion(region);

            logger.info("Región {} insertada correctamente.", region.getCode());

        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}",regionDTO.getCode() ,e.getMessage());

            String mensaje = messageSource.getMessage(
                    "msg.region-controller.insert.error",
                    null,
                    locale
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    mensaje
            );
        }

        return "redirect:/regions";
    }

    /**
     * Muestra el formulario de edición de una región.
     *
     * @param id Identificador de la región.
     * @param model Modelo de la vista.
     * @param redirectAttributes Mensajes para la redirección.
     * @return Vista del formulario o redirección al listado.
     */
    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Mostrando formulario de edición de región con ID {}", id);
        Region region=null;
        RegionUpdateDTO regionDTO=null;

        try {
            region = regionDAO.getRegionById(id);
            if(region==null){
                logger.warn("No se encontro la region con ID:{}", id);
            }
            regionDTO=RegionMapper.toUpdateDTO(region);
        } catch (Exception e) {
            logger.error("Error al buscar la región con ID {}: {}",
                    id, e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "No se ha podido encontrar la región."
            );
        }
        model.addAttribute("region", regionDTO);
        return "redirect:/regions";
    }

    /**
     * Actualiza una región, validando los datos y
     * comprobando que el código no pertenezca a otra región.
     *
     * @param regionDTO Región recibida desde el formulario.
     * @param result Resultado de las validaciones.
     * @param redirectAttributes Mensajes para la redirección.
     * @param locale Idioma activo.
     * @return Vista del formulario o redirección al listado.
     */
    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") RegionUpdateDTO regionDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Actualizando región con ID {}", regionDTO.getId());

        try {
            // Comprobar los errores de validación.
            if (result.hasErrors()) {
                return "views/region/region-form";
            }
            // Comprobar si el código pertenece a otra región.
            if (regionDAO.existsRegionByCodeAndNotId(
                    regionDTO.getCode(), regionDTO.getId())) {

                logger.warn("El código {} ya pertenece a otra región.",
                        regionDTO.getCode());

                String mensaje = messageSource.getMessage(
                        "msg.region-controller.update.codeExist",
                        null,
                        locale
                );

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        mensaje
                );
                return "redirect:/regions/edit?id=" + regionDTO.getId();
            }
            // Cargar entidad existente y copiar campos editables desde el DTO
            Region region = regionDAO.getRegionById(regionDTO.getId());
            if (region == null) {
                logger.warn("No se encontró la región con ID {}", regionDTO.getId());
                String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/regions";
            }
            RegionMapper.copyToExistingEntity(regionDTO, region);
            // Actualizar la región.
            regionDAO.updateRegion(region);

            logger.info("Región con ID: {} actualizada correctamente.", regionDTO.getId());

        } catch (Exception e) {
            logger.error("Error al actualizar la región {}: {}", regionDTO.getId(),e.getMessage());

            String mensaje = messageSource.getMessage(
                    "msg.region-controller.update.error",
                    null,
                    locale
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    mensaje
            );
        }
        return "redirect:/regions";
    }

    /**
     * Elimina una región de la base de datos.
     *
     * @param id Identificador de la región.
     * @param redirectAttributes Mensajes para la redirección.
     * @return Redirección al listado.
     */
    @PostMapping("/delete")
    public String deleteRegion(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        logger.info("Eliminando región con ID {}", id);

        try {
            regionDAO.deleteRegion(id);

            logger.info("Región con ID {} eliminada correctamente.", id);

        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}",
                    id, e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al eliminar la región."
            );
        }

        return "redirect:/regions";
    }
    /**
     * Muestra el detalle de una región específica, incluyendo su lista de provincias asociadas.
     *
     * @param id Identificador único de la región que se desea consultar.
     * @param model Modelo de Spring MVC utilizado para pasar datos a la vista.
     * @param redirectAttributes Objeto para enviar mensajes flash de error o información al redirigir.
     * @param locale Configuración regional actual del usuario (para internacionalización de mensajes).
     * @return El nombre de la plantilla Thymeleaf que muestra el detalle de la región
     * ({@code views/region/region-detail}), o una redirección a {@code /regions} en caso de error.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la región con ID {}", id);
        try {
            // Cargar la región junto con sus provincias (fetch) para evitar LazyInitializationException
            Region region = regionDAO.getRegionById(id);
            if (region == null) {
                String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/regions";
            }
            RegionDetailDTO regionDTO = RegionMapper.toDetailDTO(region);
            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }
}
