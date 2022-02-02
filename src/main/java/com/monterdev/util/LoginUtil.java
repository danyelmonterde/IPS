package com.monterdev.util;

import com.monterdev.model.User;
import com.monterdev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class LoginUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private User user;

    @Autowired
    private ConfigurableApplicationContext applicationContext;


    public String encryptPassword(String rawPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encPassword = passwordEncoder.encode(rawPassword);
        return encPassword;
    }

    public boolean isAuthenticated (String username,String password,ConfigurableApplicationContext applicationContext){
        this.userRepository = applicationContext.getBean(UserRepository.class);
        user = applicationContext.getBean(User.class);
        encoder = applicationContext.getBean(PasswordEncoder.class);
        User userData = userRepository.findByUsername(username);
        boolean isUserAuthenticated;
        if(!ObjectUtils.isEmpty(userData)){
            isUserAuthenticated = encoder.matches(password,userData.getPassword());
            if(isUserAuthenticated){
                user.setIsAdmin(userData.getIsAdmin());
                user.setUsername(userData.getUsername());
                user.setPassword(userData.getPassword());
                user.setIsSuperAdmin(userData.getIsSuperAdmin());
                user.setId(userData.getId());
            }
        }else{
            isUserAuthenticated = false;
        }
        return isUserAuthenticated;
    }

    public  boolean isPasswordEqual(String currentPassword, String encryptedPassword){
        if(ObjectUtils.isEmpty(encoder)){
            encoder = applicationContext.getBean(PasswordEncoder.class);
        }
        return encoder.matches(currentPassword,encryptedPassword);
    }
}
