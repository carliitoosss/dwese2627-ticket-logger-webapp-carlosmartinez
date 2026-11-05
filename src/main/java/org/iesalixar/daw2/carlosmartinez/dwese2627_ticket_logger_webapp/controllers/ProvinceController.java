
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.ProvinceDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.*;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.mappers.ProvinceMapper;
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
 * Controlador Spring MVC para gestionar las provincias.
 */
@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceDAO provinceDAO;

    @Autowired
    private RegionDAO regionDAO;

    /**
     * Lista todas las provincias con sus comunidades autónomas.
     */
    @GetMapping
    public String listProvinces(Model model, Locale locale) {

        logger.info("Solicitando el listado de provincias.");

        try {
            List<Province> listProvinces = provinceDAO.listAllProvinces();
            List<ProvinceDTO> listProvincesDTOs = ProvinceMapper.toDTOList(listProvinces);
            logger.info("Se han cargado {} provincias.", listProvincesDTOs.size());
            model.addAttribute("listProvinces", listProvinces);

        } catch (Exception e) {

            logger.error("Error al listar las provincias: {}", e.getMessage());

            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.list.error",
                    null,
                    locale
            );

            model.addAttribute("errorMessage", errorMessage);
        }

        return "views/province/province-list";
    }

    /**
     * Muestra el formulario de creación de provincias.
     */
    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {

        logger.info("Mostrando formulario de nueva provincia.");

        try {
            List<Region> listRegions = regionDAO.listAllRegions();
            List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(listRegions);
            model.addAttribute("province", new ProvinceCreateDTO());
            model.addAttribute("listRegions", listRegionsDTOs);
        } catch (Exception e) {

            logger.error("Error al cargar las regiones para el formulario de province: {}",
                    e.getMessage());

            String errorMessage= messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }

    /**
     * Inserta una nueva provincia tras validar sus datos.
     * @param provinceDTO objeto con los datos del formulario.
     * @param result Resultado de validacion.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional actual para mensajes,
     * @return Redireccion al listado de provincias.
     */
    @PostMapping("/insert")
    public String insertProvince(
            @Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            Locale locale) {

        logger.info("Intentando insertar provincia con código {}", provinceDTO.getCode());

        try {
            if (result.hasErrors()) {
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegionsDTOs);
                return "views/province/province-form";
            }
            if(provinceDAO.existsProvinceByCode(provinceDTO.getCode())){
                logger.warn("El codigo de la provincia {} ya existe. ", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.insertProvince(province);
            logger.info("Provincia {} insertada con exito,", province.getCode());
        } catch (Exception e) {
            logger.error("Error al cargar las regiones: {}", e.getMessage());
            String errorMessage=messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

    /**
     * Muestra el formulario de edición de provincias.
     *
     * @param id ID de la provincia a editar.
     * @param model modelo para pasar datos a la vista.
     * @return plantilla thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            Locale locale) {

        logger.info("Mostrando formulario de edición de provincia con ID {}", id);

        try {
            Province province = provinceDAO.getProvinceById(id);
            ProvinceUpdateDTO provinceDTO=ProvinceMapper.toUpdateDTO(province);
            if(province==null){
                logger.warn("No se encontro la provincia con ID: {}", id);
                String errorMessage= messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
                model.addAttribute("errorMessage", errorMessage);
            }else{
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTOs=RegionMapper.toDTOList(listRegions);
                model.addAttribute("province", provinceDTO);
                model.addAttribute("listRegions", listRegionsDTOs);
            }
        } catch (Exception e) {

            logger.error("Error al cargar la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage=messageSource.getMessage("msg.province-controller.edit.form",null,locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }


    /**
     * Actualiza una provincia existente en la base de datos.
     *
     * @param provinceDTO        Provincia con los datos actualizados.
     * @param result             Resultado de la validación.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale             Configuración regional actual.
     * @return Redirección al listado de provincias.
     */
    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Actualizando provincia con ID {}", provinceDTO.getId());

        try {
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegionsDTOs);

                return "views/province/province-form";
            }

            if (provinceDAO.existsProvinceByCodeAndNotId(provinceDTO.getCode(), provinceDTO.getId())) {
                logger.warn("El código de la provincia {} ya existe para otra provincia.", provinceDTO.getCode());

                String errorMessage = messageSource.getMessage(
                        "msg.province-controller.update.codeExist", null, locale);

                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

                return "redirect:/provinces/edit?id=" + provinceDTO.getId();
            }

            // Mapear DTO -> Entity y persistir
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.updateProvince(province);

            logger.info("Provincia con ID {} actualizada con éxito.", province.getId());

        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID: {}", provinceDTO.getId(), e.getMessage());

            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.update.error", null, locale);

            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/provinces";
    }

    /**
     * Elimina una provincia de la base de datos.
     *
     * @param id Identificador de la provincia.
     * @param redirectAttributes Mensajes de redirección.
     * @return Redirección al listado de provincias.
     */
    @PostMapping("/delete")
    public String deleteProvince(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        logger.info("Eliminando provincia con ID {}", id);

        try {

            provinceDAO.deleteProvince(id);

            logger.info("Provincia con ID {} eliminada correctamente.", id);

        } catch (Exception e) {

            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la provincia.");
        }

        return "redirect:/provinces";
    }

    /**
     * Muestra el detalle de una provincia específica, incluyendo su región asociada.
     *
     * @param id                 Identificador de la provincia a consultar.
     * @param model              Modelo de Spring MVC para pasar datos a la vista.
     * @param redirectAttributes Mensajes flash al redirigir (errores/avisos).
     * @param locale             Configuración regional para internacionalización de mensajes.
     * @return Plantilla Thymeleaf {@code views/province/province-detail} o redirección a {@code /provinces}.
     */

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        logger.info("Mostrando detalle de la provincia con ID {}", id);

        try {
            Province province = provinceDAO.getProvinceById(id);
            ProvinceDetailDTO provinceDTO = ProvinceMapper.toDetailDTO(province);

            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);

                String msg = messageSource.getMessage(
                        "msg.province-controller.detail.notFound", null, locale);

                redirectAttributes.addFlashAttribute("errorMessage", msg);

                return "redirect:/provinces";
            }

            model.addAttribute("province", provinceDTO);

            return "views/province/province-detail";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la provincia {}: {}", id, e.getMessage(), e);

            String msg = messageSource.getMessage(
                    "msg.province-controller.detail.error", null, locale);

            redirectAttributes.addFlashAttribute("errorMessage", msg);

            return "redirect:/provinces";
        }
    }
}
