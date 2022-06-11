package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nikolai.education.enums.StatusCourseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course extends BaseModel {

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "description", length = 400, nullable = false)
    private String description;

    @Column(name = "date_creat", nullable = false)
    private String dateCreat;

    @Column(name = "plan", length = 400, nullable = false)
    private String plan;

    @ManyToOne()
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "task_courses",
            joinColumns = {@JoinColumn(name = "courses_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tasks_id", referencedColumnName = "id")}
    )
    private Set<Task> tasks;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "users_courses",
            joinColumns = {@JoinColumn(name = "courses_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")}
    )
    private Set<User> users;

    private Long creatorId;

    public Course() {
       dateCreat = dateCreated();
    }

    public Course(String name, String description, String plan) {
        this.name = name;
        this.description = description;
        this.plan = plan;
        dateCreat = dateCreated();
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateCreat='" + dateCreat + '\'' +
                ", plan='" + plan + '\'' +
                ", creatorId=" + creatorId +
                '}';
    }
}
