package com.example.players;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
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
    private static List<Player> playersCache = null;
    private final Context context;
    private final Gson gson = new Gson();

    public MockInterceptor(Context context) {
        this.context = context.getApplicationContext();
        loadMockPlayers();
    }

    private synchronized void loadMockPlayers() {
        if (playersCache != null) return;
        playersCache = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        context.getAssets().open("mock_players.json"),
                        StandardCharsets.UTF_8
                )
        )) {
            Type listType = new TypeToken<List<Player>>() {}.getType();
            List<Player> list = gson.fromJson(br, listType);

            if (list != null) playersCache.addAll(list);

            Log.d(TAG, "Loaded mock players: " + playersCache.size());

        } catch (Exception e) {
            Log.e(TAG, "Error loading mock_players.json", e);
        }
    }

    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request();
        String path = request.url().encodedPath();
        String method = request.method().toUpperCase(Locale.ROOT);

        try {
            // GET /players
            if (method.equals("GET") && path.equals("/players")) {
                return jsonResponse(request, gson.toJson(playersCache), 200, "OK");
            }

            // POST /players
            if (method.equals("POST") && path.equals("/players")) {
                String bodyStr = requestBodyToString(request);
                Player p = gson.fromJson(bodyStr, Player.class);

                int nextId = 1;
                for (Player existing : playersCache) {
                    if (existing.getId() >= nextId) nextId = existing.getId() + 1;
                }
                p.setId(nextId);

                if (p.getGoals() == 0) p.setGoals(0);
                if (p.getMatchesPlayed() == 0) p.setMatchesPlayed(0);

                playersCache.add(p);
                return jsonResponse(request, gson.toJson(p), 201, "Created");
            }

            // PUT /players/{id}
            if (method.equals("PUT") && path.startsWith("/players/")) {
                int id = Integer.parseInt(path.substring("/players/".length()));
                String bodyStr = requestBodyToString(request);
                Player incoming = gson.fromJson(bodyStr, Player.class);

                for (int i = 0; i < playersCache.size(); i++) {
                    Player cur = playersCache.get(i);

                    if (cur.getId() == id) {
                        if (incoming.getName() != null) cur.setName(incoming.getName());
                        if (incoming.getPosition() != null) cur.setPosition(incoming.getPosition());
                        cur.setGoals(incoming.getGoals());
                        cur.setMatchesPlayed(incoming.getMatchesPlayed());
                        playersCache.set(i, cur);
                        return jsonResponse(request, gson.toJson(cur), 200, "OK");
                    }
                }
                return jsonResponse(request, "{\"error\":\"not found\"}", 404, "Not Found");
            }

            // DELETE /players/{id}
            if (method.equals("DELETE") && path.startsWith("/players/")) {
                int id = Integer.parseInt(path.substring("/players/".length()));
                for (int i = 0; i < playersCache.size(); i++) {
                    if (playersCache.get(i).getId() == id) {
                        playersCache.remove(i);
                        return jsonResponse(request, "", 204, "No Content");
                    }
                }
                return jsonResponse(request, "{\"error\":\"not found\"}", 404, "Not Found");
            }

            return jsonResponse(request, "{\"error\":\"unknown\"}", 404, "Not Found");

        } catch (Exception e) {
            Log.e(TAG, "Mock error", e);
            return jsonResponse(request, "{\"error\":\"internal mock error\"}", 500, "Error");
        }
    }

    private String requestBodyToString(Request request) {
        try {
            okhttp3.Request copy = request.newBuilder().build();
            okhttp3.RequestBody body = copy.body();
            if (body == null) return "";
            okio.Buffer buffer = new okio.Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            return "";
        }
    }

    private Response jsonResponse(Request request, String json, int code, String message) {
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
}
