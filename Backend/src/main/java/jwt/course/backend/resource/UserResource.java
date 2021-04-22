package jwt.course.backend.resource;

import jwt.course.backend.constant.FileCostant;
import jwt.course.backend.constant.SecurityConstant;
import jwt.course.backend.domain.HttpResponse;
import jwt.course.backend.domain.User;
import jwt.course.backend.domain.UserPrincipal;
import jwt.course.backend.exception.domain.*;
import jwt.course.backend.service.UserService;
import jwt.course.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static jwt.course.backend.constant.FileCostant.*;

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

    @PostMapping("/add")
    public ResponseEntity<?> addNewUser(@RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String username,
                                        @RequestParam String email,
                                        @RequestParam String role,
                                        @RequestParam String isActive,
                                        @RequestParam String isNonLocked,
                                        @RequestParam(required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException {
        User user = service.addNewUser(firstName, lastName, username, email, role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam String firstName,
                                        @RequestParam String currentUsername,
                                        @RequestParam String lastName,
                                        @RequestParam String username,
                                        @RequestParam String email,
                                        @RequestParam String role,
                                        @RequestParam String isActive,
                                        @RequestParam String isNonLocked,
                                        @RequestParam(required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException {
        User user = service.updateUser(currentUsername, firstName, lastName, username, email, role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<?> updateUser(@RequestParam String username,
                                        @RequestParam MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException {
        User user = service.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username){
        return ResponseEntity.ok(service.findUserByUsername(username));
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) throws EmailNotFoundException, MessagingException {
        service.resetPassword(email);
        return response(HttpStatus.OK, "An Email with a new password was sent to: " + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable Long id){
        service.deleteUser(id);
        return response(HttpStatus.NO_CONTENT, "User Deleted Successfully");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        return new ResponseEntity<>(new HttpResponse(new Date(), status.value(), status, status.getReasonPhrase(), message.toUpperCase()), status);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        User registeredUser = service.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping(path = "/image/{username}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable String username, @PathVariable String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @GetMapping(path = "/image/profile/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable String profile, @PathVariable String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try(InputStream stream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = stream.read(chunk)) > 0){
                outStream.write(chunk, 0, bytesRead);
            }
        }
        return outStream.toByteArray();
    }
}
