package de.vd40xu.smilebase.service.interfaces;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;

import java.util.List;

public interface IAccountManagement {
    List<User> getAllUsers() throws IllegalAccessException;
    User createOrUpdateUser(UserDTO userDTO, Boolean create) throws IllegalAccessException;
    User deleteUser(UserDTO userDTO) throws IllegalAccessException;
}
