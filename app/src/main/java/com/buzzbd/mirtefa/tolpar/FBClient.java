package com.buzzbd.mirtefa.tolpar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by mirtefa on 4/12/15.
 */
public class FBClient {
    private static final String BASE_URL = "https://graph.facebook.com";

    private static AsyncHttpClient client;


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = createFBClient();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client = createFBClient();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static AsyncHttpClient createFBClient() {
        AsyncHttpClient client = new AsyncHttpClient();

//        // Add parse headers to client
//        client.addHeader("Content-Type", "application/json");
        client.addHeader("access_token", StoryActivity.mAccessToken);
        client.addHeader("name", "Tolpar");
        client.addHeader("android", "[" +
                "    {\n" +
                "      \"url\" : \"story://story/1234\",\n" +
                "      \"package\" : \"com.tolpar.mirtefa\",\n" +
                "      \"app_name\" : \"Tolpar\",\n" +
                "    },\n" +
                "  ]");
        client.addHeader("web", "'{\"should_fallback\": false}'");
        return client;
    }
}
