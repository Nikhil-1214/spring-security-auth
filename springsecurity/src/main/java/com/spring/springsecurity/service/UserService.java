package com.spring.springsecurity.service;

import com.spring.springsecurity.model.User;
import com.spring.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class UserService {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
public User register(String name,String password,Long salary){
if(userRepository.existsByUsername(name)){
throw new IllegalArgumentException("Username already exist"+name);}
String hashpassword=passwordEncoder.encode(password);
User user=User.builder()
        .username(name)
        .password(hashpassword)
        .salary(salary)
        .role("Role_User")
        .build();
return userRepository.save(user);
}
}
