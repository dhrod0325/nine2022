package web.http.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import web.http.message.request.L1HttpRequest;
import web.http.message.response.L1HttpResponseHandler;
import web.http.message.response.impl.L1HttpResponseHandlerImpl;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class L1HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
    private HttpRequest request;

    private L1HttpResponseHandler handler = new L1HttpResponseHandlerImpl();

    public void setWebResponse(L1HttpResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            this.request = (HttpRequest) httpObject;

            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER));
            }
        }

        if (httpObject instanceof HttpContent) {
            String body = handler.handle(new L1HttpRequest(ctx, request));

            HttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));

            response.headers().set("Access-Control-Allow-Origin", "*");
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }
}