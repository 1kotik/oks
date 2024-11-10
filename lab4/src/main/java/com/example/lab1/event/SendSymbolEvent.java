package com.example.lab1.event;

import com.example.lab1.initializer.PortInitializer;
import com.example.lab1.utility.BitStaffingUtility;
import com.example.lab1.utility.CSMACDUtility;
import com.example.lab1.utility.HammingCodeUtility;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class SendSymbolEvent implements EventHandler<KeyEvent> {
    private final TextArea input;
    private final SerialPort port;
    private final Label modifiedFrame;
    private final Label symbolsSent;
    private final Label collisionLabel;
    private boolean isThreadRunning = false;

    public SendSymbolEvent(TextArea input, SerialPort port, Label modifiedFrame, Label symbolsSent, Label collisionLabel) {
        this.input = input;
        this.port = port;
        this.modifiedFrame = modifiedFrame;
        this.symbolsSent = symbolsSent;
        this.collisionLabel = collisionLabel;
    }

    @Override
    public void handle(KeyEvent event) {
        StringBuilder collisions = new StringBuilder();
        String data = input.getText();
        System.out.println(CSMACDUtility.getBusy());
        if (data.length() - Integer.parseInt(symbolsSent.getText()) >=17 && CSMACDUtility.getBusy()==0) {
            System.out.println("go");
            CSMACDUtility.setBusy(1);
            System.out.println(CSMACDUtility.getBusy());
            Thread thread = new Thread(()-> {
                try {
                    int attempt = 0;
                    //input.setDisable(true);
                    String frame = createFrame(data);
                    System.out.println(frame);
                    PortInitializer initializer = new PortInitializer();
                    initializer.initializePort(port);
                    frame = HammingCodeUtility.HammingEncode(frame);
                    frame = BitStaffingUtility.bitStaffing(frame, modifiedFrame);

                    for (int i = 0; i < frame.length(); i++) {
                        if (frame.charAt(i) == ' ') {
                            continue;
                        }
                        while (CSMACDUtility.isChannelBusy()) ;
                        writeToPort(frame.substring(i, i + 1));
                        if (CSMACDUtility.isCollisionOccurred()) {
                            attempt++;
                            collisions.append(CSMACDUtility.JAM_SIGNAL);
                            writeToPort(CSMACDUtility.JAM_SIGNAL);
                            i--;
                            CSMACDUtility.setDelay(attempt);
                        } else {
                            attempt = 0;
                            collisions.append(". ");
                        }
                    }

                    writeToPort(CSMACDUtility.FRAME_END);
                    Platform.runLater(()->{
                        collisionLabel.setText(collisions.toString());
                        String currentCount = symbolsSent.getText();
                        int newCount = Integer.parseInt(currentCount) + 17;
                        symbolsSent.setText(String.valueOf(newCount));
                        //CSMACDUtility.setBusy(newCount);
                    });

                    //input.setDisable(false);
                    //input.positionCaret(input.getText().length());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                } finally {
                    CSMACDUtility.setBusy(0);
                    handle(event);
                }
            });
            thread.start();
        }
    }

    private void writeToPort(String message) {
        byte[] charMessage = message.getBytes(StandardCharsets.UTF_8);
        try {
            //System.out.println(message);
            port.writeBytes(charMessage);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }


    public String createFrame(String data) {
        int pos=Integer.parseInt(symbolsSent.getText());
        String source = Integer.toBinaryString(Integer.parseInt(port.getPortName().substring(3)));
        for (int i = source.length(); i < 4; i++) {
            source = "0" + source;
        }
        //return BitStaffingUtility.FLAG + BitStaffingUtility.DEST + source
        //        + data.substring(data.length() - 17) + "00000";
        //System.out.println(data.substring(pos,pos+17));
        return BitStaffingUtility.FLAG + BitStaffingUtility.DEST + source
               + data.substring(pos,pos+17) + "00000";
    }
}
