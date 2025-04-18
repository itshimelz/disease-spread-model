package domain.utils;

import domain.person.Person;

import java.util.ArrayList;
import java.util.List;

public class MergeSort {

    public static void mergeSort(List<Person> list) {
        if (list == null || list.size() <= 1) {
            return; // Nothing to sort
        }
        int n = list.size();
        List<Person> tempArray = new ArrayList<>(list);
        mergeSortHelper(list, tempArray, 0, n - 1);
    }

    private static void mergeSortHelper(List<Person> list, List<Person> tempArray, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2; // Avoid potential overflow
            mergeSortHelper(list, tempArray, left, mid);
            mergeSortHelper(list, tempArray, mid + 1, right);
            merge(list, tempArray, left, mid, right);
        }
    }

    private static void merge(List<Person> list, List<Person> tempArray, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (list.get(i).getAge() <= list.get(j).getAge()) { // Compare by age
                tempArray.set(k, list.get(i));
                i++;
            } else {
                tempArray.set(k, list.get(j));
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i <= mid) {
            tempArray.set(k, list.get(i));
            i++;
            k++;
        }
        while (j <= right) {
            tempArray.set(k, list.get(j));
            j++;
            k++;
        }

        // Copy sorted elements back to the original list
        for (k = left; k <= right; k++) {
            list.set(k, tempArray.get(k));
        }
    }
}
