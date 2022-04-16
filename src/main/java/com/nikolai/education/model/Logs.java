package com.nikolai.education.model;

import com.nikolai.education.enums.UserLogsEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Table
@Entity
public class Logs extends BaseModel implements Serializable {

    @Enumerated(EnumType.STRING)
    private UserLogsEnum logs;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "users_id")
    private User user;

    private String dateCreateLogs;

    public Logs(UserLogsEnum logs, User user) {
        this.logs = logs;
        this.user = user;
        dateCreateLogs = dateCreated();
    }

    public Logs() {
        dateCreateLogs = dateCreated();
    }
}
