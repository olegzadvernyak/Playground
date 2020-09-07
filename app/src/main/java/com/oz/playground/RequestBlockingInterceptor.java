package com.oz.playground;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestBlockingInterceptor implements Interceptor {

    private boolean isBlockEnabled = false;

    public void setBlockEnabled(boolean blockEnabled) {
        isBlockEnabled = blockEnabled;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (isBlockEnabled) {
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(400)
                    .message("Request is blocked")
                    .body(ResponseBody.create(MediaType.parse("text/html"), ""))
                    .build();
        }
        return chain.proceed(request);
    }

}
