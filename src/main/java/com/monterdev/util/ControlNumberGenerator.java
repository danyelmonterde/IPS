package com.monterdev.util;

import com.monterdev.configuration.GlobalConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ControlNumberGenerator {

    public String generate(){
            return GlobalConfiguration.getClientAcronym()+"-"+ getYear() +"-"+getMonth()+"-"+ generateControlNumber();
    }

    private int getYear(){
         LocalDateTime now = LocalDateTime.now();
         return now.getYear();
    }

    private int getMonth(){
        LocalDateTime now = LocalDateTime.now();
        return now.getMonthValue();
    }

    public static String generateControlNumber(){
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
