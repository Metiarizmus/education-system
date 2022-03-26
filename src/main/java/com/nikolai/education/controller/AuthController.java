package com.nikolai.education.controller;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.ConfirmationToken;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.SigninRequest;
import com.nikolai.education.payload.request.SignupRequest;
import com.nikolai.education.payload.request.SingupInviteRequest;
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = new User(signupRequest.getFirstName(), signupRequest.getLastName(),
                signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getPhoneNumber());

        userService.saveUser(user, TypeRoles.ROLE_USER);

        log.info("user {} registering to the system", user.getEmail());
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(
            summary = "Login in the system"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest loginRequest) {

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
    public ResponseEntity<?> inviteRegistr(@Valid @RequestBody SingupInviteRequest inviteRequest,
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
        user.setFirstName(inviteRequest.getFirstName());
        user.setLastName(inviteRequest.getLastName());
        user.setPassword(inviteRequest.getPassword());
        user.setPhoneNumber(inviteRequest.getPhoneNumber());

    //    TypeWayInvited wayInvited = TypeWayInvited.MAIL;

//        if (token.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {
//            String mailContent = "Please confirm that you click on the link in telegram for number phone " + user.getPhoneNumber();
//            String linkConfirm = "";
//            String emailSubject = "Confirmation user";
//            sendMessages.sendNotificationAccepted(user.getEmail(), mailContent, emailSubject);
//            wayInvited = TypeWayInvited.TELEGRAM;
//        }

        userService.saveUserInvite(user, token.getIdSender(), token.getIdCourse());
        log.info("invited user {} registering to the system", user.getEmail());

        return ResponseEntity.ok("User registered successfully!");
    }




}
