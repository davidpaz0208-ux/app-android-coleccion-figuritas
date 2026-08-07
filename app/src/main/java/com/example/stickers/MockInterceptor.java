package com.example.stickers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    private static final String TAG = "MockInterceptor";
    private static List<Sticker> stickersCache = null;
    private final Context context;
    private final Gson gson = new Gson();

    public MockInterceptor(Context context) {
        this.context = context.getApplicationContext();
        loadMockStickers();
    }

    private synchronized void loadMockStickers() {
        if (stickersCache != null) return;

        stickersCache = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        context.getAssets().open("mock_stickers.json"),
                        StandardCharsets.UTF_8
                )
        )) {
            Type listType = new TypeToken<List<Sticker>>() {}.getType();
            List<Sticker> list = gson.fromJson(br, listType);

            if (list != null) stickersCache.addAll(list);

            Log.d(TAG, "Loaded mock stickers: " + stickersCache.size());

        } catch (IOException e) {
            Log.e(TAG, "Error loading mock_stickers.json", e);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String path = request.url().encodedPath();
        String method = request.method().toUpperCase(Locale.ROOT);

        try {

            // GET ALL
            if (method.equals("GET") && path.equals("/stickers")) {
                return jsonResponse(request, gson.toJson(stickersCache), 200, "OK");
            }

            // GET BY ID
            if (method.equals("GET") && path.startsWith("/stickers/")) {

                int id = Integer.parseInt(path.substring("/stickers/".length()));

                for (Sticker s : stickersCache) {
                    if (s.getId() == id) {
                        return jsonResponse(request, gson.toJson(s), 200, "OK");
                    }
                }

                return jsonResponse(request, "{\"error\":\"not found\"}", 404, "Not Found");
            }

            // CREATE
            if (method.equals("POST") && path.equals("/stickers")) {

                String bodyStr = requestBodyToString(request);
                Sticker s = gson.fromJson(bodyStr, Sticker.class);

                int nextId = 1;
                for (Sticker existing : stickersCache) {
                    if (existing.getId() >= nextId)
                        nextId = existing.getId() + 1;
                }

                s.setId(nextId);

                stickersCache.add(s);

                return jsonResponse(request, gson.toJson(s), 201, "Created");
            }

            // UPDATE
            if (method.equals("PUT") && path.startsWith("/stickers/")) {

                int id = Integer.parseInt(path.substring("/stickers/".length()));
                String bodyStr = requestBodyToString(request);
                Sticker incoming = gson.fromJson(bodyStr, Sticker.class);

                for (int i = 0; i < stickersCache.size(); i++) {

                    Sticker cur = stickersCache.get(i);

                    if (cur.getId() == id) {

                        if (incoming.getName() != null)
                            cur.setName(incoming.getName());

                        if (incoming.getTeam() != null)
                            cur.setTeam(incoming.getTeam());

                        if (incoming.getRarity() != null)
                            cur.setRarity(incoming.getRarity());

                        cur.setNumber(incoming.getNumber());
                        cur.setRepeated(incoming.isRepeated());

                        stickersCache.set(i, cur);

                        return jsonResponse(request, gson.toJson(cur), 200, "OK");
                    }
                }

                return jsonResponse(request, "{\"error\":\"not found\"}", 404, "Not Found");
            }

            // DELETE
            if (method.equals("DELETE") && path.startsWith("/stickers/")) {

                int id = Integer.parseInt(path.substring("/stickers/".length()));

                for (int i = 0; i < stickersCache.size(); i++) {

                    if (stickersCache.get(i).getId() == id) {
                        stickersCache.remove(i);
                        return jsonResponse(request, "", 204, "No Content");
                    }
                }

                return jsonResponse(request, "{\"error\":\"not found\"}", 404, "Not Found");
            }

            return jsonResponse(request, "{\"error\":\"unknown\"}", 404, "Not Found");

        } catch (Exception e) {

            Log.e(TAG, "Mock error", e);
            return jsonResponse(request,
                    "{\"error\":\"internal mock error\"}",
                    500,
                    "Error");
        }
    }

    private String requestBodyToString(Request request) {
        try {
            okhttp3.RequestBody body = request.body();
            if (body == null) return "";

            okio.Buffer buffer = new okio.Buffer();
            body.writeTo(buffer);

            return buffer.readUtf8();

        } catch (Exception e) {
            return "";
        }
    }

    private Response jsonResponse(Request request,
                                  String json,
                                  int code,
                                  String message) {

        ResponseBody body = ResponseBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json
        );

        return new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .code(code)
                .message(message)
                .body(body)
                .build();
    }

    public static List<Sticker> getApiStickers() {

        if (stickersCache == null)
            return new ArrayList<>();

        return new ArrayList<>(stickersCache);
    }
}




