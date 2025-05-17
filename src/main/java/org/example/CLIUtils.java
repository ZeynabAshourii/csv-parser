package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLIUtils {
    public static List<String> promptForList(Scanner scanner, String prompt, String separator) {
        System.out.println(prompt);
        String input = scanner.nextLine();
        String[] parts = input.split(separator);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part.trim());
        }
        return result;
    }

    public static List<Boolean> convertToBooleanList(List<String> strings) {
        List<Boolean> result = new ArrayList<>();
        for (String s : strings) {
            result.add(Boolean.parseBoolean(s));
        }
        return result;
    }
}