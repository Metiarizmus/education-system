package com.nikolai.education.controller;

import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/org/root-admin")
@PreAuthorize("hasAuthority('ROLE_ROOT_ADMIN')")
@Tag(name = "Root admin controller", description = "points of the main admin")
public class RootAdminController {

    private final UserRepo userRepo;
    private final SendMessages sendMessages;


    @Operation(
            summary = "Send invitation link for admin or manager to email"
    )
    @PostMapping("/invite-mail")
    public ResponseEntity<?> inviteAdminOrManagerMail(@Valid @RequestBody InviteRequest inviteRequest, Principal principal) {

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {

            String emailSubject = "Invitation to join to the organization";
            String mailContent = "RootAdmin invite you to the course";

            sendMessages.sendInvite(inviteRequest, emailSubject, mailContent, principal, null);

            return new ResponseEntity<>("The invitation has been sent to email " + inviteRequest.getEmail(), HttpStatus.OK);

        } else if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {

            sendMessages.sendInvite(inviteRequest, null, null, principal, null);

        }

        return new ResponseEntity<>("The invitation has been sent to telegram with number " + inviteRequest.getTelephoneNumber(), HttpStatus.OK);
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
