package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.enums.TypeRolesEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
@Getter
@Setter
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private TypeRolesEnum nameRoles;


    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;

    public Role(TypeRolesEnum nameRoles) {
        this.nameRoles = nameRoles;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }


}
