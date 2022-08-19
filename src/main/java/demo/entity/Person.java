package demo.entity;

import bibernate.annotation.Column;
import lombok.Data;

@Data
public class Person {

    private Long id;
    @Column("first_name")
    private String firstName;
    @Column("lastName")
    private String lastName;
}
