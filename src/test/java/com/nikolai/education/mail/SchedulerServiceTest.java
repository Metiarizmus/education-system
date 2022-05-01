package com.nikolai.education.mail;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.TaskProgress;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.WorkWithTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SchedulerServiceTest {

    @InjectMocks
    private SchedulerService scheduler;
    @Mock
    private UserRepo userRepo;
    @Mock
    private WorkWithTime workWithTime;
    @Mock
    private SendMessages sendMessages;
    @Mock
    private TaskRepo taskRepo;

    @Test
    void cronSendNotif() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        List<Task> testList = new ArrayList<>();
        Task task1 = new Task("test task", "u must choose correct answer", "its ur first task");
        task1.setDateFinish(formatter.format(calendar.getTime()));

        Task task2 = new Task("second task", "u must choose correct answer", "its ur last task");
        task2.setDateFinish(formatter.format(calendar.getTime()));

        testList.add(task1);
        testList.add(task2);

        User testUser = new User("Nikolai", "Nagornyh", "test@mail.ru", "123123", "+375444794639");

        when(taskRepo.findAllByProgressTasks_progressTaskEnum(any(ProgressTaskEnum.class))).thenReturn(testList);
        when(userRepo.findByProgressTasks_task(any(Task.class))).thenReturn(testUser);
        when(workWithTime.dateNow()).thenReturn(formatter.format(calendar.getTime()));

        scheduler.cronSendNotif();

    }
}