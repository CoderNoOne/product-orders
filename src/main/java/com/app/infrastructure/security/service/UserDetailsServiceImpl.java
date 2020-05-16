package com.app.infrastructure.security.service;

import com.app.domain.entity.Manager;
import com.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new SecurityException("cannot find user with username " + username));

        return new User(
                user.getUsername(),
                user.getPassword(),
                !(user instanceof Manager manager) || manager.getEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(String.format("ROLE_%s", user.getRole().getName())))
                );
    }
}
