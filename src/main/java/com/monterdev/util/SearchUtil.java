package com.monterdev.util;

import com.monterdev.model.Item;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchUtil {

    public void searchItem(String text, List<Item> resultsList, List<String> responseList, String searchType) {
        responseList.clear();
        StringBuilder regexTextBuilder = new StringBuilder();
        regexTextBuilder.setLength(0);
        try {
            if (text.matches(".*\\s.*") && text.length() >= 5) {
                String[] splitText = text.split("\\s+");
                regexTextBuilder.append(".*");
                for (int ctr = 0; ctr < splitText.length; ctr++) {
                    regexTextBuilder.append(splitText[ctr].toCharArray()[1]);
                    regexTextBuilder.append(splitText[ctr].toCharArray()[2]);
                    regexTextBuilder.append(".");
                    regexTextBuilder.append(splitText[ctr].toCharArray()[4]);
                    regexTextBuilder.append(".*");
                }
            } else if (text.length() >= 5) {
                regexTextBuilder.append(".*");
                regexTextBuilder.append(text.toCharArray()[1]);
                regexTextBuilder.append(text.toCharArray()[2]);
                regexTextBuilder.append(".");
                regexTextBuilder.append(text.toCharArray()[4]);
                regexTextBuilder.append(".*");
            }
            Pattern p = Pattern.compile(regexTextBuilder.toString(), Pattern.CASE_INSENSITIVE);
            for (int ctr = 0; ctr < resultsList.size(); ctr++) {
                Matcher m = p.matcher(resultsList.get(ctr).getItem_name());
                if (m.matches()) {
                    if (searchType.equalsIgnoreCase("SKU")) {
                        responseList.add(String.valueOf(resultsList.get(ctr).getSku()));
                    } else if (searchType.equalsIgnoreCase("NAME")) {
                        responseList.add(resultsList.get(ctr).getItem_name());
                    }
                    Collections.sort(responseList);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }


}
