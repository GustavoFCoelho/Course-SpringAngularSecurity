package jwt.course.backend.service;

import jwt.course.backend.domain.User;
import jwt.course.backend.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(String firstNme, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException, IOException, NotAnImageException;
    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageException;
    void deleteUser(String id) throws IOException;
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageException;
}
