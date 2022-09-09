package com.example.userdata.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrivateUserDto implements Serializable {
    private final long id;
    private final String username;
    private final String publicData;
    private final String privateData;
}
