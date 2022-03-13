package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.enums.StatusOrg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseModel {

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private StatusOrg status;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_org",
            joinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")}
    )
    private Set<User> users;

    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL)
    private Set<Course> courses;

    public Organization(String name, String description, StatusOrg status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    @JsonIgnore
    public Set<Course> getCourses() {
        return courses;
    }
}
