package org.aicart.sslcommerz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class SslcommerzService {

    @ConfigProperty(name = "sslcommerz.validationUrl")
    String validationUrl;

    @ConfigProperty(name = "sslcommerz.storeId")
    String storeId;

    @ConfigProperty(name = "sslcommerz.storePassword")
    String storePassword;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SslcommerzService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public SslcommerzResponse sslcommerzPaymentVerify(String valId) {

            String urlWithParams = String.format("%s?val_id=%s&store_id=%s&store_passwd=%s&format=json",
                    validationUrl,
                    URLEncoder.encode(valId, StandardCharsets.UTF_8),
                    URLEncoder.encode(storeId, StandardCharsets.UTF_8),
                    URLEncoder.encode(storePassword, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlWithParams))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                return objectMapper.readValue(response.body(), new TypeReference<SslcommerzResponse>() { });
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to fetch posts", e);
            }
    }
}
