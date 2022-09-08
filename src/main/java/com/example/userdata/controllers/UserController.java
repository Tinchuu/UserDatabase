package com.example.userdata.controllers;

import com.example.userdata.models.User;
import com.example.userdata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity getUserByUsernameWithoutPassword(@PathVariable String username) {
        return new ResponseEntity<>(userService.getUserByUsernameWithoutPassword(username), HttpStatus.OK);
    }

    @GetMapping("/user/{username}/{password}")
    public ResponseEntity getUserByUsername(@PathVariable String username, @PathVariable String password) throws NoSuchAlgorithmException {
        if (userService.getUserByUsername(username).isPresent()) {
            if (userService.login(username, password)) {
                return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/user")
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            if (userService.getUserByUsername(user.getUsername()).isEmpty()) {
                userService.createUser(user);
            } else {
                return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user/{username}/{password}")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            if (userService.getUserByUsername(user.getUsername()).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                userService.changeData(user);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
