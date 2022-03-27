package web.socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L1WebApiData {
    private String action;
    private Map<String, String> data = new HashMap<>();

    public L1WebApiData(String action) {
        this.action = action;
        put("api_action", action);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public List<NameValuePair> getPairData() {
        List<NameValuePair> params = new ArrayList<>();

        for (String key : data.keySet()) {
            params.add(new BasicNameValuePair(key, data.get(key)));
        }

        return params;
    }
}
