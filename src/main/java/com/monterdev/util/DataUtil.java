package com.monterdev.util;

import com.monterdev.constants.ErrorConstants;
import com.monterdev.exception.IqwdException;
import com.monterdev.model.IqwdInventory;
import com.monterdev.model.Item;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class DataUtil {

    private static final String STRING_FORMAT = "%.2f";
    private static final String IQWD_MIGRATION_DEFAULT_CATEGORY = "CONSTRUCTION MATERIALS";
    private static final String IQWD_MIGRATION_DEFAULT_TAG = "1";
    private static final String IQWD_MIGRATION_DEFAULT_UNIT = "PCS";

    public static double formatDouble(String text) {
        try{
            return Double.parseDouble(String.format(STRING_FORMAT, Double.parseDouble(text)));
        }catch (IllegalFormatException illegalFormatException){
            return 0.0;
        }catch (NullPointerException nullPointerException){
            return 0.0;
        }catch (NumberFormatException numberFormatException){
            return 0.0;
        }

    }

    public static Double formatToDouble(Double number){
        try{
            return Double.parseDouble((String.format(STRING_FORMAT, number)));
        }catch (IllegalFormatException exception){
            return 0.0;
        }catch (NullPointerException e){
            return 0.0;
        }catch (NumberFormatException exception){
            return 0.0;
        }
    }

    public static String formatDoubleToString(Double number){
        try{
            return String.format(STRING_FORMAT, number);
        }catch (IllegalFormatException exception){
            return "0.0";
        }catch (NullPointerException e){
            return "0.0";
        }catch (NumberFormatException exception){
            return "0.0";
        }
    }

    public static List<Item> migrateIQWDInventory(File file){
        List<Item> itemList = new ArrayList<>();
        if (file != null) {
            try {
                List<IqwdInventory> importedItems = new CsvToBeanBuilder(new FileReader(file))
                        .withType(IqwdInventory.class)
                        .build().parse();
                List<Integer> quantityListPerItem = new ArrayList<>();
                List<Double> costListPerItem = new ArrayList<>();
                if (!ObjectUtils.isEmpty(importedItems)) {
                    for(int ctr=0;ctr<=importedItems.size()-1;ctr++){
                        double totalAmount = 0;
                        int totalQuantity = 0;
                        if(importedItems.get(ctr).getNumber()==1000){
                            //if Number is equal to 1000 then it should terminate
                            getAverageCost(itemList, importedItems, quantityListPerItem, costListPerItem, ctr, totalAmount, totalQuantity);
                        }
                        else if(importedItems.get(ctr).getNumber()!=importedItems.get(ctr+1).getNumber()){
                            getAverageCost(itemList, importedItems, quantityListPerItem, costListPerItem, ctr, totalAmount, totalQuantity);
                        }else{
                            quantityListPerItem.add(importedItems.get(ctr).getQuantity());
                            costListPerItem.add(importedItems.get(ctr).getAmount());
                        }
                    }
                }

            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
        return itemList;
    }

    private static void getAverageCost(List<Item> itemList, List<IqwdInventory> importedItems, List<Integer> quantityListPerItem, List<Double> costListPerItem, int ctr, double totalAmount, int totalQuantity) {
        double finalAverageCost;

        quantityListPerItem.add(importedItems.get(ctr).getQuantity());
        costListPerItem.add(importedItems.get(ctr).getAmount());

        for(int qtyCtr = 0; qtyCtr<= quantityListPerItem.size()-1; qtyCtr++){
            totalQuantity += quantityListPerItem.get(qtyCtr);
            totalAmount += costListPerItem.get(qtyCtr);
        }

        if(totalAmount ==0 && totalQuantity ==0){
            finalAverageCost = 0;
        }else{
            finalAverageCost = totalAmount / totalQuantity;
        }

        Item item = new Item();
        item.setItem_name(importedItems.get(ctr).getDescription());
        item.setQuantity(totalQuantity);
        item.setCost(finalAverageCost);
        item.setItem_category(IQWD_MIGRATION_DEFAULT_CATEGORY);
        item.setIn_stock(totalQuantity);
        item.setLow_stock(1);
        item.setSku(0);
        item.setTag(IQWD_MIGRATION_DEFAULT_TAG);
        item.setUnit(IQWD_MIGRATION_DEFAULT_UNIT);
        itemList.add(item);
        quantityListPerItem.clear();
        costListPerItem.clear();
    }
}
