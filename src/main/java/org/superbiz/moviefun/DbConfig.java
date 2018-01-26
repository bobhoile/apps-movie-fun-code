package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.boot.internal.Helper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter retVal = new HibernateJpaVendorAdapter();
        retVal.setDatabase(Database.MYSQL);
        retVal.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        retVal.setGenerateDdl(true);
        return retVal;
    }

    private DataSource buildDataSource(String url,String username, String password) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        return new HikariDataSource(hikariConfig);
    }

    private LocalContainerEntityManagerFactoryBean buildEntityManagerFactoryBean(DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter, String persistenceUnit) {
        LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
        retVal.setDataSource(dataSource);
        retVal.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        retVal.setPackagesToScan(DbConfig.class.getPackage().getName());
        retVal.setPersistenceUnitName(persistenceUnit);
        return retVal;
    }

    private PlatformTransactionManager createPlatformTransactionManager(LocalContainerEntityManagerFactoryBean factoryBean){
        return new JpaTransactionManager(factoryBean.getObject());
    }

    /*
    albums
     */
    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        return buildDataSource(url, username, password);
    }

    @Bean
    @Qualifier("albums")
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        return buildEntityManagerFactoryBean(albumsDataSource, hibernateJpaVendorAdapter, "albums");
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager(@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean){
        return createPlatformTransactionManager(factoryBean);
    }

    /*
    movies
     */
    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        return buildDataSource(url, username, password);
    }

    @Bean
    @Qualifier("movies")
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        return buildEntityManagerFactoryBean(moviesDataSource, hibernateJpaVendorAdapter, "movies");
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(@Qualifier("movies") LocalContainerEntityManagerFactoryBean factoryBean){
        return createPlatformTransactionManager(factoryBean);
    }

}
