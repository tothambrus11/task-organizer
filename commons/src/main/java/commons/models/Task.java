package commons.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.kiprobinson.bigfraction.BigFraction;
import commons.utils.BigFractionDeserializer;
import commons.utils.BigFractionSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "task")
public class Task {

    @Id
    private UUID id;
    private String title;
    private String description;

    @JsonSerialize(using = BigFractionSerializer.class)
    @JsonDeserialize(using = BigFractionDeserializer.class)
    private BigFraction position;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "tasklist_id")
    private TaskList taskList;

    @Column(name = "tasklist_id", insertable = false, updatable = false)
    private UUID taskListId;


    @Column(name = "taskhighlight_id")
    private UUID taskHighlightId;

    @JsonManagedReference
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks = new ArrayList<>();

//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(name = "task_tag",
//            joinColumns = @JoinColumn(name = "task_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id"))
//    private List<Tag> tags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_tag", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag_id")
    private List<UUID> tags = new ArrayList<>();

    public Task(UUID id, String title, String description, BigFraction position) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.position = position;
    }

    public Task() {
    }

    // Getters and setters for tags
    public List<UUID> getTags() {
        return tags;
    }

    public void setTags(List<UUID> tags) {
        this.tags = tags;
    }

    public UUID getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(UUID taskListId) {
        this.taskListId = taskListId;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigFraction getPosition() {
        return this.position;
    }

    public void setPosition(BigFraction position) {
        this.position = position;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

//    public TaskHighlight getTaskHighlight() {
//        return taskHighlight;
//    }
//    public void setTaskHighlight(TaskHighlight taskHighlight) {
//        this.taskHighlight = taskHighlight;
//    }

    public UUID getTaskHighlightId() {
        return taskHighlightId;
    }

    public void setTaskHighlightId(UUID taskHighlightId) {
        this.taskHighlightId = taskHighlightId;
    }

    public List<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void update(Task update) {
        setTitle(update.getTitle());
        setDescription(update.getDescription());
        setPosition(update.getPosition());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
