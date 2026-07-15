package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class PostgresContractTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");
    @Test void realDatabaseEnforcesUniqueness() throws Exception {
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement()) {
            statement.execute("create table learner (id integer primary key, name text not null)");
            statement.execute("insert into learner values (1, 'Ada')");
            try (var rows = statement.executeQuery("select count(*) from learner")) {
                rows.next();
                assertEquals(1, rows.getInt(1));
            }
        }
    }
}
