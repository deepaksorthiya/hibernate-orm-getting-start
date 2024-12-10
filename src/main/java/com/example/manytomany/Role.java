package com.example.manytomany;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(
        name = "ROLES",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_ROLES_ROLE_NAME", columnNames = {"roleName"})
        })
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;

    private String roleName;

    private String roleDesc;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private Set<AppUser> appUsers = new HashSet<>();

    public Role(String roleName, String roleUserDescription) {
        this.roleName = roleName;
        this.roleDesc = roleUserDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roleId);
    }
}
