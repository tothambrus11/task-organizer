package client.utils;

public class LoginEvent {
    boolean isAdmin;

    public LoginEvent(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
}
