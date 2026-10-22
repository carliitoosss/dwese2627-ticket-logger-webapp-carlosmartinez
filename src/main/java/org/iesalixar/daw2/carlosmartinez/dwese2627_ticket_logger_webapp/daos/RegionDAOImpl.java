package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos;

import org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.entities.Region;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

@Repository
public class RegionDAOImpl implements RegionDAO {

    // Logger para registrar eventos de la aplicación.
    private static final Logger logger =
            LoggerFactory.getLogger(RegionDAOImpl.class);

    // Objeto para ejecutar consultas SQL.
    private final JdbcTemplate jdbcTemplate;

    // Spring inyectará automáticamente JdbcTemplate.
    public RegionDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Obtiene todas las regiones de la base de datos.
     *
     * @return Lista de regiones.
     */
    @Override
    public List<Region> listAllRegions() {

        logger.info("Listing all regions from the database.");

        String sql = "SELECT * FROM regions";

        List<Region> regions = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Region.class)
        );

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

        logger.info("Inserting region with code {}", region.getCode());

        String sql = "INSERT INTO regions (code, name) VALUES (?, ?)";

        int rowsAffected = jdbcTemplate.update(
                sql,
                region.getCode(),
                region.getName()
        );

        logger.info("Inserted region. Rows affected: {}", rowsAffected);
    }

    /**
     * Actualiza una región existente.
     *
     * @param region Región con los nuevos datos.
     */
    @Override
    public void updateRegion(Region region) {

        logger.info("Updating region with ID {}", region.getId());

        String sql = "UPDATE regions SET code = ?, name = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(
                sql,
                region.getCode(),
                region.getName(),
                region.getId()
        );

        logger.info("Updated region. Rows affected: {}", rowsAffected);
    }

    /**
     * Elimina una región de la base de datos.
     *
     * @param id Identificador de la región que queremos eliminar.
     */
    @Override
    public void deleteRegion(Long id) {

        logger.info("Deleting region with ID {}", id);

        String sql = "DELETE FROM regions WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql, id);

        logger.info("Deleted region. Rows affected: {}", rowsAffected);
    }

    /**
     * Recupera una región mediante su identificador.
     *
     * @param id Identificador de la región.
     * @return Región encontrada o null si no existe.
     */
    @Override
    public Region getRegionById(Long id) {

        logger.info("Getting region with ID {}", id);

        String sql = "SELECT * FROM regions WHERE id = ?";

        List<Region> regions = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Region.class),
                id
        );

        if (!regions.isEmpty()) {
            Region region = regions.get(0);
            logger.info("Region retrieved with ID {}", id);
            return region;
        }

        logger.warn("No region found with ID {}", id);
        return null;
    }

    /**
     * Comprueba si existe una región con un código determinado.
     *
     * @param code Código que queremos comprobar.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsRegionByCode(String code) {

        logger.info("Checking if region with code {} exists", code);

        String sql = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                code.toUpperCase()
        );

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

        logger.info("Checking if code {} exists excluding ID {}", code, id);

        String sql = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ? AND id != ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                code.toUpperCase(),
                id
        );

        boolean exists = count != null && count > 0;

        logger.info("Code {} exists in another region: {}", code, exists);

        return exists;
    }
}