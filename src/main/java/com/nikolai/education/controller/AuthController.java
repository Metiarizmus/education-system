package com.nikolai.education.controller;

import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.ConfirmationToken;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.response.JwtResponse;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.security.jwt.JwtUtils;
import com.nikolai.education.security.userDetails.CustomUserDetails;
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

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication controller", description = "points for registration or login in the system")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final SendMessages sendMessages;
    private final ConfirmTokenRepo tokenRepo;

    @Operation(
            summary = "Registration in the system"
    )
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signupRequest) {
        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = new User(signupRequest.getFirstName(), signupRequest.getLastName(),
                signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getPhoneNumber());

        user.setRoles(Collections.singleton(new Role(TypeRoles.ROLE_USER)));
        userService.saveUser(user);

        log.info("user {} registering to the system", user.getEmail());
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(
            summary = "Login in the system"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        log.info("user {} signin to the system", loginRequest.getEmail());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwtToken = jwtUtils.generateRefreshJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        return ResponseEntity.ok(new JwtResponse(jwt, refreshJwtToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));

    }

    @Operation(
            summary = "Registration in the system after clicking on the link in the mail"
    )
    @PostMapping("/signup/invite")
    public ResponseEntity<?> inviteRegistr(@Valid @RequestBody UserDTO inviteRequest,
                                           @RequestParam("confirmToken") String confToken) {

        ConfirmationToken token = tokenRepo.findByConfirmationToken(confToken);

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
        if (user != null) {
            user.setFirstName(inviteRequest.getFirstName());
            user.setLastName(inviteRequest.getLastName());
            user.setPassword(inviteRequest.getPassword());
            user.setPhoneNumber(inviteRequest.getPhoneNumber());

            userService.saveUserInvite(user, token.getIdSender(), token.getIdCourse());
            log.info("invited user {} registering to the system", user.getEmail());

            return ResponseEntity.ok("User registered successfully!");
        }
        return ResponseEntity.ok("No such user was found");
    }


}
