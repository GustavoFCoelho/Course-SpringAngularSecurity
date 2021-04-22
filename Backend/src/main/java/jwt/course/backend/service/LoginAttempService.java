package jwt.course.backend.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttempService {
    private static final int MAXIMUM_NUMBERS_OF_ATTEMPTS = 5;
    private static final int ATTEMPTS_INCREMENTS = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttempService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) throws ExecutionException {
        int attemps = 0;
        attemps = ATTEMPTS_INCREMENTS + loginAttemptCache.get(username);
        loginAttemptCache.put(username, attemps);

    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_NUMBERS_OF_ATTEMPTS;
    }
}
