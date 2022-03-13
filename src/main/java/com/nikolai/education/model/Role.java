package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.enums.TypeRoles;
import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
@Getter
@Setter
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private TypeRoles nameRoles;


    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;

    public Role(TypeRoles nameRoles) {
        this.nameRoles = nameRoles;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
