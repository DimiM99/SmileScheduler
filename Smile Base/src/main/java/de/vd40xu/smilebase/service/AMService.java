package de.vd40xu.smilebase.service;

import de.vd40xu.smilebase.dto.UserDTO;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.UserRole;
import de.vd40xu.smilebase.repository.UserRepository;
import de.vd40xu.smilebase.service.interfaces.IAccountManagement;
import de.vd40xu.smilebase.service.interfaces.IUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AMService implements IAccountManagement {

    private final IUserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AMService(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void isAuthorized() throws IllegalAccessException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.loadUserFromPrincipal(principal);
        if (user.isEmpty() || user.get().getRole() != UserRole.ADMIN) {
            throw new IllegalAccessException("You are not authorized to perform this action");
        }
    }

    @Override
    public List<User> getAllUsers() throws IllegalAccessException {
        isAuthorized();
        return userRepository.findAllWithRoleReceptionistOrDoctor();
    }

    @Override
    public User createOrUpdateUser(UserDTO user, Boolean create) throws IllegalAccessException {
        isAuthorized();
        return Boolean.TRUE.equals(create) ? handleCreateUser(user) : handleUpdateUser(user);
    }

    private User handleCreateUser(UserDTO user) {
        User userToCreate = User.builder()
                           .username(user.getUsername())
                           .password(passwordEncoder.encode(user.getPassword()))
                           .name(user.getName())
                           .email(user.getEmail())
                           .role(user.getRole())
                           .active(true)
                           .build();
        return userRepository.save(userToCreate);
    }

    private User handleUpdateUser(UserDTO user) {
        Optional<User> currentUser = userRepository.findByUsername(user.getUsername());
        if (currentUser.isEmpty()) {
            return null;
        }
        User userToUpdate = currentUser.get();
        if (user.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userRepository.save(userToUpdate);
    }

    @Override
    public User deleteUser(UserDTO user) throws IllegalAccessException {
        isAuthorized();
        Optional<User> userToDelete = userRepository.findByUsername(user.getUsername());
        if (userToDelete.isEmpty()) {
            return null;
        }
        userRepository.delete(userToDelete.get());
        return userToDelete.get();
    }
}
