package demo.entity;

import bibernate.annotation.Column;
import bibernate.annotation.Id;
import bibernate.annotation.Table;
import lombok.Data;

@Data
@Table("persons")
public class Person {
    @Id
    private Long id;
    @Column("first_name")
    private String firstName;
    @Column("lastName")
    private String lastName;
}
