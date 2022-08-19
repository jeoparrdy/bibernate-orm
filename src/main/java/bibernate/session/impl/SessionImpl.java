package bibernate.session.impl;

import bibernate.annotation.Column;
import bibernate.session.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Optional;

@RequiredArgsConstructor
public class SessionImpl implements Session {

    private final JdbcEntityDao entityDao;

    public SessionImpl(DataSource dataSource) {
        entityDao = new JdbcEntityDao(dataSource);
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        return entityDao.findById(entityType,id);
    }
}
