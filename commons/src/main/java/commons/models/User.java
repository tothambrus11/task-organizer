package commons.models;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class User {

    //TODO
    //Do we change it to UUID?
    private Long id;

    private String name;

    @SuppressWarnings("unused")
    private User() { /* for object mapper */ }

    public User(Long id, String username) {
        this.id = id;
        this.name = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
