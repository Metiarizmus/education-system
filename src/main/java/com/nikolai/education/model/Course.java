package com.nikolai.education.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "course")
    private Set<Task> tasks;

    @ManyToOne()
    @JoinColumn(name = "users_id", nullable = false)
    private User user;
}
