package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskProgress extends BaseModel {

    @Enumerated(EnumType.STRING)
    private ProgressTaskEnum progressTaskEnum;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tasks_id", nullable = false )
    @JsonIgnore
    private Task task;

    @ManyToOne()
    @JoinColumn(name = "users_id", nullable = false)
    @JsonIgnore
    private User user;


    public TaskProgress(ProgressTaskEnum progressTaskEnum, Task task) {
        this.progressTaskEnum = progressTaskEnum;
        this.task = task;
    }

    @Override
    public String toString() {
        return "TaskProgress{" +
                "progressTaskEnum=" + progressTaskEnum +
                ", user=" + user +
                '}';
    }
}
