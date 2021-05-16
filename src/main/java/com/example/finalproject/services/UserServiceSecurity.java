package com.example.finalproject.services;

import com.example.finalproject.exceptions.RegistrationCodeDoesNotExist;
import com.example.finalproject.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceSecurity  implements UserDetailsService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserDetails loadUserByRegistrationCode(String registrationCode) throws Exception{
        return userRepository.findUserByRegistrationCode(registrationCode).orElseThrow(()-> new RegistrationCodeDoesNotExist(registrationCode));
    }
}
