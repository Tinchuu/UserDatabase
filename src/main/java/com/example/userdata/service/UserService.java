package com.example.userdata.service;

import com.example.userdata.dto.PrivateUserDto;
import com.example.userdata.dto.PublicUserDto;
import com.example.userdata.models.User;
import com.example.userdata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "userCache")
    public List<PublicUserDto> getAllUsers() {
        List<User> unfilteredList = userRepository.findAll();
        List<PublicUserDto> filteredList = new ArrayList<>();

        // Filters users so that the private data cannot be seen in listing
        for (User user : unfilteredList) {
            filteredList.add(new PublicUserDto(
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

    @Transactional
    @CacheEvict(value = "dataCache", key = "#username")
    public void changeData(User user) {
        User current = userRepository.findByUsername(user.getUsername()).get();
        current.setPublicData(user.getPublicData());
        current.setSecretData(user.getSecretData());
    }

    public boolean login(String username, String password) throws NoSuchAlgorithmException {
        if (userRepository.findByUsername(username).isEmpty()) {
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

    public boolean doesUserExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Cacheable(value = "dataCache", key = "#username")
    public PrivateUserDto getUserByUsername(String username) {
        User unfilteredData = userRepository.findByUsername(username).get();

        return new PrivateUserDto(
                unfilteredData.getId(),
                unfilteredData.getUsername(),
                unfilteredData.getPublicData(),
                unfilteredData.getSecretData()
        );
    }

    public PublicUserDto getUserByUsernameWithoutPassword(String username) {
        User unfilteredData = userRepository.findByUsername(username).get();

        // Returns filtered data without secret
        return new PublicUserDto(
                unfilteredData.getId(),
                unfilteredData.getUsername(),
                unfilteredData.getPublicData()
        );
    }

}
