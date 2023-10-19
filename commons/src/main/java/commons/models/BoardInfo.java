package commons.models;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Entry represents an individual entry in the Keychain.
 * It contains information such as join key, password, last join time, title, and creator.
 */
public class BoardInfo {
    private UUID id;

    private String joinKey;
    private String password;
    private LocalDateTime lastJoinTime;

    private String title;
    private String creator;

    public BoardInfo() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(LocalDateTime lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void update(BoardInfo update) {
        if (update.getTitle() != null) setTitle(update.getTitle());
        if (update.getCreator() != null) setCreator(update.getCreator());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
