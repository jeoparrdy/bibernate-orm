package demo;

import bibernate.session.SessionFactory;
import bibernate.session.impl.SessionFactoryImpl;
import demo.entity.Person;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class DemoApp {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new SessionFactoryImpl(initDataSource());
        var session = sessionFactory.openSession();

        Person person = session.find(Person.class, 7l);
        System.out.println(person);

    }
    private static DataSource initDataSource(){
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }
}
