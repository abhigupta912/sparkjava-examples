package pg.dbcp.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoDataStore {
    private final static Logger logger = LoggerFactory.getLogger(TodoDataStore.class);

    public static DataSource createDataSource(final String fileName) {
        final Properties properties = new Properties();

        try (InputStream stream = TodoDataStore.class.getResourceAsStream(fileName)) {
            properties.load(stream);

            final BasicDataSource connectionPool = new BasicDataSource();
            connectionPool.setUsername(properties.getProperty("username"));
            connectionPool.setPassword(properties.getProperty("password"));
            connectionPool.setDriverClassName(properties.getProperty("driver"));
            connectionPool.setUrl(properties.getProperty("url"));
            connectionPool.setInitialSize(Integer.valueOf(properties.getProperty("initialConnections")));
            connectionPool.setMinIdle(Integer.valueOf(properties.getProperty("minIdleConnections")));
            return connectionPool;
        } catch (FileNotFoundException e) {
            logger.error("Datasource properties file: {} not found", fileName);
            logger.error("Error initializing data source", e);
        } catch (IOException e) {
            logger.error("Unable to read properties file: {}", fileName);
            logger.error("Error initializing data source", e);
        }

        return null;
    }
}
