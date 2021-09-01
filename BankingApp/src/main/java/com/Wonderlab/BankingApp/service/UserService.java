package com.Wonderlab.BankingApp.service;

import com.Wonderlab.BankingApp.controller.AccountsController;
import com.Wonderlab.BankingApp.model.User;
import com.Wonderlab.BankingApp.repository.RoleRepo;
import com.Wonderlab.BankingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AccountsController accountsController;

    @Autowired
    private SecurityService securityService;

    public void save(User user){

        String NewUsername = user.getUsername();

        Optional<User> user2 = Optional.ofNullable(userRepo.findByUsername(NewUsername));
        if ( !user2.isPresent()) {
            user.setPassword(user.getPassword());
            user.setRoles(roleRepo.findAll());
            userRepo.save(user);
        }else  throw  new RuntimeException("The username is already registered. Please use login option");

    }
    public User getUserById(Long id){
        Optional<User> optional = userRepo.findAllById(id);
        User user = null;
        if (optional.isPresent()){
            //user=optional.get();
        }else throw  new RuntimeException("User with the that id is not found");
        return user;
    }
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }
    public void findByLogedinUser(Model model){
        User currentUser = currentUser();
        model.addAttribute("id",currentUser.getId());
        model.addAttribute("username",currentUser.getUsername());
        model.addAttribute("firstName",currentUser.getFirstName());
        model.addAttribute("lastName",currentUser.getLastName());

    }
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName());
    }
 public void updateUserAccount(User user){

     Long UserId = currentUser().getId();

             Optional<User> userAccount = userRepo.findAllById(UserId);

     if (userAccount.isPresent()){

       String newUserName =  user.getUsername();
       String newFirstName =  user.getFirstName();
       String newLastName = user.getLastName();

         user.setUsername(newUserName);
         user.setFirstName(newFirstName);
         user.setLastName(newLastName);
         userRepo.save(user);

     }else throw  new RuntimeException("User with that id is not found");
 }
}
