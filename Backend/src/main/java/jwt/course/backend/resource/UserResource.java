package jwt.course.backend.resource;

import jwt.course.backend.constant.SecurityConstant;
import jwt.course.backend.domain.User;
import jwt.course.backend.domain.UserPrincipal;
import jwt.course.backend.exception.domain.EmailExistException;
import jwt.course.backend.exception.domain.ExceptionHandling;
import jwt.course.backend.exception.domain.UserNotFoundException;
import jwt.course.backend.exception.domain.UsernameExistException;
import jwt.course.backend.service.UserService;
import jwt.course.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource extends ExceptionHandling {
    private final UserService service;
    private final AuthenticationManager manager;
    private final JwtTokenProvider provider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = service.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity(loginUser, jwtHeader, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, provider.genereteJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User registeredUser =  service.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(registeredUser);
    }
}
