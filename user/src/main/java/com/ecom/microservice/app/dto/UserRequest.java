package com.ecom.microservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phone;
    private AddressDto address;
}
