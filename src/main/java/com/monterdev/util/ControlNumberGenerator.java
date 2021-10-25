package com.monterdev.util;

import com.monterdev.constants.GlobalConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ControlNumberGenerator {

    public String generate(){
            return GlobalConfiguration.getClientAcronym()+"-"+ getYear() +"-"+getMonth()+"-"+ RandomStringUtils.randomAlphanumeric(8);
    }

    private int getYear(){
         LocalDateTime now = LocalDateTime.now();
         return now.getYear();
    }

    private int getMonth(){
        LocalDateTime now = LocalDateTime.now();
        return now.getMonthValue();
    }
}
