package com.nikolai.education.mail;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.ConfirmationTokenService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendMessages {

    private final MailService mailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepo userRepo;
    private final UserLogsRepo userLogsRepo;

    private static final String COURSE_LINK = "http://localhost:8080/api/users/accept-course?confirmToken=%s";
    private static final String REGISTR_LINK = "http://localhost:8080/api/auth/signup/invite?confirmToken=%s";

    public void sendInvite(InviteRequest recipient, String emailSubject, String content, String emailSender, Long idCourse) {

        User sender = userRepo.findByEmail(emailSender);

        User user;
        boolean isExists = userRepo.existsByEmail(recipient.getEmail());
        System.out.println("isExist :: " + isExists);
        if (isExists) {
            user = userRepo.findByEmail(recipient.getEmail());
        } else {
            user = new User();
            if (recipient.getRole() != null) {
                user.setRoles(Collections.singleton(new Role(recipient.getRole())));
            }else  {
                user.setRoles(Collections.singleton(new Role(TypeRolesEnum.ROLE_USER)));
            }
            user.setEmail(recipient.getEmail());
            user.setPhoneNumber(recipient.getTelephoneNumber());
        }


        InvitationLink confirmationToken = new InvitationLink(user, recipient.getExpirationDateCount(), sender.getId(), recipient.getTypeWayInvited());
        confirmationToken.setIdCourse(idCourse);
        confirmationTokenService.saveConfirmToken(confirmationToken, recipient.getRole());

        String link;

        if (isExists) {
            link = String.format(COURSE_LINK, confirmationToken.getConfirmationToken());
        } else {
            link = String.format(REGISTR_LINK, confirmationToken.getConfirmationToken());
        }

        if (recipient.getTypeWayInvited().equals(TypeWayInvitedEnum.MAIL)) {
            link = "<a href=" + link + ">click</a>";
            log.info("send invite link {} to email in the org", link);
            sendMessageToEmail(sender.getEmail(), recipient.getEmail(), emailSubject, content, link);
        } else {
            log.info("send invite link {} to telegram in the org", link);
            sendMessageToTelegram(recipient.getBotToken(), recipient.getChatId(), content + " " + link);
        }
        Logs logs = new Logs(UserLogsEnum.INVITE, user);
        userLogsRepo.save(logs);
    }

    @Async
    public void sendMessageToEmail(String emailSender, String emailRecipient, String emailSubject,
                                   String emailContent, String link) {
        Mail mail = new Mail();
        mail.setMailFrom(emailSender);
        mail.setMailTo(emailRecipient);
        mail.setMailSubject(emailSubject);
        mail.setMailContent(emailContent + link);
        mailService.sendEmail(mail);
    }


    @Async
    public void sendMessageToTelegram(String botToken, int chatId, String message) {
        TelegramBot bot = new TelegramBot(botToken);

        SendMessage request = new SendMessage(chatId, message);
        bot.execute(request);
    }

    @Async
    public void sendNotificationAccepted(String emailTo, String mailContent, String emailSubject) {

        Mail mail = new Mail();
        mail.setMailFrom(emailTo);
        mail.setMailTo(emailTo);
        mail.setMailSubject(emailSubject);
        mail.setMailContent(mailContent);
        mailService.sendEmail(mail);

        log.info("send notification to email {} ", emailTo);
    }


    public boolean validLink(String lastDate) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String now = formatter.format(calendar.getTime());

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = formatter.parse(lastDate);
            date2 = formatter.parse(now);
        } catch (ParseException e) {
            System.err.println("date parse exception");
        }

        int res = date1.compareTo(date2);
        return res >= 0;
    }


}
