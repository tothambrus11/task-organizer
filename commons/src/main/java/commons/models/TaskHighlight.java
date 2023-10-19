package commons.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import commons.utils.SmartColor;
import commons.utils.SmartColorAttributeConverter;
import commons.utils.SmartColorDeserializer;
import commons.utils.SmartColorSerializer;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "taskHighlight")
public class TaskHighlight {
    @Id
    private UUID Id;
    private String name;

    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor foregroundColor;

    @JsonSerialize(using = SmartColorSerializer.class)
    @JsonDeserialize(using = SmartColorDeserializer.class)
    @Convert(converter = SmartColorAttributeConverter.class)
    private SmartColor backgroundColor;
    private Long position;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "board_id", insertable = false, updatable = false)
    private UUID boardId;


    public TaskHighlight(UUID highlightId, String name, SmartColor foregroundColor, SmartColor backgroundColor, Long position) {
        this.Id = highlightId;
        this.name = name;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.position = position;
    }

    public TaskHighlight() {
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        this.Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SmartColor getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(SmartColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public SmartColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(SmartColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void update(TaskHighlight highlight) {
        this.name = highlight.getName();
        this.foregroundColor = highlight.getForegroundColor();
        this.backgroundColor = highlight.getBackgroundColor();
        this.position = highlight.getPosition();
    }
}
