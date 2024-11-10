package com.example.lab1.utility;

import javafx.util.Pair;

import java.util.Random;

public class HammingCodeUtility {
    public static String HammingEncode(String frame) {
        StringBuilder data = new StringBuilder(frame.substring(16));
        boolean parity = false;
        int checkBitCount = log2(data.substring(0,17).length()+1);
        for (int checkBit = 0; checkBit < checkBitCount; checkBit++) {
            for (int i = 0; i < data.length() - checkBitCount; i++) {
                if (isBitControlled(i + 1, checkBit)) {
                    parity ^= charToBoolean(data.charAt(i));
                }
            }
            data.setCharAt(data.length() - 5 + checkBit, parity ? '1' : '0');
            parity = false;
        }
        return frame.substring(0, 16) + data.toString();
    }

    public static String HammingDecode(String frame) {
        StringBuilder data = new StringBuilder(frame.substring(16));
        int errorPosition = 0;
        int checkBitCount = log2(data.substring(0,17).length()+1);
        for (int checkBit = 0; checkBit < checkBitCount; checkBit++) {
            boolean parity = charToBoolean(data.charAt(data.length() - 5 + checkBit));
            for (int i = 0; i < data.length() - checkBitCount; i++) {
                if (isBitControlled(i + 1, checkBit)) {
                    parity ^= charToBoolean(data.charAt(i));
                }
            }
            if (parity) {
                errorPosition += (int) Math.pow(2, checkBit);
            }
        }

        if (errorPosition != 0) {
            data.setCharAt(errorPosition - 1, data.charAt(errorPosition - 1) == '1' ? '0' : '1');
        }
        return frame.substring(0, 16) + data.toString();
    }

    public static String createErrorBit(String frame) {
        StringBuilder data = new StringBuilder(frame.substring(16, 33));
        if (isErrorOccurred()) {
            int errorPosition = 0;
            do {
                Random random = new Random();
                errorPosition = random.nextInt(data.length());
            } while (data.charAt(errorPosition) == '\n');
            data.setCharAt(errorPosition, data.charAt(errorPosition) == '1' ? '0' : '1');
        }
        return frame.substring(0, 16) + data.toString() + frame.substring(33);
    }

    public static boolean isBitControlled(int pos, int checkBit) {
        int shiftedPos = pos >>> checkBit;
        return (shiftedPos & 1) == 1;
    }

    public static boolean charToBoolean(char ch) {
        return ch == '1';
    }

    public static boolean isErrorOccurred() {
        Random random = new Random();
        int probability = 3;
        int randomNumber = random.nextInt(10);
        return randomNumber < probability;
    }

    public static int log2(int x){
        return (int)(Math.ceil(Math.log(x)/Math.log(2)));
    }

}
