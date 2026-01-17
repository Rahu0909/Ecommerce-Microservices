package com.ecom.microservice.app.service;

import com.ecom.microservice.app.dto.UserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class KeyCloakAdminService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${keycloak.admin.username}")
    private String adminUsername;
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    @Value("${keycloak.admin.service-url}")
    private String keycloakServiceUrl;
    @Value("${keycloak.admin.realm}")
    private String realm;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.client-uid}")
    private String clientUid;

    public String getAdminAccessToken() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(params, headers);
        String url = keycloakServiceUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                entity, Map.class
        );
        return response.getBody().get("access_token").toString();
    }

    public String createUser(String token, UserRequest userRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> userPaylod = new HashMap<>();
        userPaylod.put("username", userRequest.getUserName());
        userPaylod.put("email", userRequest.getEmail());
        userPaylod.put("enabled", true);
        userPaylod.put("firstName", userRequest.getFirstName());
        userPaylod.put("lastName", userRequest.getLastName());

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", userRequest.getPassword());
        credentials.put("temporary", false);
        userPaylod.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPaylod, headers);
        String url = keycloakServiceUrl + "/admin/realms/" + realm + "/users";
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity, String.class
        );
        if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
            throw new RuntimeException("Failed to create user in keycloak " + response.getBody());
        }

        //Extract Keycloak user id
        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new RuntimeException("Keycloak did not return Location Header " + response.getBody());
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private Map<String, Object> getRealmRoleRepresentation(String token, String roleName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = keycloakServiceUrl + "/admin/realms/" +
                realm + "/clients/" + clientUid + "/roles/" + roleName;
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );
        return response.getBody();
    }

    public void assignRealmRoleToUser(String username, String roleName, String userId) {
        String token = getAdminAccessToken();
        Map<String, Object> roleRep = getRealmRoleRepresentation(
                token, roleName
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<List<Map<String, Object>>> entity =
                new HttpEntity<>(List.of(roleRep), headers);

        String url = keycloakServiceUrl + "/admin/realms/" +
                realm + "/users/" + userId + "/role-mappings/clients/" + clientUid;
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url, entity, Void.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Failed to assign role " + roleName + " to user " + username +
                            ": HTTP " + response.getStatusCode()
            );
        }
    }
}
