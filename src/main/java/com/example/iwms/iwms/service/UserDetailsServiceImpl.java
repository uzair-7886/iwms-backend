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
        // 1) try email/phone
        Optional<User> u = userRepository.findByEmailOrPhone(username);

        // 2) if that fails, see if it's just a numeric id
        if (u.isEmpty()) {
            try {
                Long id = Long.parseLong(username);
                u = userRepository.findById(id);
            } catch (NumberFormatException ignored) { }
        }

        User user = u.orElseThrow(() ->
            new UsernameNotFoundException("User not found: " + username)
        );

        // **IMPORTANT**: return a UserDetails whose .getUsername() == whatever you passed in
        // so that your JWT-util and your filter stay in sync.
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword() != null ? user.getPassword() : "")
                .roles(user.getRole().toUpperCase())
                .build();
    }
}
