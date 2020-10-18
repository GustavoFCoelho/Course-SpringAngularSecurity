package jwt.course.backend.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttempService {
    private static final int MAXIMUM_NUMBERS_OF_ATTEMPTS = 5;
    private static final int ATTEMPTS_INCREMENTS = 5;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttempService(){
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
        .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }
}
