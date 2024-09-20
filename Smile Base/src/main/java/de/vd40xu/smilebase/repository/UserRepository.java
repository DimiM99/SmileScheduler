package de.vd40xu.smilebase.repository;

import de.vd40xu.smilebase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u " +
           "FROM User u " +
           "WHERE u.role = de.vd40xu.smilebase.model.emuns.UserRole.RECEPTIONIST " +
           "OR u.role = de.vd40xu.smilebase.model.emuns.UserRole.DOCTOR")
    List<User> findAllWithRoleReceptionistOrDoctor();
}
