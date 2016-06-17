package restapi.repository;

import restapi.model.Todo;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoRepository {
    private final Set<Todo> todoStore = new HashSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public List<Todo> getAll() {
        try {
            lock.readLock().lock();
            return todoStore.stream().collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Todo> getById(final String id) {
        try {
            lock.readLock().lock();
            return todoStore.stream().filter(todo -> todo.getId().equals(id)).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean insertTodo(final Todo todo) {
        final Optional<Todo> todoById = getById(todo.getId());
        if (todoById.isPresent()) {
            return false;
        }

        try {
            lock.writeLock().lock();
            todoStore.add(todo);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean updateTodo(final String id, final String title, final String description, final String isDoneString) {
        final Optional<Todo> todoById = getById(id);
        if (!todoById.isPresent()) {
            return false;
        }

        try {
            lock.writeLock().lock();
            final Todo oldTodo = todoById.get();
            final Todo newTodo = new Todo(id);

            final String titleToSet = (title == null) ? oldTodo.getTitle() : title;
            newTodo.setTitle(titleToSet);

            final String descriptionToSet = (description == null) ? oldTodo.getDescription() : description;
            newTodo.setDescription(descriptionToSet);

            final boolean isDoneToSet = (isDoneString == null) ? oldTodo.isDone() : Boolean.parseBoolean(isDoneString);
            newTodo.setDone(isDoneToSet);

            todoStore.remove(oldTodo);
            todoStore.add(newTodo);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteAllTodos() {
        try {
            lock.writeLock().lock();
            todoStore.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteTodoById(final String id) {
        final Optional<Todo> todoById = getById(id);
        if (!todoById.isPresent()) {
            return false;
        }

        try {
            lock.writeLock().lock();
            todoStore.remove(todoById.get());
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
