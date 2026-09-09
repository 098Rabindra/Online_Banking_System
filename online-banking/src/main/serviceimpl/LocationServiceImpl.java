package com.bank.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bank.service.LocationService;

@Service
@SuppressWarnings("null")
public class LocationServiceImpl implements LocationService {

    @Value("${csc.api.key}")
    private String apiKey;

    @Value("${csc.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public LocationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpEntity<String> getEntity() {

        HttpHeaders headers = new HttpHeaders();

        headers.set("X-CSCAPI-KEY", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(headers);
    }

    @Override
    public String getCountries() {

        String url = baseUrl + "/countries";

        try {

            System.out.println("Calling URL: " + url);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            getEntity(),
                            String.class);

            System.out.println("Status: " + response.getStatusCode());

            return response.getBody();

        } catch (Exception e) {

            System.out.println("API URL : " + url);
            System.out.println("API KEY : " + apiKey);

            e.printStackTrace();

            return "[]";
        }
    }

    @Override
    public String getStates(String countryCode) {

        String url =
                baseUrl +
                "/countries/" +
                countryCode +
                "/states";

        try {

            System.out.println("Calling URL: " + url);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            getEntity(),
                            String.class);

            System.out.println("Status: " + response.getStatusCode());

            return response.getBody();

        } catch (Exception e) {

            System.out.println("API URL : " + url);
            System.out.println("API KEY : " + apiKey);

            e.printStackTrace();

            return "[]";
        }
    }

    @Override
    public String getCities(String countryCode, String stateCode) {

        String url =
                baseUrl +
                "/countries/" +
                countryCode +
                "/states/" +
                stateCode +
                "/cities";

        try {

            System.out.println("Calling URL: " + url);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            getEntity(),
                            String.class);

            System.out.println("Status: " + response.getStatusCode());

            return response.getBody();

        } catch (Exception e) {

            System.out.println("API URL : " + url);
            System.out.println("API KEY : " + apiKey);

            e.printStackTrace();

            return "[]";
        }
    }
}