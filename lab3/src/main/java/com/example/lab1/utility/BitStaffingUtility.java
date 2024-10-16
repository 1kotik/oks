package com.example.lab1.utility;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class BitStaffingUtility {
    public static final String FLAG = "10010001";
    public static final String DEST = "0000";
    //public static final String FCS = "0";
    private static int bitCounter = 0;
    private static ArrayList<Integer> modifiedIndexes = new ArrayList<>();

    public static String bitStaffing(String input, Label frame) {
        String data = input.substring(8);
        modifiedIndexes.add(-1);
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == FLAG.charAt(bitCounter)) {
                bitCounter++;
            } else {
                if (bitCounter != 0) {
                    if (i >= 4 && bitCounter >= 4) {
                        i -= 4;
                    } else {
                        i--;
                    }
                }
                bitCounter = 0;
            }
            if (bitCounter == 7) {
                data = addChar(data, '0', ++i);
                modifiedIndexes.add(i + 8);
                bitCounter = 0;
            }
        }
        String result = input.substring(0, 8) + data;
        frame.setText("");
        frame.setText(result);
        paintModifiedFrame(frame);
        bitCounter = 0;
        return result;
    }

    public static String bitDeStaffing(String input) {
        String data = input.substring(8);
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == FLAG.charAt(bitCounter)) {
                bitCounter++;
            } else {
                if (bitCounter != 0) {
                    if (i >= 4 && bitCounter >= 4) {
                        i -= 4;
                    } else {
                        i--;
                    }
                }
                bitCounter = 0;
            }
            if (bitCounter == 7) {
                data = removeCharAt(data, i);
                bitCounter = 0;
            }
        }
        bitCounter = 0;
        return input.substring(0, 8) + data;
    }

    public static String addChar(String str, char ch, int pos) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(pos, ch);
        return sb.toString();
    }

    public static String removeCharAt(String str, int pos) {
        return str.substring(0, pos) + str.substring(pos + 1);
    }

    public static void paintModifiedFrame(Label frame) {
        HBox hbox = new HBox();
        frame = insertSpace(frame);
        //System.out.println(modifiedIndexes);
        for (int i = 1; i < modifiedIndexes.size(); i++) {
            Text plainText = new Text(frame.getText().substring(modifiedIndexes.get(i - 1) + 1, modifiedIndexes.get(i)));
            plainText.setFill(Color.BLACK);
            plainText.setFont(Font.font(10));
            Text coloredText = new Text(frame.getText().substring(modifiedIndexes.get(i), modifiedIndexes.get(i) + 1));
            coloredText.setFill(Color.RED);
            coloredText.setFont(Font.font(10));
            plainText.setText(plainText.getText().replace("\n", "\\n"));
            coloredText.setText(coloredText.getText().replace("\n", "\\n"));
            hbox.getChildren().addAll(plainText, coloredText);
        }
        Text remainingText = new Text(frame.getText()
                .substring(modifiedIndexes.getLast() + 1).replace("\n", "\\n"));
        remainingText.setFont(Font.font(10));
        hbox.getChildren().add(remainingText);
        frame.setText("");
        hbox.setAlignment(Pos.CENTER);
        modifiedIndexes.clear();
        frame.setGraphic(hbox);
    }

    public static Label insertSpace(Label frame) {
        StringBuilder sb = new StringBuilder(frame.getText());
        if (modifiedIndexes.getLast() >= sb.length() - 5) {
            sb.insert(sb.length() - 6, " ");
            int last = modifiedIndexes.getLast();
            modifiedIndexes.removeLast();
            modifiedIndexes.add(last + 1);
        } else {
            sb.insert(sb.length() - 5, " ");
        }
        frame.setText(sb.toString());
        return frame;
    }

}
