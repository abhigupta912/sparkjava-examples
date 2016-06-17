package service;

import model.Todo;
import repository.TodoRepository;

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

    public List<Todo> getAllTodos() {
        return todoRepository.getAll();
    }

    public List<Todo> getPendingTodos() {
        return getAllTodos().stream().filter(todo -> !todo.isDone()).collect(Collectors.toList());
    }

    public List<Todo> getCompletedTodos() {
        return getAllTodos().stream().filter(todo -> todo.isDone()).collect(Collectors.toList());
    }

    public Todo getTodoById(final String id) {
        final Optional<Todo> todoById = todoRepository.getById(id);
        if (todoById.isPresent()) {
            return todoById.get();
        } else {
            return null;
        }
    }

    public boolean addTodo(final Todo todo) {
        return todoRepository.insertTodo(todo);
    }

    public boolean updateTodo(final String id, final String title, final String description, final String isDoneString) {
        return todoRepository.updateTodo(id, title, description, isDoneString);
    }

    public void deleteAllTodos() {
        todoRepository.deleteAllTodos();
    }

    public boolean deleteTodoById(final String id) {
        return todoRepository.deleteTodoById(id);
    }

    public void deleteCompletedTodos() {
        getCompletedTodos().stream().forEach(todo -> todoRepository.deleteTodoById(todo.getId()));
    }
}
