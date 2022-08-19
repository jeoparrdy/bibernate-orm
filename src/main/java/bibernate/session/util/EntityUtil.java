package bibernate.session.util;

import bibernate.annotation.Column;

import java.lang.reflect.Field;
import java.util.Optional;

public class EntityUtil {

   public static String resolveColumnName(Field field){
       return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElse(field.getName());
    }
}
