package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Todo;
import org.eclipse.jetty.http.HttpStatus;
import service.TodoService;

import java.lang.reflect.Type;
import java.util.List;

import static spark.Spark.*;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoController {
    private final TodoService todoService;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    public void initializeRotues() {
        get("/", (request, response) -> {
            final List<Todo> todos = todoService.getAllTodos();
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            response.type("application/json");
            return gson.toJson(todos, type);
        });

        get("/pending", (request, response) -> {
            final List<Todo> todos = todoService.getPendingTodos();
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            response.type("application/json");
            return gson.toJson(todos, type);
        });

        get("/completed", (request, response) -> {
            final List<Todo> todos = todoService.getCompletedTodos();
            final Type type = new TypeToken<List<Todo>>() {}.getType();
            response.type("application/json");
            return gson.toJson(todos, type);
        });

        get("/id/:id", (request, response) -> {
            final Todo todo = todoService.getTodoById(request.params("id"));
            if (todo == null) {
                halt(HttpStatus.NOT_FOUND_404);
            }
            response.type("application/json");
            return gson.toJson(todo, Todo.class);
        });

        post("/", (request, response) -> {
            final String title = request.queryParams("title");
            final String description = request.queryParams("description");
            final boolean isDone = Boolean.parseBoolean(request.queryParams("isDone"));
            final Todo todo = new Todo();
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setDone(isDone);
            final boolean added = todoService.addTodo(todo);
            if (added) {
                halt(HttpStatus.CREATED_201);
            } else {
                halt(HttpStatus.BAD_REQUEST_400);
            }
            return null;
        });

        put("/id/:id", (request, response) -> {
            final String id = request.params("id");
            final String title = request.queryParams("title");
            final String description = request.queryParams("description");
            final String isDoneString = request.queryParams("isDone");
            final boolean updated = todoService.updateTodo(id, title, description, isDoneString);
            if (updated) {
                halt(HttpStatus.ACCEPTED_202);
            } else {
                halt(HttpStatus.NOT_FOUND_404);
            }
            return null;
        });

        delete("/", (request, response) -> {
            todoService.deleteAllTodos();
            halt(HttpStatus.ACCEPTED_202);
            return null;
        });

        delete("/completed", (request, response) -> {
            todoService.deleteCompletedTodos();
            halt(HttpStatus.ACCEPTED_202);
            return null;
        });

        delete("/id/:id", (request, response) -> {
            final boolean deleted = todoService.deleteTodoById(request.params("id"));
            if (deleted) {
                halt(HttpStatus.ACCEPTED_202);
            } else {
                halt(HttpStatus.NOT_FOUND_404);
            }
            return null;
        });
    }
}

