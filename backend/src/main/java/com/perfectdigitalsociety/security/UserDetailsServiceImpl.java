package com.perfectdigitalsociety.security;

import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user by username or email: {}", usernameOrEmail);
        
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.error("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });
        
        if (!user.getIsActive()) {
            log.error("User account is deactivated: {}", usernameOrEmail);
            throw new UserNotFoundException("User account is deactivated: " + usernameOrEmail);
        }
        
        List<GrantedAuthority> authorities = getAuthorities(user);
        
        log.debug("User loaded successfully: {} with authorities: {}", usernameOrEmail, authorities);
        
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getIsActive(),
                authorities
        );
    }
    
    private List<GrantedAuthority> getAuthorities(User user) {
        String roleName = "ROLE_" + user.getRole().toString();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}