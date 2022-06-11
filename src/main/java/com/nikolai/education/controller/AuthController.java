package com.nikolai.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.exception.TokenRefreshException;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.RefreshToken;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.RefreshTokenRequest;
import com.nikolai.education.payload.response.JwtResponse;
import com.nikolai.education.payload.response.TokenRefreshResponse;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.security.CustomUserDetails;
import com.nikolai.education.security.jwt.JwtUtils;
import com.nikolai.education.service.RefreshTokenService;
import com.nikolai.education.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication controller", description = "points for registration or login in the system")
public class AuthController {

    private final UserRepo userRepo;
    private final UserService userService;
    private final SendMessages sendMessages;
    private final ConfirmTokenRepo tokenRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Registration in the system"
    )
    @PostMapping(value = "/signup")
    public ResponseEntity<?> registerUser(@RequestParam(name = "user") String userJson,
                                          @RequestParam("avatar") MultipartFile image
    ) throws IOException {

        UserDTO signupRequest = objectMapper.readValue(userJson, UserDTO.class);

        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = new User(signupRequest.getFirstName(), signupRequest.getLastName(),
                signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getPhoneNumber());

        user.setRoles(Collections.singleton(new Role(TypeRolesEnum.ROLE_USER)));
        user.setAvatar(image.getBytes());

        userService.saveUser(user);

        log.info("user {} registering to the system", user.getEmail());
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(
            summary = "Registration in the system after clicking on the link in the mail"
    )
    @PostMapping("/signup/invite/{confirmToken}")
    public ResponseEntity<?> inviteRegistr(@RequestParam(name = "user") String userJson,
                                           @RequestParam("avatar") MultipartFile image,
                                           @PathVariable String confirmToken) throws IOException {

        InvitationLink token = tokenRepo.findByConfirmationToken(confirmToken);
        UserDTO inviteRequest = objectMapper.readValue(userJson, UserDTO.class);

        if (!userRepo.existsByEmail(token.getUser().getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: No this invited user!");
        }

        if (!sendMessages.validLink(token.getFinishDate())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Link not valid!");
        }

        User user = userRepo.getById(token.getUser().getId());
        user.setFirstName(inviteRequest.getFirstName());
        user.setLastName(inviteRequest.getLastName());
        user.setPassword(inviteRequest.getPassword());
        user.setPhoneNumber(inviteRequest.getPhoneNumber());
        user.setAvatar(image.getBytes());

        userService.saveUserInvite(user, token.getIdSender(), token.getIdCourse());
        log.info("invited user {} registering to the system", user.getEmail());

        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(
            summary = "Login to the system"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), authentication.getName(), roles, refreshToken.getExpiryDate()));

        } else {
            return (ResponseEntity<?>) ResponseEntity.notFound();
        }

    }

    @Operation(
            summary = "Get new access token by refresh token"
    )
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

//    @GetMapping("/logout")
//    public ResponseStatus logout(Principal principal) {
//
//
//    }


}