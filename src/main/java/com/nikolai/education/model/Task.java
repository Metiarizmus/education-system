package com.nikolai.education.model;

import com.nikolai.education.enums.ProgressTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseModel {

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "text", length = 400, nullable = false)
    private String text;

    @Column(name = "description", length = 400, nullable = false)
    private String description;

    @Column(name = "date_created", nullable = false)
    private String dateCreated;

    @Column(name = "date_start")
    private String dateStart;

    @Column(name = "date_finish")
    private String dateFinish;

    @ManyToOne()
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "progress")
    @Enumerated(EnumType.STRING)
    private ProgressTask progress;

    @ManyToOne()
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    public Task(String name, String text, String description) {
        this.name = name;
        this.text = text;
        this.description = description;
        dateCreated = dateCreated();
    }
}
