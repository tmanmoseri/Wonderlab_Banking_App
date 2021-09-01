package com.Wonderlab.BankingApp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String rolename;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    public Role(String rolename, List<User> users) {
        this.rolename = rolename;
        this.users = users;
    }
    public Role( List<User> users) {
        this.users = users;
    }

    public Role(String rolename) {
        this.rolename = rolename;
    }
}
