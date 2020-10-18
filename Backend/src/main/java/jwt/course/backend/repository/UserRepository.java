package jwt.course.backend.repository;

import jwt.course.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);

}
