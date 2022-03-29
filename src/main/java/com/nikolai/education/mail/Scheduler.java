package com.nikolai.education.mail;

import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.WorkWithTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final SendMessages sendEmail;
    private final TaskRepo taskRepo;
    private final WorkWithTime workWithTime;
    private final UserRepo userRepo;

    @SneakyThrows
    @Scheduled(cron = "0 0 * * * * ")// once at midnight every day
    @Async
    public void cronSendNotif() {
        String emailSubject = "Task Reminder";

        List<Task> tasks = taskRepo.findTaskByProgress(ProgressTask.IN_PROGRESS);
        Date dateNow = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(workWithTime.dateNow());

        for (Task q : tasks) {
            Date dateFinish = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(q.getDateFinish());
            long duration  = dateFinish.getTime() - dateNow.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

            if (diffInDays <= 3) {
                String mailContent = String.format("you must complete the task \"%s\" before %s", q.getName(), q.getDateFinish());
                User user = userRepo.findByTasks(q);
                sendEmail.sendNotificationAccepted(user.getEmail(), mailContent, emailSubject);
                log.info("send notification about task to {}", user.getEmail());
            }
        }
    }

}
