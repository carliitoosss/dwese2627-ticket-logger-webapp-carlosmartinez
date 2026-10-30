package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class RegionDAOImpl implements RegionDAO {

    // Logger para registrar eventos de la aplicación.
    private static final Logger logger =LoggerFactory.getLogger(RegionDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Obtiene todas las regiones de la base de datos.
     *
     * @return Lista de regiones.
     */
    @Override
    public List<Region> listAllRegions() {

        logger.info("Listing all regions from the database.");
        String hql = "SELECT r FROM Region r";
        List<Region> regions = entityManager.createQuery(hql, Region.class).getResultList();
        logger.info("Retrieved {} regions from the database.", regions.size());
        return regions;
    }

    /**
     * Inserta una nueva región en la base de datos.
     *
     * @param region Región que queremos insertar.
     */
    @Override
    public void insertRegion(Region region) {

        logger.info("Inserting region with code {} and name{}", region.getCode(), region.getName());
        entityManager.persist(region);
        logger.info("Inserted region sucessful");
    }

    /**
     * Actualiza una región existente.
     *
     * @param region Región con los nuevos datos.
     */
    @Override
    public void updateRegion(Region region) {

        logger.info("Updating region with ID {}", region.getId());
        entityManager.merge(region);
        logger.info("Updated region with ID: {}", region.getId());
    }

    /**
     * Elimina una región de la base de datos.
     *
     * @param id Identificador de la región que queremos eliminar.
     */
    @Override
    public void deleteRegion(Long id) {

        logger.info("Deleting region with ID {}", id);
        Region region=entityManager.find(Region.class,id);
        if(region!=null){
            entityManager.remove(region);
            logger.info("Deleted region with id: {}",id);
        }else{
            logger.info("Region with id:{} not found", id);
        }

    }

    /**
     * Recupera una región mediante su identificador.
     *
     * @param id Identificador de la región.
     * @return Región encontrada o null si no existe.
     */
    @Override
    public Region getRegionById(Long id) {

        logger.info("Retrieving region by ID: {}", id);
        Region region=entityManager.find(Region.class,id);
        if(region!=null){
            logger.info("Region retrieved:{} - {}", region.getCode(),region.getName());
        }else{
            logger.warn("No region found with ID {}", id);
        }
        return region;
    }

    /**
     * Comprueba si existe una región con un código determinado.
     *
     * @param code Código que queremos comprobar.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsRegionByCode(String code) {

        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code";
        Long count=entityManager.createQuery(hql, Long.class)
                .setParameter("code",code.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with code {} exists: {}", code, exists);
        return exists;
    }

    /**
     * Comprueba si otra región utiliza el código indicado.
     *
     * @param code Código que queremos comprobar.
     * @param id ID de la región que estamos editando.
     * @return true si el código pertenece a otra región.
     */
    @Override
    public boolean existsRegionByCodeAndNotId(String code, Long id) {

        logger.info("Checking if region with code {} exists excluding ID {}", code, id);

        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code AND r.id != :id";
        Long count=entityManager.createQuery(hql, Long.class)
                .setParameter("code",code.toUpperCase())
                .setParameter("id",id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with code: {} exists excluding id {}: {}", code, id,exists);
        return exists;
    }
}