package com.monterdev.util;

import com.monterdev.repository.UserRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

public class LoginUtil {

    private UserRepository userRepository;

    private PasswordEncoder encoder;

    public String encryptPassword(String rawPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encPassword = passwordEncoder.encode(rawPassword);
        return encPassword;
    }

    public boolean isAuthenticated (String username,String password,ConfigurableApplicationContext applicationContext){
        userRepository = applicationContext.getBean(UserRepository.class);
        encoder = applicationContext.getBean(PasswordEncoder.class);
        String encryptedPassword = userRepository.findPasswordByUsername(username);
        boolean isUserAuthenticated;
        if(!ObjectUtils.isEmpty(encryptedPassword)){
            isUserAuthenticated = encoder.matches(password,encryptedPassword);
            encryptedPassword = null;
        }else{
            isUserAuthenticated = false;
            encryptedPassword = null;
        }
        return isUserAuthenticated;
    }
}
