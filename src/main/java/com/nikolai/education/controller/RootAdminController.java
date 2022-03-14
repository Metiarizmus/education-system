package com.nikolai.education.controller;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.MailService;
import com.nikolai.education.model.*;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.payload.request.OrgRequest;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.ObjectLinkService;
import com.nikolai.education.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/org/rootAdmin")
@PreAuthorize("hasAuthority('ROLE_ROOT_ADMIN')")
public class RootAdminController {

    private final OrgService orgService;
    private final MailService mailService;
    private final ObjectLinkService objectLinkService;
    private static final Integer dateExpirationDay = 1;
    private final UserRepo userRepo;
    private final OrgRepo orgRepo;

    @PostMapping("/invite")
    public ResponseEntity<?> inviteAdminOrManager(@Valid @RequestBody InviteRequest inviteRequest, Principal principal) {
        User sender = userRepo.findByEmail(principal.getName());
        Organization organization = orgRepo.findByCreatorId(sender.getId());

        User user = new User();
        user.setRoles(Collections.singleton(new Role(inviteRequest.getRole())));

        if (TypeWayInvited.MAIL.equals(inviteRequest.getTypeWayInvited())) {
            Mail mail = new Mail();
            mail.setMailFrom(sender.getEmail());
            mail.setMailTo(inviteRequest.getEmail());
            mail.setMailSubject("Invitation to join the organization");

            user.setEmail(inviteRequest.getEmail());

            ObjectLink objectLink = null;
            try {
                objectLink = new ObjectLink(user, dateExpirationDay, sender.getId());
                objectLinkService.saveLinkObject(objectLink, inviteRequest.getRole());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String link = String.format("<a href=http://localhost:8080/api/auth/inviteMail/signup?mail=%s&lastDate=%s&role=%s&userId=%s&orgId=%d>click</a>",
                                        objectLink.getUser().getEmail(), objectLink.getFinishDate(),
                                        inviteRequest.getRole().name(), objectLink.getUser().getId(),
                                        organization.getId()
                                        );


            mail.setMailContent("Root admin invite you to be an admin  in the organization, please click here : " + link);
            mailService.sendEmail(mail);

            return new ResponseEntity<Object>("invite was send to email", HttpStatus.OK);
        }

        return null;
    }


}
