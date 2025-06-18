package jeu.terralib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class APIUtils {
    private APIUtils() {}
    private static final HashMap<String, APIInfo> apiCache = new HashMap<>();
    private static final HttpClient httpClient = HttpClient.newHttpClient();


    public static CompletableFuture<JsonObject> accessAPI(String apiName, PARAM[] param) {
        String url = "https://api.hypixel.net/v2/" + apiName;
        APIInfo info = apiCache.get(url);
        long currentTime = System.currentTimeMillis();
        if (info != null && currentTime - info.lastUpdate < info.cooldown) {
            return CompletableFuture.completedFuture(info.data);
        }
        return updateAPI(url, param);
    }


    private static CompletableFuture<JsonObject> updateAPI(String apiName, PARAM[] param) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder urlBuilder = new StringBuilder(apiName).append("?");
                for (PARAM p : param) {
                    urlBuilder.append(p.name).append("=").append(p.value).append("&");
                }
                if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
                    urlBuilder.setLength(urlBuilder.length() - 1);
                }
                String fullUrl = "https://jeu-backend.onrender.com/get?link=" + urlBuilder; // use proxy to hide API key
                System.out.println("Accessing API: " + fullUrl);
                long lastUpdate = System.currentTimeMillis();
                long cooldown = apiCache.get(fullUrl) != null && apiCache.get(fullUrl).cooldown != 0
                        ? apiCache.get(fullUrl).cooldown : 60000;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(fullUrl))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
                apiCache.put(fullUrl, new APIInfo(data, lastUpdate, cooldown));
                return data;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public record PARAM(String name, String value) {}
    public record APIInfo(JsonObject data, long lastUpdate, long cooldown){}

    public static CompletableFuture<String> getUsername(String uuid){
        return accessAPI(uuid, new PARAM[]{new PARAM("uuid", uuid)})
                .thenApply(jsonObject -> {
                    if (jsonObject.has("player") && jsonObject.get("player").isJsonObject()) {
                        JsonObject player = jsonObject.getAsJsonObject("player");
                        if (player.has("displayname")) {
                            return player.get("displayname").getAsString();
                        }
                    }
                    return "Unknown Player";
                });
    }

    public static CompletableFuture<String> getUUID(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
                return data.get("id").getAsString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}