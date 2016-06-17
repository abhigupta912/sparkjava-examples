package pg.dbcp.service;

import pg.dbcp.model.Todo;
import pg.dbcp.repository.TodoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos(final String contextId) {
        return todoRepository.getAll(contextId);
    }

    public List<Todo> getPendingTodos(final String contextId) {
        return getAllTodos(contextId).stream().filter(todo -> !todo.isDone()).collect(Collectors.toList());
    }

    public List<Todo> getCompletedTodos(final String contextId) {
        return getAllTodos(contextId).stream().filter(todo -> todo.isDone()).collect(Collectors.toList());
    }

    public Todo getTodoById(final String contextId, final String id) {
        final Optional<Todo> todoById = todoRepository.getById(contextId, id);
        if (todoById.isPresent()) {
            return todoById.get();
        } else {
            return null;
        }
    }

    public boolean addTodo(final String contextId, final Todo todo) {
        return todoRepository.insertTodo(contextId, todo);
    }

    public boolean updateTodo(final String contextId, final String id, final String title, final String description, final String isDoneString) {
        return todoRepository.updateTodo(contextId, id, title, description, isDoneString);
    }

    public void deleteAllTodos(final String contextId) {
        todoRepository.deleteAllTodos(contextId);
    }

    public boolean deleteTodoById(final String contextId, final String id) {
        return todoRepository.deleteTodoById(contextId, id);
    }

    public void deleteCompletedTodos(final String contextId) {
        getCompletedTodos(contextId).stream().forEach(todo -> todoRepository.deleteTodoById(contextId, todo.getId()));
    }
}
