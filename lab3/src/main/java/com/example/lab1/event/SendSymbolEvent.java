package com.example.lab1.event;

import com.example.lab1.initializer.PortInitializer;
import com.example.lab1.utility.BitStaffingUtility;
import com.example.lab1.utility.HammingCodeUtility;
import javafx.event.EventHandler;
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

    public SendSymbolEvent(TextArea input, SerialPort port, Label modifiedFrame, Label symbolsSent) {
        this.input = input;
        this.port = port;
        this.modifiedFrame = modifiedFrame;
        this.symbolsSent = symbolsSent;
    }

    @Override
    public void handle(KeyEvent event) {
        try {
            String data = input.getText();
            if (data.length() % 17 == 0) {
                String frame = createFrame(data);
                PortInitializer initializer = new PortInitializer();
                initializer.initializePort(port);
                frame = HammingCodeUtility.HammingEncode(frame);
                byte[] message = BitStaffingUtility.bitStaffing(frame, modifiedFrame).getBytes(StandardCharsets.UTF_8);
                port.writeBytes(message);
                String currentCount = symbolsSent.getText();
                int newCount = Integer.parseInt(currentCount) + 17;
                symbolsSent.setText(String.valueOf(newCount));
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public String createFrame(String data) {

        String source = Integer.toBinaryString(Integer.parseInt(port.getPortName().substring(3)));
        for (int i = source.length(); i < 4; i++) {
            source = "0" + source;
        }
        return BitStaffingUtility.FLAG + BitStaffingUtility.DEST + source
                + data.substring(data.length() - 17) + "00000";
    }
}
