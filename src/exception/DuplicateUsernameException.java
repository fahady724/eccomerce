package exception;

public class DuplicateUsernameException extends Exception {
    private String username;

    public DuplicateUsernameException(String username) {
        super("Username '" + username + "' already exists!");
        this.username = username;
    }

    public String getUsername() { 
        return username; 
    }
}