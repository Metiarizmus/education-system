package com.nikolai.education.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @Column(name = "date_start", nullable = false)
    private String dateStart;

    @Column(name = "date_finish", nullable = false)
    private String dateFinish;

    @ManyToOne()
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "progress")
    @Enumerated(EnumType.STRING)
    private Progress progress;

    public enum Progress {
        IN_PROGRESS, DONE, NOT_START
    }
}
