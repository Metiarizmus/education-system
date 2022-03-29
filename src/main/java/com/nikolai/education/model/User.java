package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Table(name = "users")
@Entity
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseModel implements Serializable {

    @Column(name = "first_name", length = 15)
    private String firstName;

    @Column(name = "last_name", length = 15)
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", length = 90)
    @JsonIgnore
    private String password;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "date_registr")
    private String dateRegistr;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "roles_id", referencedColumnName = "id")}
    )
    private Set<Role> roles;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Organization> org;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Course> courses;

    @OneToMany(mappedBy = "user")
    private Set<Task> tasks;

    private boolean isEnable = false;

    public User(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        dateRegistr = dateCreated();
    }

    public User() {
        dateRegistr = dateCreated();
    }

    @JsonIgnore
    public Set<Role> getRoles() {
        return roles;
    }
    @JsonIgnore
    public Set<Organization> getOrg() {
        return org;
    }
    @JsonIgnore
    public Set<Course> getCourses() {
        return courses;
    }
    @JsonIgnore
    public Set<Task> getTasks() {
        return tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateRegistr='" + dateRegistr + '\'' +
                ", roles=" + roles +
                ", org=" + org +
                ", courses=" + courses +
                ", isEnable=" + isEnable +
                '}';
    }
}
