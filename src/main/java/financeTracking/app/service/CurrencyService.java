package financeTracking.app.service;

import financeTracking.app.model.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CurrencyService {

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String url = apiUrl + "?access_key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("rates")) {
                    Map<String, Double> rates = (Map<String, Double>) body.get("rates");

                    if (rates.containsKey(fromCurrency) && rates.containsKey(toCurrency)) {
                        return rates.get(toCurrency) / rates.get(fromCurrency);
                    }
                }
            }
            throw new RuntimeException("Invalid API response for exchange rates.");
        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rate: " + e.getMessage());
        }
    }
}