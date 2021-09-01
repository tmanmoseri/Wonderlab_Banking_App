package com.Wonderlab.BankingApp.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 5, max = 26)
    @NotNull
    private String username;
    @Column(nullable = false)

    @Size(min = 5)
    @NotNull
    private String password;
    @Transient
    private String passwordConfirm;

    @Column(nullable = false)
    @Size(min = 5, max = 26)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @Size(min = 5, max = 26)
    @NotNull
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Account> accounts;

    @Transient
    public String getPasswordConfirm() {
        return passwordConfirm;
    }


}
