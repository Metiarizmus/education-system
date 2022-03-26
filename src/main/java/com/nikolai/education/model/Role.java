package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.enums.TypeRoles;
import lombok.*;

import javax.persistence.*;
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


}
