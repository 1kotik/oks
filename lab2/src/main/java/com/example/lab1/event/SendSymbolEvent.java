package com.example.lab1.event;

import com.example.lab1.initializer.PortInitializer;
import com.example.lab1.utility.BitStaffingUtility;
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

    public SendSymbolEvent(TextArea input, SerialPort port, Label modifiedFrame) {
        this.input = input;
        this.port = port;
        this.modifiedFrame = modifiedFrame;
    }

    @Override
    public void handle(KeyEvent event) {
        try {
            String data = input.getText().replace("\n", "");
            if (data.length() % 17 == 0 && !input.getText().substring(input.getLength() - 1).equals("\n")) {
                String frame = createFrame(data);
                PortInitializer initializer = new PortInitializer();
                initializer.initializePort(port);
                byte[] message = BitStaffingUtility.bitStaffing(frame, modifiedFrame).getBytes(StandardCharsets.UTF_8);
                port.writeBytes(message);
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
                + data.substring(data.length() - 17) + BitStaffingUtility.FCS;
    }
}
