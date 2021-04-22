package jwt.course.backend.listener;

import jwt.course.backend.domain.User;
import jwt.course.backend.service.LoginAttempService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
    private final LoginAttempService service;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User){
            User user = (User) principal;
            service.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
