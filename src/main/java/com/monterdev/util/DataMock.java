package com.monterdev.util;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import com.monterdev.model.CustomItemList;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

import static com.monterdev.util.ComponentCreator.*;

public class DataMock {

    public static List<CustomItemList> getMockedCustomItemList(){

        CustomItemList customItemList = new CustomItemList();
        customItemList.setItemName(new JFXCheckBox("Clamp"));
        customItemList.setCategory(addComboboxWithPadding(""));
        customItemList.setPrice(addTextFieldWithPadding("",Pos.CENTER));
        customItemList.setCost(addLabel("10"));
        customItemList.setMargin(addLabel("150%"));
        customItemList.setInStock(addTextFieldWithPadding("",Pos.CENTER));

        CustomItemList customItemList2 = new CustomItemList();
        customItemList2.setItemName(new JFXCheckBox("Tube"));
        customItemList2.setCategory(addComboboxWithPadding(""));
        customItemList2.setPrice(addTextFieldWithPadding("",Pos.CENTER));
        customItemList2.setCost(addLabel("13"));
        customItemList2.setMargin(addLabel("50%"));
        customItemList2.setInStock(addTextFieldWithPadding("",Pos.CENTER));

        List<CustomItemList> itemLists = new ArrayList<>();
        itemLists.add(customItemList);
        itemLists.add(customItemList2);
        return itemLists;
    }
}
