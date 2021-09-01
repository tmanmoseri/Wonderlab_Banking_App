package com.Wonderlab.BankingApp.repository;

import com.Wonderlab.BankingApp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepo extends CrudRepository<Role, Long> {
    @Override
    List<Role> findAll();
    Role findByRolename(String rolename);
}
