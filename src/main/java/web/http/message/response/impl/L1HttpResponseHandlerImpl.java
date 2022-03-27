package web.http.message.response.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ks.commands.gm.GmCommands;
import ks.commands.gm.command.executor.L1UserCalc;
import ks.commands.gm.command.executor.L1Who;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import web.http.message.request.L1HttpRequest;
import web.http.message.response.L1HttpResponseHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L1HttpResponseHandlerImpl implements L1HttpResponseHandler {
    private static final Logger logger = LogManager.getLogger(L1HttpResponseHandlerImpl.class);

    private final static ObjectMapper o = new ObjectMapper();

    @Override
    public String handle(L1HttpRequest request) {
        JSONObject result = new JSONObject();
        result.put("result", true);

        try {
            String socketAddress = request.getSocketAddress();

            if (!socketAddress.startsWith("/0:0:0:0:0:0:0:1") && !socketAddress.startsWith("/127.0.0.1")) {
                result.put("result", false);
                result.put("message", "허용하지않음");

                logger.warn("[웹서버] 비정상접근 아이피 : " + socketAddress);

                return result.toString();
            }

            String action = request.getParameter("action");

            if (action == null) {
                result.put("result", false);
                result.put("message", "정상 파라메터가 넘어오지 않음");

                return result.toString();
            }

            String msg = "[웹서버] action : " + action;

            L1CommonUtils.sendMessageToAllGm(msg);

            logger.info(msg);

            Object data = handleResponse(action);
            return o.writeValueAsString(data);
        } catch (Exception e) {
            result.put("result", false);
            result.put("message", e.getMessage());

            logger.error("오류", e);
        }

        return result.toString();
    }

    private Object handleResponse(String action) {
        Map<String, Object> o = new HashMap<>();
        o.put("result", false);

        try {
            switch (action) {
                case "userCount":
                    L1Who.WhoResult whoResult = L1Who.getWhoResult();
                    List<String> robotList = whoResult.robotList;
                    List<String> gmList = whoResult.gmList;
                    List<String> playList = whoResult.playList;

                    int noUserCount = L1UserCalc.getCalcUser();

                    o.put("noUserCount", noUserCount);
                    o.put("robotCount", robotList.size());
                    o.put("gmCount", gmList.size());
                    o.put("userCount", playList.size());

                    break;

            }

            GmCommands.getInstance().handleCommands(action);
            o.put("result", true);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return o;
    }
}
