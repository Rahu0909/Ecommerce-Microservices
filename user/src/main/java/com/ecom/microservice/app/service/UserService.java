package com.ecom.microservice.app.service;

import com.ecom.microservice.app.dto.AddressDto;
import com.ecom.microservice.app.dto.UserRequest;
import com.ecom.microservice.app.dto.UserResponse;
import com.ecom.microservice.app.model.Address;
import com.ecom.microservice.app.model.User;
import com.ecom.microservice.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeyCloakAdminService keyCloakAdminService;

    public List<UserResponse> fetchALlList() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public void createUsers(UserRequest userRequest) {
        String token = keyCloakAdminService.getAdminAccessToken();
        String keycloakUserId = keyCloakAdminService.createUser(token, userRequest);
        User user = new User();
        updateUserFromRequest(user,
                userRequest);
        user.setKeycloakId(keycloakUserId);
        keyCloakAdminService.assignRealmRoleToUser(userRequest.getUserName(),
                "USER", keycloakUserId);
        userRepository.save(user);
    }

    public Optional<UserResponse> getUser(String id) {
        return userRepository.findById(String.valueOf(id))
                .map(this::mapToUserResponse);
    }

    public boolean updateUser(String id,
                              UserRequest updatedUserRequest) {
        return userRepository.findById(String.valueOf(id))
                .stream()
                .filter(user -> user.getId()
                        .equals(id))
                .findFirst()
                .map(existingUser -> {
                    updateUserFromRequest(existingUser,
                            updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    private void updateUserFromRequest(User user,
                                       UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setStreet(userRequest.getAddress()
                    .getStreet());
            address.setState(userRequest.getAddress()
                    .getState());
            address.setCity(userRequest.getAddress()
                    .getCity());
            address.setCountry(userRequest.getAddress()
                    .getCountry());
            address.setZipcode(userRequest.getAddress()
                    .getZipcode());
            user.setAddress(address);
        }
    }


    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setKeyCloakId(user.getKeycloakId());
        userResponse.setId(String.valueOf(user.getId()));
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setUserRole(user.getRole());
        if (user.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress()
                    .getStreet());
            addressDto.setCity(user.getAddress()
                    .getCity());
            addressDto.setState(user.getAddress()
                    .getState());
            addressDto.setCountry(user.getAddress()
                    .getCountry());
            addressDto.setZipcode(user.getAddress()
                    .getZipcode());
            userResponse.setAddress(addressDto);
        }
        return userResponse;
    }
}
