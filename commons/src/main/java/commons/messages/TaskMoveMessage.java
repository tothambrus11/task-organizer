package commons.messages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.kiprobinson.bigfraction.BigFraction;
import commons.utils.BigFractionDeserializer;
import commons.utils.BigFractionSerializer;

import java.util.UUID;

public class TaskMoveMessage extends Message {
    private UUID taskId;
    private UUID sourceListId;
    private UUID targetListId;
    @JsonSerialize(using = BigFractionSerializer.class)
    @JsonDeserialize(using = BigFractionDeserializer.class)
    private BigFraction targetPosition;

    @SuppressWarnings("unused")
    public TaskMoveMessage() {
    }

    public TaskMoveMessage(UUID taskId, UUID sourceListId, UUID targetListId, BigFraction targetPosition) {
        this.taskId = taskId;
        this.sourceListId = sourceListId;
        this.targetListId = targetListId;
        this.targetPosition = targetPosition;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getTargetListId() {
        return targetListId;
    }

    public void setTargetListId(UUID targetListId) {
        this.targetListId = targetListId;
    }

    public BigFraction getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(BigFraction targetPosition) {
        this.targetPosition = targetPosition;
    }

    public UUID getSourceListId() {
        return sourceListId;
    }

    public void setSourceListId(UUID sourceListId) {
        this.sourceListId = sourceListId;
    }
}

