
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.ProvinceDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.ProvinceUpdateDTO;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.dtos.RegionDTO;
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
                List<Region> listRegions = RegionDAO.listAllRegions();
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
        } catch (Exception e) {

            logger.error("Error al cargar las regiones: {}",
                    e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al cargar las comunidades autónomas."
            );

            return "redirect:/provinces";
        }


        try {

            if (provinceDAO.existsProvinceByCode(province.getCode())) {

                logger.warn("Ya existe una provincia con código {}",
                        province.getCode());

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Ya existe una provincia con ese código."
                );

                return "redirect:/provinces/new";
            }

            provinceDAO.insertProvince(province);

            logger.info("Provincia insertada correctamente.");

            return "redirect:/provinces";

        } catch (Exception e) {

            logger.error("Error al insertar la provincia: {}",
                    e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al insertar la provincia."
            );

            return "redirect:/provinces/new";
        }
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
     * Actualiza una provincia existente.
     */
    @PostMapping("/update")
    public String updateProvince(
            @Valid @ModelAttribute("province") Province province,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Actualizando provincia con ID {}",
                province.getId());

        if (bindingResult.hasErrors()) {

            logger.warn("Errores de validación al actualizar la provincia.");

            try {

                model.addAttribute(
                        "listRegions",
                        regionDAO.listAllRegions()
                );

                return "views/province/province-form";

            } catch (Exception e) {

                logger.error("Error al cargar las regiones: {}",
                        e.getMessage());

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Error al cargar las comunidades autónomas."
                );

                return "redirect:/provinces";
            }
        }

        try {

            if (provinceDAO.existsProvinceByCodeAndNotId(
                    province.getCode(), province.getId())) {

                logger.warn("El código {} pertenece a otra provincia.",
                        province.getCode());

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Ya existe otra provincia con ese código."
                );

                return "redirect:/provinces/edit?id=" + province.getId();
            }

            provinceDAO.updateProvince(province);

            logger.info("Provincia actualizada correctamente.");

            return "redirect:/provinces";

        } catch (Exception e) {

            logger.error("Error al actualizar la provincia: {}",
                    e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al actualizar la provincia."
            );

            return "redirect:/provinces/edit?id=" + province.getId();
        }
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

            logger.info("Provincia con ID {} eliminada correctamente.",
                    id);

        } catch (Exception e) {

            logger.error("Error al eliminar la provincia con ID {}: {}",
                    id, e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al eliminar la provincia."
            );
        }

        return "redirect:/provinces";
    }

    /**
     * Muestra el detalle de una provincia concreta.
     *
     * @param id Identificador de la provincia.
     * @param model Modelo para pasar los datos a la vista.
     * @param redirectAttributes Atributos para mensajes de redirección.
     * @param locale Idioma actual.
     * @return Nombre de la plantilla o redirección.
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {

        logger.info("Mostrando detalle de la provincia con ID {}", id);

        try {
            Province province = provinceDAO.getProvinceById(id);

            if (province == null) {
                logger.warn("Provincia con ID {} no encontrada", id);

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        messageSource.getMessage(
                                "msg.province-controller.detail.notFound",
                                null,
                                locale
                        )
                );

                return "redirect:/provinces";
            }

            model.addAttribute("province", province);
            return "views/province/province-detail";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la provincia con ID {}", id, e);

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    messageSource.getMessage(
                            "msg.province-controller.detail.error",
                            null,
                            locale
                    )
            );

            return "redirect:/provinces";
        }
    }
}
