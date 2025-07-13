package xyz.aqlabs.janitorTool.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import xyz.aqlabs.janitorTool.models.output.WrapperResponse;

@Slf4j
@Component
public class ClientWrapper {

    private final WebClient webClient;

    private final String key = System.getenv("GITHUB_APIKEY");

    public ClientWrapper(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public WrapperResponse get(String url) {
        log.info("Request received GET {}", url);
        String responseBody = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + key)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("GET error response: {}", error);
                                    return Mono.error(new RuntimeException("GET request failed: " + error));
                                }))
                .bodyToMono(String.class)
                .block();

        return new WrapperResponse(responseBody, 200); // You can enhance this to reflect the actual status code if needed
    }

    public WrapperResponse delete(String url) {
        log.info("Request received DELETE {}", url);
        String responseBody = webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + key)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("DELETE error response: {}", error);
                                    return Mono.error(new RuntimeException("DELETE request failed: " + error));
                                }))
                .bodyToMono(String.class)
                .block();

        return new WrapperResponse(responseBody, 200);
    }

    public WrapperResponse patch(String url, String body) {
        log.info("Request received PATCH {}", url);
        String responseBody = webClient.patch()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + key)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("PATCH error response: {}", error);
                                    return Mono.error(new RuntimeException("PATCH request failed: " + error));
                                }))
                .bodyToMono(String.class)
                .block();
        return new WrapperResponse(responseBody, 200);
    }
}