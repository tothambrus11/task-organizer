package commons.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import commons.utils.SmartColor;
import commons.utils.SmartColorAttributeConverter;
import commons.utils.SmartColorDeserializer;
import commons.utils.SmartColorSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "tag")
public class Tag {
    @Id
    private UUID id;
    private String name;
    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor color;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "board_id", insertable = false, updatable = false)
    private UUID boardId;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private List<Task> tasks = new ArrayList<>();

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_tag", joinColumns = @JoinColumn(name = "tag_id"))
    @Column(name = "task_id")
    private List<UUID> tasks = new ArrayList<>();

    public Tag(UUID id, String name, SmartColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Tag() {
    }

    // Getters and setters for tasks
    public List<UUID> getTasks() {
        return tasks;
    }

    public void setTasks(List<UUID> tasks) {
        this.tasks = tasks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SmartColor getColor() {
        return color;
    }

    public void setColor(SmartColor color) {
        this.color = color;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(UUID boardId) {
        this.boardId = boardId;
    }

    public void update(Tag update) {
        setName(update.getName());
        setColor(update.getColor());
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
