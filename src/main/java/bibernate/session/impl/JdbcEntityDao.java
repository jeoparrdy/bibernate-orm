package bibernate.session.impl;

import bibernate.session.EntityKey;
import bibernate.session.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bibernate.session.util.EntityUtil.resolveColumnName;
import static bibernate.session.util.EntityUtil.resolveTableName;

/**
 * Reflection based JDBC client for entities
 */
@RequiredArgsConstructor
public class JdbcEntityDao {
    private final static String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    private final DataSource dataSource;
    private Map<EntityKey<?>,Object> entityCache = new HashMap<>();

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        var cachedEntity = entityCache.get(EntityKey.of(entityType, id));
        if (cachedEntity != null){
            return entityType.cast(cachedEntity);
        }
        var idField = EntityUtil.getIdField(entityType);
        return findOneBy(entityType, idField, id);

    }
    @SneakyThrows
    public <T> List<T> findAllBy(Class<T> entityType, Field field, Object columnValue){
        var list = new ArrayList<T>();
        try (Connection connection = dataSource.getConnection()){
            var tableName = resolveTableName(entityType);
            var columnnName = resolveColumnName(field);
            var selectSql = String.format(SELECT_FROM_TABLE_BY_COLUMN,tableName, columnnName);
            try (var selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setObject(1, columnnName);
                System.out.println("SQL: " + selectStatement);
                ResultSet resultSet = selectStatement.executeQuery();
                while(resultSet.next()){
                    var entity = createEntityFromResultSet(entityType,resultSet);
                    list.add(entity);
                }
            }
        }
        return list;

    }

    @SneakyThrows
    public <T> T findOneBy(Class<T> entityType, Field field, Object columnValue){
        List<T> result = findAllBy(entityType, field, columnValue);
        if (result.size() != 1){
            throw  new IllegalStateException("RESULT MUST HAVE ONLY ONE ROW!");
        }
        return result.get(0);
    }
    @SneakyThrows
    private  <T> T createEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        String columnName;
        // 1. create entity instance
        var entityConstructor = entityType.getConstructor(); //getting the constructor
        var entity = entityConstructor.newInstance(); //creating the instance of entity
        // 2. for each field -> find a corresponding column value in the result set
        for (var field : entityType.getDeclaredFields()) {
            columnName = resolveColumnName(field);
            // 3. set field value
            var columnValue = resultSet.getObject(columnName);
            field.setAccessible(true);
            field.set(entity,columnValue);
        }
        // 4. return entity
        return cache(entity);
    }

    private  <T> T cache(T entity) {
        var entityKey  = EntityKey.valueOf(entity);
        var cachedEntity = entityCache.get(entityKey);
        if (cachedEntity != null){
            return (T) cachedEntity;
        } else {
            entityCache.put(entityKey,entity);
            return  entity;
        }
    }
}
