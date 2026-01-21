package com.example.Commerce.utils.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSort {
    
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) return;
        mergeSort(list, 0, list.size() - 1, comparator);
    }
    
    private static <T> void mergeSort(List<T> list, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid, comparator);
            mergeSort(list, mid + 1, right, comparator);
            merge(list, left, mid, right, comparator);
        }
    }
    
    private static <T> void merge(List<T> list, int left, int mid, int right, Comparator<T> comparator) {
        List<T> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<T> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));
        
        int i = 0, j = 0, k = left;
        
        while (i < leftList.size() && j < rightList.size()) {
            if (comparator.compare(leftList.get(i), rightList.get(j)) <= 0) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }
        
        while (i < leftList.size()) {
            list.set(k++, leftList.get(i++));
        }
        
        while (j < rightList.size()) {
            list.set(k++, rightList.get(j++));
        }
    }
}
