package com.example.examsdata.service;

import com.example.examsdata.dto.UserDto;
import com.example.examsdata.models.User;
import com.example.examsdata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> unfilteredList = userRepository.findAll();
        List<UserDto> filteredList = new ArrayList<>();

        // Filters users so that the private data cannot be seen in listing
        for (User user : unfilteredList) {
            filteredList.add(new UserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getPublicData()
            ));
        }

        return filteredList;
    }

    public void createUser(User user) throws NoSuchAlgorithmException {
        user.setPasswordHash(sha256(user.getPasswordHash()));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean login(String username, String password) throws NoSuchAlgorithmException {
        if (getUserByUsername(username).isEmpty()) {
            return false;
        } else {
            return userRepository.findByUsername(username).get().getPasswordHash().equals(sha256(password));
        }
    }

    public String sha256(String str) throws NoSuchAlgorithmException {

        MessageDigest dig = MessageDigest.getInstance("SHA-256");
        BigInteger num = new BigInteger(1, dig.digest(str.getBytes(StandardCharsets.UTF_8)));

        StringBuilder output = new StringBuilder(num.toString());

        while (output.length() < 64) {
            output.insert(0, "0");
        }

        return output.toString();
    }

    public Optional<User> getUserByUsername(String username) {
        // Returns data with secret
        return userRepository.findByUsername(username);
    }

    public UserDto getUserByUsernameWithoutPassword(String username) {
        User unfilteredData = getUserByUsername(username).get();

        // Returns filtered data without secret
        return new UserDto(
                unfilteredData.getId(),
                unfilteredData.getUsername(),
                unfilteredData.getPublicData()
        );
    }

}
