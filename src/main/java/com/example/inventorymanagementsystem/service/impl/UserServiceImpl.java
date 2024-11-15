package com.example.inventorymanagementsystem.service.impl;

import com.example.inventorymanagementsystem.dto.UpdateUserDTO;
import com.example.inventorymanagementsystem.entity.Role;
import com.example.inventorymanagementsystem.exceptions.UserExistsException;
import com.example.inventorymanagementsystem.entity.User;
import com.example.inventorymanagementsystem.repository.UserRepository;
import com.example.inventorymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;


    @Override
    public User createUser(User user, Role userRole) {
        Optional<User> optionalUser = userRepository.findUserByUsername(user.getUsername());
        if(optionalUser.isPresent()){
            throw new UserExistsException("User Already Exists.");
        }

        //Encrypting the password
        BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = pwEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        //Adding the Role to user's Role
        user.getRoles().add(userRole);

        //Saving the new user to the repository
        return userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }
        throw new UsernameNotFoundException("User Not Found.");
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Integer getUserId(String username){
        Optional<Integer> optionalUserId = userRepository.getUserId(username);
        if(optionalUserId.isPresent()){
            return optionalUserId.get();
        }
        throw new UsernameNotFoundException("User Not Found.");
    }

    @Override
    public User updateUser(UpdateUserDTO updateUserDTO) {

        //Getting the existing details of the user from the DB
        User existingUser = getUser(updateUserDTO.getUsername());

        //Updating First and Last Name with the newly provided one
        existingUser.setFirstName(updateUserDTO.getFirstName());
        existingUser.setLastName(updateUserDTO.getLastName());

        return userRepository.save(existingUser);
    }

    @Override
    public User deleteUser(User user) {
        user.setUsername(user.getUsername() + "_deleted");
        user.setPassword(null);
        user.setFirstName(null);
        user.setLastName(null);
        return userRepository.save(user);
//        userRepository.deleteUserByUsername(username);
    }

}
