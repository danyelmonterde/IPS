package com.monterdev.configuration;


import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ItemsUIConfiguration extends GlobalConfiguration {

    //SIDEBARCATEGORY_FUNCTION_X..

    public static final String ITEMS_ADD_ITEM_BUTTON_NAME;
    public static final String ITEMS_IMPORT_BUTTON_NAME;
    public static final String ITEMS_EXPORT_BUTTON_NAME;
    public static final String ITEMS_SETTINGS_BUTTON_NAME;
    public static final String ITEMS_SEARCH_BUTTON_NAME;
    public static final String ITEMS_BUTTON_CSS_CLASSES;
    public static final String ITEMS_ADD_ITEM_CSS_CLASS;
    public static final String ITEMS_LABEL_CATEGORY_NAME;
    public static final String ITEMS_LABEL_STOCKALERT_NAME;
    public static final String ITEMS_DROP_DOWN_CLASS;
    public static final String ITEMS_VBOX_CLASSES;
    public static final String ITEMS_DATATABLE_HEADER;
    public static final String ITEMS_DATATABLE_FOOTER;
    public static final String ITEMS_DATABLE_FOOTER_LABELS;
    public static final String ITEMS_TOP_HBOX_SPACING;
    public static final String ITEMS_PREV_BUTTON_NAME;
    public static final String ITEMS_NEXT_BUTTON_NAME;
    public static final String ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL;
    public static final String ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL;
    public static final String ITEMS_STOCK_ALERTS;
    public static final String ITEMS_SETTINGS_CONTEXT_MENU;
    public static final String ITEMS_ROWS_PER_PAGE;
    public static final String EDIT_BUTTON;
    public static final String DELETE_BUTTON;
    public static final String ITEMS_STOCK_ADJUSTMENT_REASON;


    static {
        ITEMS_ADD_ITEM_BUTTON_NAME = "ITEMS_ADD_ITEM_BUTTON_NAME";
        ITEMS_IMPORT_BUTTON_NAME = "ITEMS_IMPORT_BUTTON_NAME";
        ITEMS_EXPORT_BUTTON_NAME = "ITEMS_EXPORT_BUTTON_NAME";
        ITEMS_SETTINGS_BUTTON_NAME = "ITEMS_SETTINGS_BUTTON_NAME";
        ITEMS_SEARCH_BUTTON_NAME = "ITEMS_SEARCH_BUTTON_NAME";
        ITEMS_BUTTON_CSS_CLASSES = "ITEMS_BUTTON_CSS_CLASSES";
        ITEMS_ADD_ITEM_CSS_CLASS = "ITEMS_ADD_ITEM_CSS_CLASS";
        ITEMS_LABEL_CATEGORY_NAME = "ITEMS_LABEL_CATEGORY_NAME";
        ITEMS_LABEL_STOCKALERT_NAME = "ITEMS_LABEL_STOCKALERT_NAME";
        ITEMS_DROP_DOWN_CLASS = "ITEMS_DROP_DOWN_CLASS";
        ITEMS_VBOX_CLASSES ="ITEMS_VBOX_CLASSES";
        ITEMS_DATATABLE_HEADER ="ITEMS_DATATABLE_HEADER";
        ITEMS_DATATABLE_FOOTER ="ITEMS_DATATABLE_FOOTER";
        ITEMS_DATABLE_FOOTER_LABELS ="ITEMS_DATABLE_FOOTER_LABELS";
        ITEMS_TOP_HBOX_SPACING = "ITEMS_TOP_HBOX_SPACING";
        ITEMS_PREV_BUTTON_NAME="ITEMS_PREV_BUTTON_NAME";
        ITEMS_NEXT_BUTTON_NAME="ITEMS_NEXT_BUTTON_NAME";
        ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL="ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL";
        ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL="ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL";
        ITEMS_STOCK_ALERTS="ITEMS_STOCK_ALERTS";
        ITEMS_SETTINGS_CONTEXT_MENU="ITEMS_SETTINGS_CONTEXT_MENU";
        ITEMS_ROWS_PER_PAGE="ITEMS_ROWS_PER_PAGE";
        DELETE_BUTTON="DELETE_BUTTON";
        EDIT_BUTTON="EDIT_BUTTON";
        ITEMS_STOCK_ADJUSTMENT_REASON="ITEMS_STOCK_ADJUSTMENT_REASON";
    }

    //Custom getters/////////////////////////////////////

    public static String getAddItemsButtonName() {
        return getConfigValue(ITEMS_ADD_ITEM_BUTTON_NAME);
    }
    public static String getItemsStockAdjustmentReason() {
        return getConfigValue(ITEMS_STOCK_ADJUSTMENT_REASON);
    }
    public static String getExportButtonName() {
        return getConfigValue(ITEMS_EXPORT_BUTTON_NAME);
    }
    public static String getImportButtonName() {
        return getConfigValue(ITEMS_IMPORT_BUTTON_NAME);
    }
    public static String getSettingsButtonName() {
        return getConfigValue(ITEMS_SETTINGS_BUTTON_NAME);
    }
    public static String getSearchButtonName() {
        return getConfigValue(ITEMS_SEARCH_BUTTON_NAME);
    }
    public static String[] getItemsButtonCssClasses() {
        return getConfigValue(ITEMS_BUTTON_CSS_CLASSES).split(",");
    }
    public static String[] getItemsDropdownCssClasses() {
        return getConfigValue(ITEMS_DROP_DOWN_CLASS).split(",");
    }
    public static String getItemsAddItemCssClass() { return getConfigValue(ITEMS_ADD_ITEM_CSS_CLASS); }
    public static String getItemsLabelCategoryName(){
        return getConfigValue(ITEMS_LABEL_CATEGORY_NAME);
    }
    public static String getItemsTopHboxSpacing(){ return getConfigValue(ITEMS_TOP_HBOX_SPACING); }
    public static String getItemsLabelStockalertName(){ return getConfigValue(ITEMS_LABEL_STOCKALERT_NAME); }
    public static String getNextButtonName() {
        return getConfigValue(ITEMS_NEXT_BUTTON_NAME);
    }
    public static String getPrevButtonName() {
        return getConfigValue(ITEMS_PREV_BUTTON_NAME);
    }
    public static String getItemsBotHboxFooterRowsPerPageLabel() { return getConfigValue(ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL); }
    public static String getItemsBotHboxFooterPageLabel() {
        return getConfigValue(ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL);
    }
    public static String getItemsSettingsContextMenu() {return  getConfigValue(ITEMS_SETTINGS_CONTEXT_MENU);}
    public static String getItemsRowsPerPage() {return  getConfigValue(ITEMS_ROWS_PER_PAGE);}
    public static String getItemsStockAlerts() {return  getConfigValue(ITEMS_STOCK_ALERTS);}
    public static String getEditButtonImage(){ return getConfigValue(EDIT_BUTTON);}
    public static String getDeleteButtonImage(){ return getConfigValue(DELETE_BUTTON);}

    //////////////////////////////////
    public String getButtonName(String function) {
        if (function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return getAddItemsButtonName();
        else if (function.equalsIgnoreCase(ITEMS_EXPORT_BUTTON_NAME)) return getExportButtonName();
        else if (function.equalsIgnoreCase(ITEMS_IMPORT_BUTTON_NAME)) return getImportButtonName();
        else if (function.equalsIgnoreCase(ITEMS_SETTINGS_BUTTON_NAME)) return getSettingsButtonName();
        else if (function.equalsIgnoreCase(ITEMS_SEARCH_BUTTON_NAME)) return getSearchButtonName();
        else if (function.equalsIgnoreCase(ITEMS_PREV_BUTTON_NAME)) return getPrevButtonName();
        else if (function.equalsIgnoreCase(ITEMS_NEXT_BUTTON_NAME)) return getNextButtonName();
        else return null;
    }
    public Font getFont(String function) {
        if (function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_EXPORT_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_IMPORT_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_SETTINGS_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_SEARCH_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_SEARCH_BUTTON_NAME)) return getButtonFont();
        else if (function.equalsIgnoreCase(ITEMS_LABEL_CATEGORY_NAME)) return getLabelFont();
        else if (function.equalsIgnoreCase(ITEMS_LABEL_STOCKALERT_NAME)) return getLabelFont();
        else return new Font("System Bold", 14.0);
    }

    private Font getButtonFont() {
        return new Font("Roboto Bold", 14.0);
    }
    private Font getLabelFont() {
        return new Font("Roboto Regular", 14.0);
    }

    public Paint getTextFill(String function) {
        if (function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return Color.WHITE;
        else return Color.web("#0000008A");
    }

    public String[] getCssClassorId(String function) {
        if (!function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return getItemsButtonCssClasses();
        else if (function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return new String[]{getItemsAddItemCssClass()};
        else if (function.equalsIgnoreCase(ITEMS_DROP_DOWN_CLASS)) return getItemsDropdownCssClasses();
        else if (function.equalsIgnoreCase(ITEMS_DROP_DOWN_CLASS)) return getItemsDropdownCssClasses();
        else return null;
    }

    public Insets getPadding(String function) {
        if (function.equalsIgnoreCase(ITEMS_VBOX_CLASSES)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_ADD_ITEM_BUTTON_NAME)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_EXPORT_BUTTON_NAME)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_IMPORT_BUTTON_NAME)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_SETTINGS_BUTTON_NAME)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_SEARCH_BUTTON_NAME)) return new Insets(20);
        else if (function.equalsIgnoreCase(ITEMS_DATATABLE_FOOTER)) return new Insets(0,0,0,20);
        else if (function.equalsIgnoreCase(ITEMS_DATABLE_FOOTER_LABELS)) return new Insets(0,0,0,20);
        else return new Insets(0);
    }

    public String getLabelName(String function) {
        if(function.equalsIgnoreCase(ITEMS_LABEL_CATEGORY_NAME)) return getItemsLabelCategoryName();
        else if(function.equalsIgnoreCase(ITEMS_LABEL_STOCKALERT_NAME)) return getItemsLabelStockalertName();
        else if(function.equalsIgnoreCase(ITEMS_DATATABLE_HEADER)) return function;
        else if(function.equalsIgnoreCase(ITEMS_BOT_HBOX_FOOTER_ROWS_PER_PAGE_LABEL)) return getItemsBotHboxFooterRowsPerPageLabel();
        else if(function.equalsIgnoreCase(ITEMS_BOT_HBOX_FOOTER_PAGE_LABEL)) return getItemsBotHboxFooterPageLabel();
        else return function;
    }

    public static List<String> stockAlerts(){
        List<String> stockAlerts = Arrays.asList(getItemsStockAlerts().split(","));
        return stockAlerts;
    }

    public static List<String> stockAdjustmentReasons(){
        List<String> stockAdjustments = Arrays.asList(getItemsStockAdjustmentReason().split(","));
        return stockAdjustments;
    }

    public static List<String> rowsPerPage(){
        return Arrays.asList(getItemsRowsPerPage().split(","));
    }

}
