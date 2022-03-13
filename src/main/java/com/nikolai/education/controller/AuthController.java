package com.nikolai.education.controller;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.SigninRequest;
import com.nikolai.education.payload.request.SignupRequest;
import com.nikolai.education.payload.request.SingupInviteMailRequest;
import com.nikolai.education.payload.response.JwtResponse;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.security.jwt.JwtUtils;
import com.nikolai.education.security.userDetails.CustomUserDetails;
import com.nikolai.education.service.UserService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final UserService userService;

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

        log.info("user {} registering to the system",user.getEmail());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        log.info("user {} signin to the system",loginRequest.getEmail());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwtToken= jwtUtils.generateRefreshJwtToken(authentication);

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

    @PostMapping("/inviteMail/signup")
    public ResponseEntity<?> inviteRegistr(@Valid @RequestBody SingupInviteMailRequest inviteRequest,
                                           @RequestParam("mail") String mail, @RequestParam("lastDate") String lastDate,
                                           @RequestParam("role") TypeRoles typeRoles,
                                           @RequestParam("userId") Long userId, @RequestParam("orgId") Long orgId) {

        if (!userRepo.existsByEmail(mail)) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: No this invited user!");
        }

        if (!validLink(lastDate)) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Link not valid!");
        }

        User user = new User(inviteRequest.getFirstName(), inviteRequest.getLastName(),
                            mail, inviteRequest.getPassword(), inviteRequest.getPhoneNumber()
                            );
        user.setId(userId);

        userService.saveUserInvite(user,typeRoles, orgId);
        log.info("invited user {} registering to the system",user.getEmail());

        return ResponseEntity.ok("User registered successfully!");

    }

    private static boolean validLink(String lastDate) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        String now = formatter.format(calendar.getTime());

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = formatter.parse(lastDate);
            date2 = formatter.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int res = date1.compareTo(date2);
        if (res > 0) {
            return true;
        }
        if (res == 0) {
            return true;
        }

        return false;
    }
}
