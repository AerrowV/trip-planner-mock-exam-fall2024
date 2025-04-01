package dat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.dtos.PackingItemDTO;
import dat.entities.enums.TripCategory;
import dat.exceptions.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class PackingService {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    ;

    public static List<PackingItemDTO> getPackingItems(TripCategory category) {
        try {
            String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + category.name().toLowerCase();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(
                    mapper.readTree(response.body())
                            .get("items")
                            .traverse(mapper)
                            .readValueAs(PackingItemDTO[].class)
            );

        } catch (Exception e) {
            throw new ApiException(500, "Could not fetch packing list for category: " + category + ". " + e.getMessage());
        }
    }
}

