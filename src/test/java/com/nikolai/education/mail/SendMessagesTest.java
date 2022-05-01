package com.nikolai.education.mail;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.ConfirmationTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SendMessagesTest {

    @Mock
    private MailService mailService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserLogsRepo userLogsRepo;
    @Mock
    private InvitationLink invitationLink;

    @InjectMocks
    private SendMessages sendMessages;

    private User senderTest;
    private User recipientTest;

    @BeforeEach
    public void setUp() {
        senderTest = new User("Sender", "Sender", "sender@mail.ru", "123", "123456789");
        recipientTest = new User("Recipient", "Recipient", "resip@mail.ru", "1234", "375444794632");

    }

    @Test
    void sendInviteExistInTheSystem() {

        InviteRequest testInviteRequest = new InviteRequest("resip@mail.ru", "375444794632", TypeRolesEnum.ROLE_USER, 32, TypeWayInvitedEnum.TELEGRAM);
        if (TypeWayInvitedEnum.TELEGRAM.equals(testInviteRequest.getTypeWayInvited())) {
            testInviteRequest.setChatId(1);
            testInviteRequest.setBotToken("asadascjh-1w12sajda-2dsc");
        }

        when(userRepo.findByEmail(anyString())).thenReturn(senderTest);
        when(userRepo.existsByEmail(testInviteRequest.getEmail())).thenReturn(true);
        when(userRepo.findByEmail(anyString())).thenReturn(recipientTest);

        String emailSubject = "Invitation to join to the organization";
        String content = "Admin invite you to the organization ";
        sendMessages.sendInvite(testInviteRequest, emailSubject, content, senderTest.getEmail(), 1L);
        //verify(confirmationTokenService, times(1)).saveConfirmToken(new InvitationLink(), testInviteRequest.getRole());
    }

    @Test
    void sendInviteNotExistInTheSystem() {

        InviteRequest testInviteRequest = new InviteRequest("resip@mail.ru", "375444794632", TypeRolesEnum.ROLE_USER, 32, TypeWayInvitedEnum.MAIL);
        if (TypeWayInvitedEnum.TELEGRAM.equals(testInviteRequest.getTypeWayInvited())) {
            testInviteRequest.setChatId(1);
            testInviteRequest.setBotToken("asadascjh-1w12sajda-2dsc");
        }

        when(userRepo.findByEmail(anyString())).thenReturn(senderTest);
        when(userRepo.existsByEmail(testInviteRequest.getEmail())).thenReturn(false);
        when(userRepo.findByEmail(anyString())).thenReturn(recipientTest);

        String emailSubject = "Invitation to join to the organization";
        String content = "Admin invite you to the organization ";
        sendMessages.sendInvite(testInviteRequest, emailSubject, content, senderTest.getEmail(), 1L);
        //verify(confirmationTokenService, times(1)).saveConfirmToken(new InvitationLink(), testInviteRequest.getRole());
    }


    @Test
    void sendNotificationAccepted() {
        String mailContent = String.format("User accepted invitation to the course");
        String emailSubject = "accepted the invitation";
        sendMessages.sendNotificationAccepted(recipientTest.getEmail(), mailContent, emailSubject);
    }

    @Test
    void validLinkTrue() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String now = formatter.format(calendar.getTime());

        boolean result = sendMessages.validLink(now);
        Assertions.assertEquals(true, result);
    }

    @Test
    void validLinkFalse() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -100);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String now = formatter.format(calendar.getTime());

        boolean result = sendMessages.validLink(now);
        Assertions.assertEquals(false, result);
    }
}
