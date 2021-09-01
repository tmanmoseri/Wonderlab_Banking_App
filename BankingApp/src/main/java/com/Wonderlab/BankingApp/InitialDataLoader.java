package com.Wonderlab.BankingApp;


import com.Wonderlab.BankingApp.model.Role;
import com.Wonderlab.BankingApp.model.User;
import com.Wonderlab.BankingApp.repository.AccountsRepo;
import com.Wonderlab.BankingApp.repository.RoleRepo;
import com.Wonderlab.BankingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;


@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountsRepo accountsRepo;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup)
            return;

        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        Role adminRole = roleRepo.findByRolename("ROLE_ADMIN");
        Role userRole = roleRepo.findByRolename("ROLE_USER");

        User user = new User();
        user.setFirstName("Admin1");
        user.setLastName("Last1");
        user.setUsername("admin@gmail.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRoles(Arrays.asList(adminRole, userRole));

        User user2 = userRepo.findByUsername("admin@gmail.com");
        if (user2 == null)
            userRepo.save(user);
        alreadySetup = true;

    }
    @Transactional
    private Role createRoleIfNotFound(String rolename) {
        Role role = roleRepo.findByRolename(rolename);
        if (role == null) {
            role = new Role(rolename);
            roleRepo.save(role);
        }
        return role;
    }
}
