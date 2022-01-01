package com.monterdev.util;

import com.monterdev.model.Item;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchUtil {

    public void searchItem(String text, List<Item> resultsList, List<String> responseList, String searchType) {
        responseList.clear();
        Collections.sort(resultsList);
        for(Item item: resultsList){
            if(item.getItem_name().toLowerCase().startsWith(text.toLowerCase())){
                responseList.add(item.getItem_name());
            }
        }
    }


}
