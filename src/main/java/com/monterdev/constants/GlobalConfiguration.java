package com.monterdev.constants;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monterdev.configuration.JsonFileConfiguration;
import com.monterdev.exception.IqwdException;
import com.monterdev.model.CustomConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Configuration
public class GlobalConfiguration {

    //Global Configuration
    private static List<CustomConfig> customConfigs;

    //Fixed Application fields
    private static final String homePage;
    private static final String clientName;
    private static final String apiUrl;
    private static final String windowTitle;
    private static final String defaultId;
    public static final String defaultCategoryData; //DON'T FORGET TO CHANGE ON PRODUCTION
    public static final String capturedQrCodeDirectory; //DON'T FORGET TO CHANGE ON PRODUCTION
    public static final String generatedQrCodeDirectory; //DON'T FORGET TO CHANGE ON PRODUCTION
    public static final String clientAcronym;
    public static final String defaultSubCategoryData;
    public static final String customerTypes;
    public static final String risTemplates;

    static {
        homePage = "HOME_PAGE";
        clientName = "CLIENT_NAME";
        apiUrl = "API_URL";
        defaultId = "DEFAULT_ID";
        windowTitle = "WINDOW_TITLE";
        defaultCategoryData = "DEFAULT_CATEGORY_DATA";
        capturedQrCodeDirectory = "CAPTURED_QR_CODE_DIRECTORY";
        generatedQrCodeDirectory = "GENERATED_QR_CODE_DIRECTORY";
        clientAcronym = "CLIENT_ACRONYM";
        defaultSubCategoryData = "DEFAULT_SUB_CATEGORY_DATA";
        customerTypes = "CUSTOMER_TYPES";
        risTemplates = "RIS_TEMPLATES";
    }



    //Custom getters
    public static String getHomePage() {
        return getConfigValue(homePage);
    }

    public static String getClientName() {
        return getConfigValue(clientName);
    }

    public static String getApiUrl() {
        return getConfigValue(apiUrl);
    }

    public static String getWindowTitle(){
        return getConfigValue(windowTitle);
    }

    public static String getClientAcronym(){
        return getConfigValue(clientAcronym);
    }

    public static String getDefaultSubCategoryData(){
        return getConfigValue(defaultSubCategoryData);
    }

    public static String getCustomerTypes() { return getConfigValue(customerTypes);}

    public static String getRisTemplates() { return getConfigValue(risTemplates);}


    //Getting default value when configuration id could not be found
    public static String getDefaultValue(){
        Optional<CustomConfig> optionalValue = customConfigs
                .stream().filter(id -> id.getConfigId().equalsIgnoreCase(defaultId))
                .findFirst();
        return optionalValue.isPresent() ? optionalValue.get().getConfigValue(): null;
    }

    //Getting the actual configuration value based from the configuration id
    public static String getConfigValue(String configId ){
        Optional<CustomConfig> optionalValue = customConfigs
                .stream().filter(id -> id.getConfigId().equalsIgnoreCase(configId))
                .findFirst();
        return optionalValue.isPresent() ? optionalValue.get().getConfigValue(): getDefaultValue();
    }

    //On-startup setting of the Global json configuration
    public void setJsonFileConfiguration(JsonFileConfiguration jsonFileConfiguration) throws IqwdException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            customConfigs = objectMapper.readValue(objectMapper.writeValueAsString(jsonFileConfiguration.getCustomConfigurations()), new TypeReference<List<CustomConfig>>() {});
        } catch (JsonProcessingException e) {
            throw new IqwdException(ErrorConstants.APPLICATION_ERROR_CODE(),ErrorConstants.APPLICATION_ERROR_MESSAGE(), e.getCause());
        }
    }
}
