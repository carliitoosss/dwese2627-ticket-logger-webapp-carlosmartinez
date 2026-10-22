
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
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

        try {
            List<Region> listRegions = regionDAO.listAllRegions();
            model.addAttribute("listRegions", listRegions);

        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());

            model.addAttribute(
                    "errorMessage",
                    "Error al obtener el listado de regiones."
            );
        }

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

        model.addAttribute("region", new Region());

        return "views/region/region-form";
    }

    /**
     * Inserta una nueva región, validando los datos y
     * comprobando que el código no esté duplicado.
     *
     * @param region Región recibida desde el formulario.
     * @param bindingResult Resultado de las validaciones.
     * @param redirectAttributes Mensajes para la redirección.
     * @param locale Idioma activo.
     * @return Vista del formulario o redirección al listado.
     */
    @PostMapping("/insert")
    public String insertRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Insertando región con código {}", region.getCode());

        // Comprobar los errores de validación.
        if (bindingResult.hasErrors()) {
            logger.warn("Errores de validación al insertar la región.");
            return "views/region/region-form";
        }

        try {
            // Comprobar si el código ya existe.
            if (regionDAO.existsRegionByCode(region.getCode())) {

                logger.warn("Ya existe una región con código {}",
                        region.getCode());

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

            // Insertar la nueva región.
            regionDAO.insertRegion(region);

            logger.info("Región insertada correctamente.");

        } catch (Exception e) {
            logger.error("Error al insertar la región: {}", e.getMessage());

            String mensaje = messageSource.getMessage(
                    "msg.region-controller.insert.error",
                    null,
                    locale
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    mensaje
            );

            return "redirect:/regions/new";
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

        try {
            Region region = regionDAO.getRegionById(id);

            model.addAttribute("region", region);

            return "views/region/region-form";

        } catch (Exception e) {
            logger.error("Error al buscar la región con ID {}: {}",
                    id, e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "No se ha podido encontrar la región."
            );

            return "redirect:/regions";
        }
    }

    /**
     * Actualiza una región, validando los datos y
     * comprobando que el código no pertenezca a otra región.
     *
     * @param region Región recibida desde el formulario.
     * @param bindingResult Resultado de las validaciones.
     * @param redirectAttributes Mensajes para la redirección.
     * @param locale Idioma activo.
     * @return Vista del formulario o redirección al listado.
     */
    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        logger.info("Actualizando región con ID {}", region.getId());

        // Comprobar los errores de validación.
        if (bindingResult.hasErrors()) {
            logger.warn("Errores de validación al actualizar la región.");
            return "views/region/region-form";
        }

        try {
            // Comprobar si el código pertenece a otra región.
            if (regionDAO.existsRegionByCodeAndNotId(
                    region.getCode(), region.getId())) {

                logger.warn("El código {} ya pertenece a otra región.",
                        region.getCode());

                String mensaje = messageSource.getMessage(
                        "msg.region-controller.update.codeExist",
                        null,
                        locale
                );

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        mensaje
                );

                return "redirect:/regions/edit?id=" + region.getId();
            }

            // Actualizar la región.
            regionDAO.updateRegion(region);

            logger.info("Región actualizada correctamente.");

        } catch (Exception e) {
            logger.error("Error al actualizar la región: {}", e.getMessage());

            String mensaje = messageSource.getMessage(
                    "msg.region-controller.update.error",
                    null,
                    locale
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    mensaje
            );

            return "redirect:/regions/edit?id=" + region.getId();
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
}
