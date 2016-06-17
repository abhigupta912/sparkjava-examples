package pg.dbcp.model;

import java.util.UUID;

/**
 * @author Abhishek Gupta
 *         https://github.com/abhigupta912
 */
public class Todo {
    private final String id;
    private String title;
    private String description;
    private boolean isDone;

    public Todo() {
        id = UUID.randomUUID().toString();
    }

    public Todo(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;

        if (!(obj instanceof Todo)) return false;
        final Todo other = (Todo) obj;
        return this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
