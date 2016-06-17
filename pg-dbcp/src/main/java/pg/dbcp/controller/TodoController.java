package pg.dbcp.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.dbcp.model.Todo;
import pg.dbcp.service.TodoService;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoController {
    private final static Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    public void initializeRotues() {
        before((request, response) -> request.session().attribute("reqId", UUID.randomUUID().toString()));

        after((request, response) -> response.type("application/json"));

        get("/", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Retrieving All Todos", reqId);
            final List<Todo> todos = todoService.getAllTodos(reqId);
            logger.info("[{}] | Retrieved {} Todos", reqId, todos.size());
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            return gson.toJson(todos, type);
        });

        get("/pending", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Retrieving Pending Todos", reqId);
            final List<Todo> todos = todoService.getPendingTodos(reqId);
            logger.info("[{}] | Retrieved {} Todos", reqId, todos.size());
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            return gson.toJson(todos, type);
        });

        get("/completed", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Retrieving Completed Todos", reqId);
            final List<Todo> todos = todoService.getCompletedTodos(reqId);
            logger.info("[{}] | Retrieved {} Todos", reqId, todos.size());
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            return gson.toJson(todos, type);
        });

        get("/id/:id", (request, response) -> {
            final String id = request.params("id");
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Retrieving Todo with Id: {}", reqId, id);
            final Todo todo = todoService.getTodoById(reqId, id);
            if (todo == null) {
                logger.error("[{}] | No Todo found with Id: {}", reqId, id);
                halt(HttpStatus.NOT_FOUND_404);
                return null;
            } else {
                logger.info("[{}] | Retrieved Todo with Id: {}", reqId, id);
                return gson.toJson(todo, Todo.class);
            }
        });

        post("/", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Creating new Todo", reqId);
            final String title = request.queryParams("title");
            final String description = request.queryParams("description");
            final boolean isDone = Boolean.parseBoolean(request.queryParams("isDone"));
            final Todo todo = new Todo();
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setDone(isDone);
            final boolean added = todoService.addTodo(reqId, todo);
            if (added) {
                logger.info("[{}] | New Todo successfully created", reqId);
                halt(HttpStatus.CREATED_201);
            } else {
                logger.error("[{}] | Unable to create Todo", reqId);
                halt(HttpStatus.BAD_REQUEST_400);
            }
            return null;
        });

        put("/id/:id", (request, response) -> {
            final String id = request.params("id");
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Updating Todo with Id: {}", reqId, id);
            final String title = request.queryParams("title");
            final String description = request.queryParams("description");
            final String isDoneString = request.queryParams("isDone");
            final boolean updated = todoService.updateTodo(reqId, id, title, description, isDoneString);
            if (updated) {
                logger.info("[{}] | Updated Todo with Id: {}", reqId, id);
                halt(HttpStatus.ACCEPTED_202);
            } else {
                logger.error("[{}] | Unable to update Todo with Id: {}", reqId, id);
                halt(HttpStatus.NOT_FOUND_404);
            }
            return null;
        });

        delete("/", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Deleting All Todos", reqId);
            todoService.deleteAllTodos(reqId);
            logger.info("[{}] | Deleted All Todos", reqId);
            halt(HttpStatus.ACCEPTED_202);
            return null;
        });

        delete("/completed", (request, response) -> {
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Deleting Completed Todos", reqId);
            todoService.deleteCompletedTodos(reqId);
            logger.info("[{}] | Deleted Completed Todos", reqId);
            halt(HttpStatus.ACCEPTED_202);
            return null;
        });

        delete("/id/:id", (request, response) -> {
            final String id = request.params("id");
            final String reqId = request.session().attribute("reqId");
            logger.info("[{}] | Deleting Todo with Id: {}", reqId, id);
            final boolean deleted = todoService.deleteTodoById(reqId, id);
            if (deleted) {
                logger.info("[{}] | Deleted Todo with Id: {}", reqId, id);
                halt(HttpStatus.ACCEPTED_202);
            } else {
                logger.error("[{}] | Unable to delete Todo with Id: {}", reqId, id);
                halt(HttpStatus.NOT_FOUND_404);
            }
            return null;
        });
    }
}

