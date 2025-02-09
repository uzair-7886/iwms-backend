package com.example.iwms.iwms.service;

import com.example.iwms.iwms.entity.User;
import com.example.iwms.iwms.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
// import com.example.iwms.iwms.entity.User;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // First try to find the user by email or phone.
    Optional<User> userOptional = userRepository.findByEmailOrPhone(username);
    
    // If not found, try to parse the username as a userId (in case the user registered via face only).
    if (!userOptional.isPresent()) {
        try {
            Long userId = Long.parseLong(username);
            userOptional = userRepository.findById(userId);
        } catch (NumberFormatException e) {
            // If parsing fails, leave userOptional empty.
        }
    }
    
    User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    
    // Use emailOrPhone if available; otherwise, use the userId as the username.
    String loginUsername = (user.getEmailOrPhone() != null && !user.getEmailOrPhone().isEmpty())
            ? user.getEmailOrPhone()
            : user.getUserId().toString();
    
    // In case the user registered only with a face, there might be no password (or it could be an empty string).
    // Adjust this if needed (e.g. by setting a dummy password or handling it in your security configuration).
    String password = (user.getPassword() != null) ? user.getPassword() : "";
    
    return org.springframework.security.core.userdetails.User.builder()
            .username(loginUsername)
            .password(password)
            .roles(user.getRole().toUpperCase())
            .build();
}


}

