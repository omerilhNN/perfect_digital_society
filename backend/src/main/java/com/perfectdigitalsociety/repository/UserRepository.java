package com.perfectdigitalsociety.repository;

import com.perfectdigitalsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query methods as specified in documentation
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();
    
    List<User> findByRole(User.Role role);
    
    // Additional custom queries
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.freedomScore >= :minScore")
    List<User> findUsersByMinimumFreedomScore(@Param("minScore") Integer minScore);
    
    @Query("SELECT u FROM User u WHERE u.securityScore >= :minScore")
    List<User> findUsersByMinimumSecurityScore(@Param("minScore") Integer minScore);
    
    @Query("SELECT u FROM User u WHERE u.reputationScore >= :minReputation ORDER BY u.reputationScore DESC")
    List<User> findUsersByMinimumReputation(@Param("minReputation") Integer minReputation);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since")
    List<User> findUsersLoggedInSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    @Query("SELECT AVG(u.freedomScore) FROM User u WHERE u.isActive = true")
    Double getAverageFreedomScore();
    
    @Query("SELECT AVG(u.securityScore) FROM User u WHERE u.isActive = true")
    Double getAverageSecurityScore();
    
    @Query("SELECT AVG(u.reputationScore) FROM User u WHERE u.isActive = true")
    Double getAverageReputationScore();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}