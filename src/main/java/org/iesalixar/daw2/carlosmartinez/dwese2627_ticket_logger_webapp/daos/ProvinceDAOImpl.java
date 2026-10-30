
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementación del DAO de provincias utilizando JdbcTemplate.
 */
@Repository
@Transactional
public class ProvinceDAOImpl implements ProvinceDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;



    /**
     * Recupera todas las provincias con sus comunidades autónomas.
     *
     * @return Lista de provincias.
     */
    @Override
    public List<Province> listAllProvinces() {

        logger.info("Listing all provinces with their regions from the datebase.");
        String hql="SELECT p FROM Province p JOIN FETCH p.region";
        List<Province> provinces = entityManager.createQuery(hql, Province.class).getResultList();
        logger.info("Retrieved {} provinces from the database.",provinces.size());
        return provinces;
    }

    /**
     * Inserta una nueva provincia.
     *
     * @param province Provincia que queremos insertar.
     */
    @Override
    public void insertProvince(Province province) {

        logger.info("Inserting province with code: {}, name: {}, regionId: {}",
                province.getCode(),
                province.getName(),
                province.getRegion()!=null ? province.getRegion().getId() : null
        );
        entityManager.persist(province);
        logger.info("Inserted province with id: {}", province.getId());
    }

    /**
     * Actualiza los datos de una provincia existente.
     *
     * @param province Provincia con los datos modificados.
     */
    @Override
    public void updateProvince(Province province) {

        logger.info("Updating province with id: {}", province.getId());
        entityManager.merge(province);
        logger.info("Updated province with id: {}", province.getId());
    }

    /**
     * Recupera una provincia por su identificador.
     *
     * @param id Identificador de la provincia.
     * @return Provincia con su comunidad autónoma asociada.
     */
    @Override
    public Province getProvinceById(Long id) {

        logger.info("Buscando provincia con ID {}", id);
        Province province=entityManager.find(Province.class, id);
        if (province!=null){
            logger.info("Province retrieved: {} - {}", province.getCode(),province.getName());
        }else{
            logger.warn("No provinces found with id: {}", id);
        }
        return province;
    }

    /**
     * Comprueba si existe una provincia con el código indicado.
     *
     * @param code Código que queremos comprobar.
     * @return true si existe.
     */
    @Override
    public boolean existsProvinceByCode(String code) {
        logger.info("Cheking if province with code: {} exists", code);
        String hql="SELECT COUNT(p) FROM Province p WHERE UPPER(p.code)=:code";
        Long count= entityManager.createQuery(hql, Long.class)
                .setParameter("code",code.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count>0;
        logger.info("Province with code: {} exists: {}", code, exists);
        return exists;
    }

    /**
     * Comprueba si un código pertenece a otra provincia,
     * excluyendo la provincia que estamos editando.
     *
     * @param code Código que queremos comprobar.
     * @param id Identificador que queremos excluir.
     * @return true si otra provincia utiliza el código.
     */
    @Override
    public boolean existsProvinceByCodeAndNotId(String code, Long id) {

        logger.info("Checking if province with code: {} exists excluding id: {}", code, id);
        String query = "SELECT COUNT(p) FROM Province p WHERE UPPER(p.code) = :code AND p.id != :id";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("code", code.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists =count != null && count > 0;
        logger.info("Province with code: {} exists excluding id: {} : {}", code, exists, id);
        return exists;
    }

    /**
     * Elimina una provincia mediante su identificador.
     *
     * @param id Identificador de la provincia que queremos eliminar.
     */
    @Override
    public void deleteProvince(Long id) {

        logger.info("Deleting province with id: {}", id);
        Province province = entityManager.find(Province.class, id);
        if(province!=null){
            entityManager.remove(province);
            logger.info("Deleted province with id: {}", id);
        }else{
            logger.warn("Province with id: {} not found", id);
        }
    }
}
