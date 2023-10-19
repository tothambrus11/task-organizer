package commons.models;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import javax.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.kiprobinson.bigfraction.BigFraction;
import commons.utils.BigFractionDeserializer;
import commons.utils.BigFractionSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name="taskList")
public class TaskList {

    @Id
    private UUID id;
    private String title;

    @JsonSerialize(using = BigFractionSerializer.class)
    @JsonDeserialize(using = BigFractionDeserializer.class)
    private BigFraction position;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @JsonManagedReference
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;


    public TaskList(UUID id, String title) {
        this.id = id;
        this.title = title;
        this.tasks = new ArrayList<>();
    }

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public UUID getId() { return this.id; }

    public String getTitle() { return this.title; }

    public void setTitle(String name) { this.title = name; }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Task> getTasks() { return this.tasks; }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigFraction getPosition() {
        return position;
    }

    public void setPosition(BigFraction position) {
        this.position = position;
    }

    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    @JsonIgnore
    public Task getTaskById(UUID id) {
        if (id == null) return null;
        return tasks.stream().filter(task -> id.equals(task.getId())).findFirst().orElse(null);
    }

    public void update(TaskList update) {
        setTitle(update.getTitle());
        setPosition(update.getPosition());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskList taskList = (TaskList) o;

        if (!Objects.equals(id, taskList.id)) return false;
        if (!Objects.equals(title, taskList.title)) return false;
        return Objects.equals(position, taskList.position);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
