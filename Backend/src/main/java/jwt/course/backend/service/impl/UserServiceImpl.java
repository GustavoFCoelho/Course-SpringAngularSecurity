package jwt.course.backend.service.impl;

import jwt.course.backend.domain.User;
import jwt.course.backend.domain.UserPrincipal;
import jwt.course.backend.exception.domain.EmailExistException;
import jwt.course.backend.exception.domain.UserNotFoundException;
import jwt.course.backend.exception.domain.UsernameExistException;
import jwt.course.backend.repository.UserRepository;
import jwt.course.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static jwt.course.backend.enumeration.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findUserByUsername(username);
        if (user == null) {
            log.error("User not found by username " + username);
            throw new UsernameNotFoundException("User not found by username " + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            repository.save(user);
            log.info("Returning found user by username: " + username);
            return new UserPrincipal(user);
        }
    }

    @Override
    public User register(String firstNme, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstNme);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        user = repository.save(user);
        log.info("New user password: " + password);
        return user;
    }

    private String getTemporaryProfileImageUrl() {
        return "https://randomuser.me/api/portraits/men/99.jpg";
    }

    private String encodePassword(String password) {
        return encoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByUsername = findUserByUsername(newUsername);
        User userByEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException("No user found by username " + currentUsername);
            }

            if (userByUsername != null && currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }

            if (userByEmail != null && currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }
}
