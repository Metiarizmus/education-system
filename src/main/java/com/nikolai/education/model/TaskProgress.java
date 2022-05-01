package com.nikolai.education.model;

import com.nikolai.education.enums.ProgressTaskEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TaskProgress extends BaseModel {

    @Enumerated(EnumType.STRING)
    private ProgressTaskEnum progressTaskEnum;

    @ManyToOne()
    @JoinColumn(name = "tasks_id", nullable = false)
    private Task task;

    @ManyToOne()
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "TaskProgress{" +
                "progressTaskEnum=" + progressTaskEnum +
                ", task=" + task +
                ", user=" + user +
                '}';
    }
}
