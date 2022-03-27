package ks.core.auth;

import ks.app.LineageAppContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationCacheManager {
    private final static String passWordWrongCountKey = "passWordWrongCount";

    private final Map<String, Map<String, Object>> tempAuthorizationInfoMap = new ConcurrentHashMap<>();

    public static AuthorizationCacheManager getInstance() {
        return LineageAppContext.getBean(AuthorizationCacheManager.class);
    }

    public void setPassWordWrongCount(String accountName, int value) {
        put(accountName, passWordWrongCountKey, value);
    }

    public int getPassWordWrongCount(String accountName) {
        return get(accountName, passWordWrongCountKey, 0);
    }

    public void remove(String accountName) {
        tempAuthorizationInfoMap.remove(accountName);
    }

    public void put(String accountName, String key, Object value) {
        Map<String, Object> map = tempAuthorizationInfoMap.get(accountName);

        if (map == null) {
            map = new ConcurrentHashMap<>();
            map.put("createTime", System.currentTimeMillis());
        }

        map.put(key, value);

        if (!tempAuthorizationInfoMap.containsKey(accountName)) {
            tempAuthorizationInfoMap.put(accountName, map);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String accountName, String key, T defaultValue) {
        Map<String, Object> map = tempAuthorizationInfoMap.get(accountName);

        if (map != null) {
            T v = (T) map.get(key);

            if (v != null) {
                return v;
            } else {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    public Map<String, Map<String, Object>> getTempAuthorizationInfoMap() {
        return tempAuthorizationInfoMap;
    }
}
