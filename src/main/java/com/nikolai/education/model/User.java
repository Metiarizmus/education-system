package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;

@Table(name = "users")
@Entity
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseModel {

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

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Organization> org;

    @OneToMany(mappedBy = "user")
    private Set<Course> courses;

    private boolean isInvite = false;

    public User(String firstName, String lastName, String email, String password, String phoneNumber) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        dateRegistr = formatter.format(calendar.getTime());

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        dateRegistr = formatter.format(calendar.getTime());
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
}
