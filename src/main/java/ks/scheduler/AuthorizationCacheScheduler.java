package ks.scheduler;

import ks.core.auth.AuthorizationCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorizationCacheScheduler {
    private static final long CACHE_TIME = 1000 * 60;

    private final AuthorizationCacheManager authorizationManager = AuthorizationCacheManager.getInstance();

    @Scheduled(fixedDelay = 1000)
    public void scheduled() {
        Map<String, Map<String, Object>> authorizationInfoMap = authorizationManager.getTempAuthorizationInfoMap();

        authorizationInfoMap.keySet().forEach(key -> {
            Map<String, Object> map = authorizationInfoMap.get(key);

            Long createTime = (Long) map.get("createTime");

            if (System.currentTimeMillis() - createTime > CACHE_TIME) {
                authorizationManager.remove(key);
            }
        });
    }
}
