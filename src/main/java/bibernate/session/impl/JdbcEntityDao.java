package bibernate.session.impl;

import bibernate.session.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Reflection based JDBC client for entities
 */
@RequiredArgsConstructor
public class JdbcEntityDao {
    private final static String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    private final DataSource dataSource;

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {

        try (Connection connection = dataSource.getConnection()){
            try (var selectStatement = connection.createStatement()) {
                ResultSet resultSet = selectStatement.executeQuery("select * from %s where %s = ?" + id);
                resultSet.next();
                return createEntityFromResultSet(entityType, resultSet);
            }
        }
    }
    @SneakyThrows
    private static <T> T createEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        String columnName;
        // 1. create entity instance
        var entityConstructor = entityType.getConstructor(); //getting the constructor
        var entity = entityConstructor.newInstance(); //creating the instance of entity
        // 2. for each field -> find a corresponding column value in the result set
        for (var field : entityType.getDeclaredFields()) {
            columnName = EntityUtil.resolveColumnName(field);
            // 3. set field value
            var columnValue = resultSet.getObject(columnName);
            field.setAccessible(true);
            field.set(entity,columnValue);
        }
        // 4. return entity
        return entity;
    }
}
