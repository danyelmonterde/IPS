package com.monterdev.util;

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

    public static double formatDouble(String text){
        try{
            return Double.parseDouble(String.format("%.2f", Double.parseDouble(text)));
        }catch (IllegalFormatException exception){
            return 0.0;
        }catch (Exception e){
            return 0.0;
        }

    }

    public static String formatDouble(Double text){
        return String.format("%.2f", text);
    }

    public static Double formatToDouble(Double text){
        return DataUtil.formatDouble(String.format("%.2f", text));
    }

    public static List<Item> convertIQWDInventoryToItemList(){
        File file = new File("C:\\Users\\Daniel\\Desktop\\CONSTRUCTION.csv");
        List<Item> itemList = new ArrayList<>();
        if (file != null) {
            try {
                List<IqwdInventory> importedItems = new CsvToBeanBuilder(new FileReader(file))
                        .withType(IqwdInventory.class)
                        .build().parse();

                if (!ObjectUtils.isEmpty(importedItems)) {
                    for(int ctr=0;ctr<importedItems.size()-1;ctr++){
                        double totalAmount = 0;
                        int totalQuantity = 0;
                        double finalAverageCost = 0;
                        if(importedItems.get(ctr).getNumber()!=importedItems.get(ctr+1).getNumber()){
                            totalAmount = importedItems.get(ctr).getAmount();
                            totalQuantity = importedItems.get(ctr).getQuantity();
                            if(totalAmount ==0 && totalQuantity ==0){
                                finalAverageCost = 0;
                            }else{
                                finalAverageCost = totalAmount / totalQuantity;
                            }

                            Item item = new Item();
                            item.setItem_name(importedItems.get(ctr).getDescription());
                            item.setQuantity(totalQuantity);
                            item.setCost(DataUtil.formatToDouble(finalAverageCost));
                            item.setItem_category("CONSTRUCTION MATERIALS");
                            item.setIn_stock(totalQuantity);
                            item.setLow_stock(1);
                            item.setSku(0);
                            item.setTag("1");
                            item.setUnit("PCS");
                            itemList.add(item);
                        }else{
                            totalAmount+=importedItems.get(ctr).getAmount();
                            totalQuantity+=importedItems.get(ctr).getQuantity();
                        }
                    }
                    System.out.println(itemList.size());
                    System.out.println("");
                }

            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
        return itemList;
    }
}
