package com.example.userdata.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private final long id;
    private final String username;
    private final String data;
}
