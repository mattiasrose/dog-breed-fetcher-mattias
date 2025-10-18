package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        String key = breed == null ? "" : breed.toLowerCase(Locale.ROOT).trim();
        String url = "https://dog.ceo/api/breed/" + key + "/list";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException("No response body for breed: " + key);
            }

            String body = response.body().string();
            JSONObject root = new JSONObject(body);

            String status = root.optString("status", "");
            if ("error".equals(status)) {
                throw new BreedNotFoundException("Breed not found: " + key);
            }

            JSONArray msg = root.optJSONArray("message");
            List<String> out = new ArrayList<>();
            if (msg != null) {
                for (int i = 0; i < msg.length(); i++) {
                    out.add(msg.getString(i));
                }
            }
            return out;
        } catch (IOException | org.json.JSONException e) {
            throw new BreedNotFoundException("Failed to fetch sub-breeds for: " + key);
        }
        // return statement included so that the starter code can compile and run.
    }
}