package web.http.message.response;

import web.http.message.request.L1HttpRequest;

public interface L1HttpResponseHandler {
    String handle(L1HttpRequest request);
}
