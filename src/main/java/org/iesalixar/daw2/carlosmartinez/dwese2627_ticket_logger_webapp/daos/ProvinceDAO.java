
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos;

import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Province;

import java.util.List;

/**
 * Interfaz DAO para gestionar las provincias.
 */
public interface ProvinceDAO {

    /**
     * Obtiene todas las provincias con sus regiones asociadas.
     *
     * @return Lista de provincias.
     */
    List<Province> listAllProvinces();

    /**
     * Inserta una nueva provincia.
     *
     * @param province Provincia que se desea insertar.
     */
    void insertProvince(Province province);

    /**
     * Actualiza una provincia existente.
     *
     * @param province Provincia con los datos actualizados.
     */
    void updateProvince(Province province);

    /**
     * Recupera una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @return Provincia con su región asociada.
     */
    Province getProvinceById(Long id);

    /**
     * Comprueba si existe una provincia con el código indicado.
     *
     * @param code Código que queremos comprobar.
     * @return true si existe; false en caso contrario.
     */
    boolean existsProvinceByCode(String code);

    /**
     * Comprueba si otra provincia tiene el mismo código,
     * excluyendo la provincia que estamos editando.
     *
     * @param code Código que queremos comprobar.
     * @param id Identificador que debemos excluir.
     * @return true si otra provincia utiliza el código.
     */
    boolean existsProvinceByCodeAndNotId(String code, Long id);

    /**
     * Elimina una provincia por su identificador.
     *
     * @param id Identificador de la provincia que queremos eliminar.
     */
    void deleteProvince(Long id);
}
