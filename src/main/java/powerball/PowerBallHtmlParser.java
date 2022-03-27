package powerball;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import powerball.vo.PowerBall;
import powerball.vo.PowerBallResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerBallHtmlParser {
    public static final String parseUrl = "https://www.powerballgame.co.kr/";

    public PowerBallResult parse(String date) throws IOException {
        return parse(date, 1);
    }

    private JSONObject getJsonObject(String date, int page) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("view", "action");
        param.put("action", "ajaxPowerballLog");
        param.put("actionType", "dayLog");
        param.put("date", date);
        param.put("page", page + "");

        String body = Jsoup.connect(parseUrl).data(param).ignoreContentType(true).post().body().text();

        return new JSONObject(body);
    }

    private PowerBall resultFromJson(JSONObject json) {
        PowerBall result = new PowerBall();

        result.setNumber(json.get("number") + "");
        result.setNumberSum(json.get("numberSum") + "");
        result.setNumberPeriod(json.get("numberPeriod") + "");
        result.setPowerball(json.get("powerball") + "");
        result.setPowerballPeriod(json.get("powerballPeriod") + "");
        result.setPowerballUnderOver(json.get("powerballUnderOver") + "");
        result.setRound(json.get("round") + "");
        result.setTime(json.get("time") + "");
        result.setTodayRound(json.get("todayRound") + "");
        result.setNumberUnderOver(json.get("numberUnderOver") + "");
        result.setNumberSumPeriod(json.get("numberSumPeriod") + "");

        return result;
    }

    public PowerBallResult parse(String date, int page) throws IOException {
        JSONObject body = getJsonObject(date, page);
        JSONArray content = body.getJSONArray("content");
        String endYN = body.getString("endYN");

        List<PowerBall> powerBalls = new ArrayList<>();

        content.forEach(o -> {
            if (o instanceof JSONObject) {
                JSONObject json = (JSONObject) o;

                PowerBall result = resultFromJson(json);
                result.setDate(date);

                powerBalls.add(result);
            }
        });

        PowerBallResult result = new PowerBallResult();
        result.setResults(powerBalls);
        result.setEndYn(endYN);

        return result;
    }
}
