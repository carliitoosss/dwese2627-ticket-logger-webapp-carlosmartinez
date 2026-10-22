
package org.iesalixar.daw2.carlosmartinez.dwese2627_ticket_logger_webapp.daos;

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
public class ProvinceDAOImpl implements ProvinceDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(ProvinceDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor para inyectar JdbcTemplate.
     *
     * @param jdbcTemplate Herramienta para ejecutar consultas SQL.
     */
    public ProvinceDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Convierte una fila SQL en un objeto Province
     * con su comunidad autónoma asociada.
     */
    private final RowMapper<Province> provinceRowMapper = (rs, rowNum) -> {

        Province province = new Province();

        province.setId(rs.getLong("id"));
        province.setCode(rs.getString("code"));
        province.setName(rs.getString("name"));

        Region region = new Region();
        region.setId(rs.getLong("region_id"));
        region.setCode(rs.getString("region_code"));
        region.setName(rs.getString("region_name"));

        province.setRegion(region);

        return province;
    };

    /**
     * Recupera todas las provincias con sus comunidades autónomas.
     *
     * @return Lista de provincias.
     */
    @Override
    public List<Province> listAllProvinces() {

        logger.info("Listando todas las provincias.");

        String sql =
                "SELECT p.id, p.code, p.name, " +
                        "r.id AS region_id, " +
                        "r.code AS region_code, " +
                        "r.name AS region_name " +
                        "FROM provinces p " +
                        "JOIN regions r ON p.region_id = r.id";

        List<Province> provinces =
                jdbcTemplate.query(sql, provinceRowMapper);

        logger.info("Se han recuperado {} provincias.",
                provinces.size());

        return provinces;
    }

    /**
     * Inserta una nueva provincia.
     *
     * @param province Provincia que queremos insertar.
     */
    @Override
    public void insertProvince(Province province) {

        logger.info("Insertando provincia con código {}",
                province.getCode());

        String sql =
                "INSERT INTO provinces (code, name, region_id) " +
                        "VALUES (?, ?, ?)";

        jdbcTemplate.update(
                sql,
                province.getCode(),
                province.getName(),
                province.getRegion().getId()
        );

        logger.info("Provincia insertada correctamente.");
    }

    /**
     * Actualiza los datos de una provincia existente.
     *
     * @param province Provincia con los datos modificados.
     */
    @Override
    public void updateProvince(Province province) {

        logger.info("Actualizando provincia con ID {}",
                province.getId());

        String sql =
                "UPDATE provinces " +
                        "SET code = ?, name = ?, region_id = ? " +
                        "WHERE id = ?";

        jdbcTemplate.update(
                sql,
                province.getCode(),
                province.getName(),
                province.getRegion().getId(),
                province.getId()
        );

        logger.info("Provincia con ID {} actualizada.",
                province.getId());
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

        String sql =
                "SELECT p.id, p.code, p.name, " +
                        "r.id AS region_id, " +
                        "r.code AS region_code, " +
                        "r.name AS region_name " +
                        "FROM provinces p " +
                        "JOIN regions r ON p.region_id = r.id " +
                        "WHERE p.id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                provinceRowMapper,
                id
        );
    }

    /**
     * Comprueba si existe una provincia con el código indicado.
     *
     * @param code Código que queremos comprobar.
     * @return true si existe.
     */
    @Override
    public boolean existsProvinceByCode(String code) {

        String sql =
                "SELECT COUNT(*) FROM provinces WHERE code = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                code
        );

        return count != null && count > 0;
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

        String sql =
                "SELECT COUNT(*) FROM provinces " +
                        "WHERE code = ? AND id != ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                code,
                id
        );

        return count != null && count > 0;
    }

    /**
     * Elimina una provincia mediante su identificador.
     *
     * @param id Identificador de la provincia que queremos eliminar.
     */
    @Override
    public void deleteProvince(Long id) {

        logger.info("Eliminando provincia con ID {}", id);

        String sql =
                "DELETE FROM provinces WHERE id = ?";

        jdbcTemplate.update(sql, id);

        logger.info("Operación de eliminación ejecutada para el ID {}",
                id);
    }
}
