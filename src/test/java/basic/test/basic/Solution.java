package basic.test.basic;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static String solution(String[] participant, String[] completion) {
        Map<String, Integer> participantMap = new HashMap<>();

        for (String p : participant) {

            if (participantMap.containsKey(p)) {
                participantMap.put(p, participantMap.get(p) + 1);
            } else {
                participantMap.put(p, 1);
            }
        }

        for (String c : completion) {
            participantMap.put(c, participantMap.get(c) - 1);
        }

        for (String key : participantMap.keySet()) {
            int v = participantMap.get(key);

            if (v != 0) {
                return key;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        solution(new String[]{"mislav", "stanko", "mislav", "ana"}, new String[]{"stanko", "ana", "mislav"});
    }
}
