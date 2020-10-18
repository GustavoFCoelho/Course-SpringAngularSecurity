package jwt.course.backend.service;

import jwt.course.backend.domain.User;
import jwt.course.backend.exception.domain.EmailExistException;
import jwt.course.backend.exception.domain.UserNotFoundException;
import jwt.course.backend.exception.domain.UsernameExistException;

import java.util.List;

public interface UserService {
    User register(String firstNme, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
