package ru.zhadaev.schoolsecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class InitDatabaseConfig {
    private final ApplicationContext context;

    @Value("${test.data.filename.schema}")
    private String schemaFile;

    @Value("${test.data.filename.data}")
    private String dataFile;

    @Bean
    @ConditionalOnProperty(prefix = "test.data", name = "enable", havingValue = "true")
    public void initData() {
        Resource initSchema = new ClassPathResource(schemaFile);
        Resource initData = new ClassPathResource(dataFile);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, initData);
        DataSource dataSource = context.getBean(DataSource.class);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }
}
