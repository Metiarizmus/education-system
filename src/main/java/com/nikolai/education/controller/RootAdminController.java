package com.nikolai.education.controller;

import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
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
public class RootAdminController {

    private final UserRepo userRepo;
    private final SendMessages sendMessages;


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


    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        return null;
    }


}
