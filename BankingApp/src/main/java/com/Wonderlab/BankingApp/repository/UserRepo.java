package com.Wonderlab.BankingApp.repository;

import com.Wonderlab.BankingApp.model.User;
import org.springframework.data.repository.CrudRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findAllById(Long id);
    List<User>findAll();




}
