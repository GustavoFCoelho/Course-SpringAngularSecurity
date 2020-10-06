package jwt.course.backend.exception.domain;

public class UsernameExistException extends Exception {

    public UsernameExistException(String message) {
        super(message);
    }
}
