package jwt.course.backend.resource;

import jwt.course.backend.exception.domain.ExceptionHandling;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserResource extends ExceptionHandling {

}
