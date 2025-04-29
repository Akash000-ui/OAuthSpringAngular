package com.oauth.springangularoauth.restController;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");

        try {
            GoogleIdToken.Payload payload = verifyToken(idToken);
            if (payload != null) {
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");

                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "name", name,
                        "picture", picture
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("96934569111-4gg4q9mkhei39id5bvm830b6e5esq6pg.apps.googleusercontent.com")) // 👈 Replace with your client ID
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        return idToken != null ? idToken.getPayload() : null;
    }
}