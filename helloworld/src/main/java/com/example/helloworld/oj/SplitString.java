package com.example.helloworld.oj;

import java.util.Scanner;

public class SplitString {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            String input = scan.nextLine();
            int result = getCountOfString(input);
            System.out.println(result);
        }

    }

    public static int getCountOfString(String input) {
        if (input == null || input.length() == 0) {
            return 0;
        }
        String array[] = input.split("\\s+|#+|(abc)+");
        return array.length;
    }
}
