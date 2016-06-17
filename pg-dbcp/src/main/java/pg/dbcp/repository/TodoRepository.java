package pg.dbcp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.dbcp.model.Todo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class TodoRepository {
    private final static Logger logger = LoggerFactory.getLogger(TodoRepository.class);
    private final DataSource dataSource;

    public TodoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Todo> getAll(final String contextId) {
        final String sql = "select * from todos";
        final List<Todo> todos = new ArrayList<>();
        logger.info("[{}] | Retrieving Todos from DB", contextId);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        final String id = resultSet.getString("id");
                        final String title = resultSet.getString("title");
                        final String description = resultSet.getString("description");
                        final boolean isDone = resultSet.getBoolean("isDone");

                        final Todo todo = new Todo(id);
                        todo.setTitle(title);
                        todo.setDescription(description);
                        todo.setDone(isDone);

                        todos.add(todo);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
        }

        logger.info("[{}] | Found {} Todos in DB", contextId, todos.size());
        return todos;
    }

    public Optional<Todo> getById(final String contextId, final String id) {
        if (id == null) {
            logger.info("[{}] | Nothing to retrieve", contextId);
            return Optional.empty();
        }

        final String sql = "select * from todos where id = ?";
        final List<Todo> todos = new ArrayList<>();
        logger.info("[{}] | Retrieving Todo with Id: {} from DB", contextId, id);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        final String todoId = resultSet.getString("id");
                        final String title = resultSet.getString("title");
                        final String description = resultSet.getString("description");
                        final boolean isDone = resultSet.getBoolean("isDone");

                        final Todo todo = new Todo(todoId);
                        todo.setTitle(title);
                        todo.setDescription(description);
                        todo.setDone(isDone);

                        todos.add(todo);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
        }

        if (!todos.isEmpty()) {
            logger.info("[{}] | Found Todo with Id: {} in DB", contextId, id);
            return Optional.of(todos.get(0));
        }

        logger.info("[{}] | No Todo with Id: {} found in DB", contextId, id);
        return Optional.empty();
    }

    public boolean insertTodo(final String contextId, final Todo todo) {
        if (todo == null) {
            logger.info("[{}] | Nothing to insert", contextId);
            return false;
        }

        final String id = todo.getId();
        final Optional<Todo> todoById = getById(contextId, id);
        if (todoById.isPresent()) {
            logger.info("[{}] | Todo with Id: {} already present in DB", contextId, id);
            return false;
        }

        final String sql = "insert into todos (id, title, description, isDone) values (?,?,?,?)";
        logger.info("[{}] | Inserting Todo with Id: {} into DB", contextId, id);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.setString(2, todo.getTitle());
                statement.setString(3, todo.getDescription());
                statement.setBoolean(4, todo.isDone());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
            return false;
        }

        return true;
    }

    public boolean updateTodo(final String contextId, final String id, final String title, final String description, final String isDoneString) {
        if ((id == null) || (title == null && description == null && isDoneString == null)) {
            logger.info("[{}] | Nothing to update", contextId);
            return false;
        }

        final Optional<Todo> todoById = getById(contextId, id);
        if (!todoById.isPresent()) {
            logger.info("[{}] | Todo with Id: {} not present in DB", contextId, id);
            return false;
        }

        boolean commaRequired = false;

        final StringBuilder sqlBuilder = new StringBuilder("update todos set");

        if (title != null) {
            sqlBuilder.append(" title = ?");
            commaRequired = true;
        }

        if (description != null) {
            if (commaRequired) sqlBuilder.append(",");
            sqlBuilder.append(" description = ?");
            commaRequired = true;
        }

        if (isDoneString != null) {
            if (commaRequired) sqlBuilder.append(",");
            sqlBuilder.append(" isDone = ?");
            commaRequired = true;
        }

        sqlBuilder.append(" where id = ?");
        logger.info("[{}] | Updating Todo with Id: {} in DB", contextId, id);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())) {
                int index = 1;
                if (title != null) statement.setString(index++, title);
                if (description != null) statement.setString(index++, description);
                if (isDoneString != null) statement.setBoolean(index++, Boolean.valueOf(isDoneString));
                statement.setString(index, id);
                logger.debug(statement.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
        }

        return true;
    }

    public void deleteAllTodos(final String contextId) {
        final String sql = "delete from todos";
        logger.info("[{}] | Deleting All Todos from DB", contextId);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
        }
    }

    public boolean deleteTodoById(final String contextId, final String id) {
        final String sql = "delete from todos where id = ?";
        logger.info("[{}] | Deleting Todo with Id: {} from DB", contextId, id);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("[{}] | SQL Exception occurred", contextId, e);
        }

        return true;
    }
}
