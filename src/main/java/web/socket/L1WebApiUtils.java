package web.socket;

public class L1WebApiUtils {
    public static void send(L1WebApiData vo) {
        L1WebApiListener.getInstance().putApi(vo);
    }

    public static void chatLog(String type, String name, String target, String msg) {
        L1WebApiData apiData = new L1WebApiData("chat");
        apiData.put("name", name);
        apiData.put("target", target);
        apiData.put("text", msg);
        apiData.put("type", type);

        send(apiData);
    }
}
