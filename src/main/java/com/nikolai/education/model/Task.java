package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

    private Integer expirationCountHours;

    @ManyToMany(mappedBy = "tasks", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Course> courses;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<TaskProgress> progressTasks;

    public Task() {
        dateCreated = dateCreated();
    }

    public Task(String name, String text, String description) {
        this.name = name;
        this.text = text;
        this.description = description;
        dateCreated = dateCreated();
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateFinish='" + dateFinish + '\'' +
                ", expirationCountHours=" + expirationCountHours +
                ", progressTasks=" + progressTasks +
                '}';
    }
}
