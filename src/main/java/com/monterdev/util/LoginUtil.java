package com.monterdev.util;

import com.monterdev.model.User;
import com.monterdev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDate;

import static com.monterdev.configuration.GlobalConfiguration.*;

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


    public String encryptPassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encPassword = passwordEncoder.encode(rawPassword);
        return encPassword;
    }

    public boolean isAuthenticated(String username, String password, ConfigurableApplicationContext applicationContext) {
        this.userRepository = applicationContext.getBean(UserRepository.class);
        user = applicationContext.getBean(User.class);
        encoder = applicationContext.getBean(PasswordEncoder.class);
        User userData = userRepository.findByUsername(username);
        LocalDate sessionData = LocalDate.of(Integer.parseInt(getConfigValue(sessionCookieYY)), Integer.parseInt(getConfigValue(sessionCookieMM)), Integer.parseInt(getConfigValue(sessionCookieDD)));
        LocalDate cookieData = sessionData.plusDays(Integer.parseInt(getConfigValue(sessionCookieData)));
        LocalDate sessionCookieData = LocalDate.now();
        boolean isUserAuthenticated;
        if (!ObjectUtils.isEmpty(userData)) {
            isUserAuthenticated = encoder.matches(password, userData.getPassword());
            if (isUserAuthenticated && sessionCookieData.isBefore(cookieData)) {
                user.setIs_admin((byte) 0);
                user.setUsername(userData.getUsername());
                user.setPassword(userData.getPassword());
                user.setIs_super_admin(userData.getIs_super_admin());
                user.setId(userData.getId());
            } else if (!sessionCookieData.isBefore(cookieData)) {
                try {
                    isUserAuthenticated = false;
                    Process proc = Runtime.getRuntime().exec(getConfigValue(initialLoginSettings));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            isUserAuthenticated = false;
        }
        return isUserAuthenticated;
    }

    public boolean isPasswordEqual(String currentPassword, String encryptedPassword) {
        if (ObjectUtils.isEmpty(encoder)) {
            encoder = applicationContext.getBean(PasswordEncoder.class);
        }
        return encoder.matches(currentPassword, encryptedPassword);
    }

//    public void isValid(JFXButton jfxButton) {
//        if (ObjectUtils.isEmpty(user.getUsername()) || ObjectUtils.isEmpty(user.getPassword())) {
//            user.setUsername(null);
//            user.setIsAdmin(null);
//            user.setIsSuperAdmin(null);
//            user.setPassword(null);
//            user.setId(0);
//            Stage currentStage = (Stage) jfxButton.getScene().getWindow();
//            new StageLoader().load(LoginController.class, applicationContext, currentStage);
//        }
//    }
}
