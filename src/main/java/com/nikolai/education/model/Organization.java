package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nikolai.education.enums.StatusOrgEnum;
import lombok.*;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@AllArgsConstructor
public class Organization extends BaseModel {

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonIgnoreProperties
    private StatusOrgEnum status;

    @Column(name = "avatar", columnDefinition = "LONGBLOB")
    private byte[] avatar;

    @Column(name = "date_created", nullable = false)
    private String dateCreated;

    @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "users_org",
            joinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")}
    )
    private Set<User> users;

    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL)
    private Set<Course> courses;

    public Organization() {
        dateCreated = dateCreated();
    }

    public Organization(String name, String description, StatusOrgEnum status) {
        this.name = name;
        this.description = description;
        this.status = status;
        dateCreated = dateCreated();
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    @JsonIgnore
    public Set<Course> getCourses() {
        return courses;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name +
                '}';
    }
}
