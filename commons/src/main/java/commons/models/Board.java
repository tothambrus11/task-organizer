package commons.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import commons.utils.SmartColor;
import commons.utils.SmartColorAttributeConverter;
import commons.utils.SmartColorDeserializer;
import commons.utils.SmartColorSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Board {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private String title;
    private String creator;
    private String key;
    private Boolean shared;
    private UUID defaultHighlightId;


    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor boardForegroundColor;
    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor boardBackgroundColor;
    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor listForegroundColor;
    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor listBackgroundColor;


    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskHighlight> highlights = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskList> taskLists = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public Board() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SmartColor getBoardForegroundColor() {
        return boardForegroundColor;
    }

    public void setBoardForegroundColor(SmartColor foregroundColor) {
        this.boardForegroundColor = foregroundColor;
    }

    public SmartColor getBoardBackgroundColor() {
        return boardBackgroundColor;
    }

    public void setBoardBackgroundColor(SmartColor backgroundColor) {
        this.boardBackgroundColor = backgroundColor;
    }

    public SmartColor getListForegroundColor() {
        return listForegroundColor;
    }

    public void setListForegroundColor(SmartColor listForegroundColor) {
        this.listForegroundColor = listForegroundColor;
    }

    public SmartColor getListBackgroundColor() {
        return listBackgroundColor;
    }

    public void setListBackgroundColor(SmartColor listBackgroundColor) {
        this.listBackgroundColor = listBackgroundColor;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getShared() {
        return this.shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public List<TaskList> getTaskLists() {
        return this.taskLists;
    }

    public void setTaskLists(List<TaskList> taskLists) {
        this.taskLists = taskLists;
    }

    public BoardInfo getInfo() {
        var info = new BoardInfo();
        info.setId(id);
        info.setJoinKey(key);
        info.setTitle(title);
        info.setCreator(creator);
        return info;
    }

    public TaskList getTaskListById(UUID id) {
        if (id == null) return null;
        return taskLists.stream().filter(list -> id.equals(list.getId())).findFirst().get();
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Optional<Tag> getTagById(UUID id) {
        if (id == null) return null;
        return tags.stream().filter(list -> id.equals(list.getId())).findFirst();
    }

    public void update(Board update) {
        setTitle(update.getTitle());
        setCreator(update.getCreator());
        setBoardBackgroundColor(update.getBoardBackgroundColor());
        setBoardForegroundColor(update.getBoardForegroundColor());
        setListForegroundColor(update.getListForegroundColor());
        setListBackgroundColor(update.getListBackgroundColor());
        setDefaultHighlightId(update.getDefaultHighlightId());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    @JsonIgnore
    public Optional<Task> getTaskById(UUID taskId) {
        for (var list : taskLists) {
            for (var task : list.getTasks()) {
                if (task.getId().equals(taskId)) {
                    return Optional.of(task);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<SubTask> getSubtaskById(UUID id) {
        for (var list : taskLists) {
            for (var task : list.getTasks()) {
                for (var subtask : task.getSubTasks()) {
                    if (subtask.getId().equals(id)) {
                        return Optional.of(subtask);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public UUID getDefaultHighlightId() {
        return defaultHighlightId;
    }

    public void setDefaultHighlightId(UUID defaultHighlightId) {
        this.defaultHighlightId = defaultHighlightId;
    }

    public List<TaskHighlight> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<TaskHighlight> highlights) {
        this.highlights = highlights;
    }

    @JsonIgnore
    public Optional<TaskHighlight> getHighlightById(UUID highlightId) {
        return highlights.stream().filter(h -> h.getId().equals(highlightId)).findFirst();
    }
}
