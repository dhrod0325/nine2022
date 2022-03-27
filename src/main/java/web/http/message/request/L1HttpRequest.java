package web.http.message.request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class L1HttpRequest {
    private final HttpRequest request;

    private String remoteAddress;

    public L1HttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.request = request;
        this.remoteAddress = ctx.channel().remoteAddress().toString();
    }

    public Map<String, List<String>> getParameters() {
        if (request != null) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());

            Map<String, List<String>> result = queryStringDecoder.parameters();

            if (result == null) {
                return Collections.emptyMap();
            }

            return result;
        }

        return Collections.emptyMap();
    }

    public List<String> getParameterValues(String key) {
        if (getParameters().containsKey(key)) {
            return getParameters().get(key);
        }

        return Collections.emptyList();
    }

    public String getParameter(String key) {
        if (getParameters().containsKey(key)) {
            List<String> v = getParameters().get(key);

            if (v.isEmpty()) {
                return null;
            }
            return v.get(0);
        }
        return null;
    }

    public String getSocketAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
