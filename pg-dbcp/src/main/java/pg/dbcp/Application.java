package pg.dbcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.dbcp.controller.TodoController;
import pg.dbcp.repository.TodoRepository;
import pg.dbcp.service.TodoService;
import pg.dbcp.store.TodoDataStore;

import javax.sql.DataSource;

import static spark.Spark.port;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class Application {
    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        port(9000);

        final DataSource dataSource = TodoDataStore.createDataSource("/db.properties");
        if (dataSource != null) {
            final TodoController todoController = new TodoController(new TodoService(new TodoRepository(dataSource)));
            todoController.initializeRotues();
        } else {
            logger.error("Unable to initialize data source");
        }
    }
}
