package bibernate.session;

/**
* Main API
*/
public interface Session {
    <T> T find(Class<T> entityType, Object id);
}
