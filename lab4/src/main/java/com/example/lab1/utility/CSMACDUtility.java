package com.example.lab1.utility;

import java.util.Random;

public class CSMACDUtility {
    public static final String JAM_SIGNAL = "!";
    public static final String FRAME_END = "E";
    private static int busy=0;

    public static boolean isChannelBusy(){
        Random random = new Random();
        int probability = 7;
        int randomNumber = random.nextInt(10);
        return randomNumber < probability;
    }

    public static boolean isCollisionOccurred(){
        Random random = new Random();
        int probability = 3;
        int randomNumber = random.nextInt(10);
        return randomNumber < probability;
    }

    public static void setDelay(int attempt){
        try {
            Thread.sleep(Math.min(attempt, 10) * 100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getBusy(){
        return busy;
    }
    public static void setBusy(int busy){
        CSMACDUtility.busy = busy;
    }
}
