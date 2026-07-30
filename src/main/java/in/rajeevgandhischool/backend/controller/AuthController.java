package in.rajeevgandhischool.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${admin.password:RajeevAdmin2026!}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String inputPassword = body.get("password");

        if (adminPassword.equals(inputPassword)) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", "rg-admin-auth-token-2026",
                "message", "Authentication successful"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Invalid admin password"
            ));
        }
    }
}
