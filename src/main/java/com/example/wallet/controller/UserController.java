package com.example.wallet.controller;

import com.example.wallet.dto.AuthRequest;
import com.example.wallet.dto.TransferRequest;
import com.example.wallet.dto.UserDto;
import com.example.wallet.service.AuthService;
import com.example.wallet.service.TransferService;
import com.example.wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final TransferService transferService;
    private final UserService userService;

    @PostMapping("/auth/token")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody @Valid AuthRequest request) {
        return authService.authenticate(request.getEmail(), request.getPhone(), request.getPassword())
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid TransferRequest transferRequest,
                                      Authentication authentication) {
        Long fromUserId = (Long) authentication.getPrincipal();
        transferService.transfer(fromUserId, transferRequest.getToUserId(), transferRequest.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer completed"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam Optional<String> name,
                                                     @RequestParam Optional<String> email,
                                                     @RequestParam Optional<String> phone,
                                                     @RequestParam Optional<String> dateOfBirth,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchUsers(name, email, phone, dateOfBirth, page, size));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userService.getDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/emails")
    public ResponseEntity<?> addEmail(Authentication authentication, @RequestParam String email) {
        Long userId = (Long) authentication.getPrincipal();
        userService.addEmail(userId, email);
        return ResponseEntity.ok("Email added");
    }

    @DeleteMapping("/users/emails")
    public ResponseEntity<?> removeEmail(Authentication authentication, @RequestParam String email) {
        Long userId = (Long) authentication.getPrincipal();
        userService.removeEmail(userId, email);
        return ResponseEntity.ok("Email removed");
    }

    @PostMapping("/users/phones")
    public ResponseEntity<?> addPhone(Authentication authentication, @RequestParam String phone) {
        Long userId = (Long) authentication.getPrincipal();
        userService.addPhone(userId, phone);
        return ResponseEntity.ok("Phone added");
    }

    @DeleteMapping("/users/phones")
    public ResponseEntity<?> removePhone(Authentication authentication, @RequestParam String phone) {
        Long userId = (Long) authentication.getPrincipal();
        userService.removePhone(userId, phone);
        return ResponseEntity.ok("Phone removed");
    }
}
