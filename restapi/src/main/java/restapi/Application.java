package restapi;

import restapi.controller.TodoController;
import restapi.repository.TodoRepository;
import restapi.service.TodoService;

import static spark.Spark.port;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class Application {
    public static void main(String[] args) {
        port(9000);

        final TodoController todoController = new TodoController(new TodoService(new TodoRepository()));
        todoController.initializeRotues();
    }
}
