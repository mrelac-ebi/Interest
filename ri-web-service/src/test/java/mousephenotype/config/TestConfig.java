package mousephenotype.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/ri.test.properties")
@ComponentScan("org.mousephenotype.interest")
public class TestConfig {    // ri
    @Bean
    public DataSource riDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("ri")
                .addScripts("sql/h2/schema.sql", "sql/h2/interestController-data.sql")
                .build();
    }
}