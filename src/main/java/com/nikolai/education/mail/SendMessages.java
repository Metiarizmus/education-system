package com.nikolai.education.mail;

import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.model.ConfirmationToken;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.ConfirmationTokenService;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import java.io.IOException;
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

    private static final String COURSE_LINK = "<a href=http://localhost:8080/api/users/accept-course?confirmToken=%s>click</a>";
    private static final String REGISTR_LINK = "<a href=http://localhost:8080/api/auth/signup/invite?confirmToken=%s>click</a>";

    @Async
    public void sendInvite(InviteRequest recipient, String emailSubject, String emailContent, Principal principal, Long idCourse) {

        User sender = userRepo.findByEmail(principal.getName());

        User user;
        boolean isExists = userRepo.existsByEmail(recipient.getEmail());
        if (isExists) {
            user = userRepo.findByEmail(recipient.getEmail());
        } else {
            user = new User();
            user.setRoles(Collections.singleton(new Role(recipient.getRole())));
            user.setEmail(recipient.getEmail());
        }

//        if (recipient.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {
//            user.setPhoneNumber(recipient.getTelephoneNumber());
//        }

        ConfirmationToken confirmationToken = new ConfirmationToken(user, recipient.getExpirationDateCount(), sender.getId(), recipient.getTypeWayInvited());
        confirmationToken.setIdCourse(idCourse);
        confirmationTokenService.saveConfirmToken(confirmationToken, recipient.getRole());

        String link;
        if (isExists) {
            link = String.format(COURSE_LINK, confirmationToken.getConfirmationToken());
        } else {
            link = String.format(REGISTR_LINK, confirmationToken.getConfirmationToken());
        }


        if (recipient.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {
            sendMessageToEmail(sender.getEmail(), recipient.getEmail(), emailSubject, emailContent, link);
            log.info("send invite link {} in the org to email :: ", link);
        }


//        if (recipient.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {
//
//            String text = String.format("User with telephone number %s invited to be %s in the organization", recipient.getTelephoneNumber(), recipient.getRole());
//
//            sendMessageToTelegram(recipient.getBotToken(), recipient.getChatId(), text, link);
//            log.info("send invite link {} in the org to telegram :: ", link);
//
//        }


    }

//    private void sendMessageToTelegram(String token, String chatID, String text, String link) {
//        TelegramBot bot = new TelegramBot(token);
//
//        SendMessage request = new SendMessage(chatID, text + " <a href=\"" + link + "\">Click here</a>")
//                .parseMode(ParseMode.HTML);
//
//        bot.execute(request, new Callback<SendMessage, SendResponse>() {
//            @Override
//            public void onResponse(SendMessage request, SendResponse response) {}
//
//            @Override
//            public void onFailure(SendMessage request, IOException e) {}
//        });
//    }

    private void sendMessageToEmail(String emailSender, String emailRecipient, String emailSubject,
                                    String emailContent, String link) {
        Mail mail = new Mail();
        mail.setMailFrom(emailSender);
        mail.setMailTo(emailRecipient);
        mail.setMailSubject(emailSubject);


        mail.setMailContent(emailContent + link);
        mailService.sendEmail(mail);
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
            e.printStackTrace();
        }

        assert date1 != null;
        int res = date1.compareTo(date2);
        return res >= 0;
    }


}
