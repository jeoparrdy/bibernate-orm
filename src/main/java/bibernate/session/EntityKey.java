package bibernate.session;

import bibernate.session.util.EntityUtil;

public record EntityKey<T>(Class<T> entityType, Object id) {
    public static <T> EntityKey<?> of(Class<T> entityType, Object id) {
        return new EntityKey<>(entityType, id);
    }

    public static <T> EntityKey valueOf(T entity) {
        var id = EntityUtil.getId(entity);
        var entityType = entity.getClass();
        return new EntityKey<>(entityType, id);
    }
}
