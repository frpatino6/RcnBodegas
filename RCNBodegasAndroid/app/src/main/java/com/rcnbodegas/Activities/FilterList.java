package com.rcnbodegas.Activities;

import java.util.ArrayList;
import java.util.List;

public class FilterList<E> {


    public  <T> List filterList(List<T> originalList, Filter filter, E text) {
        try {
            List<T> filterList = new ArrayList<T>();
            for (T object : originalList) {
                if (filter.isMatched(object, text.toString().toLowerCase())) {
                    filterList.add(object);
                } else {
                    continue;
                }
            }
            return filterList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}