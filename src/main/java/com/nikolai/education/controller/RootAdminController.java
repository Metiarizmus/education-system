package com.nikolai.education.controller;

import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequestToken;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.service.UserService;
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

    private final UserService userService;
    private final SendMessages sendMessages;
    private final OrgService orgService;
    private final UserRepo userRepo;

    @Operation(
            summary = "Send invitation link for admin or manager to email"
    )
    @PostMapping("/invite")
    public ResponseEntity<?> inviteAdminOrManagerMail(@Valid @RequestBody InviteRequestToken inviteRequest, Principal principal) {

        String link = null;

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {

            String emailSubject = "Invitation to join to the organization";
            String mailContent = "RootAdmin invite you to the course";

            sendMessages.sendInvite(inviteRequest, emailSubject, mailContent, principal, null);

            return new ResponseEntity<>("The invitation has been sent to email " + inviteRequest.getEmail(), HttpStatus.OK);

        } else if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {
            link = sendMessages.sendInvite(inviteRequest, null, null, principal, null);
        }

        return new ResponseEntity<>("Give this link for invite user : " + link , HttpStatus.OK);
    }


    @Operation(
            summary = "Delete user from an organization"
    )
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUserFromOrg(id);
        return new ResponseEntity<>("User was deleted", HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an organization"
    )
    @DeleteMapping("/delete-org")
    public ResponseEntity<?> deleteOrg(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        orgService.deleteOrg(user);
        return new ResponseEntity<>("the organization was deleted", HttpStatus.OK);
    }

}
