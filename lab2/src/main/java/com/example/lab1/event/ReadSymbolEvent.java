package com.example.lab1.event;

import com.example.lab1.utility.BitStaffingUtility;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class ReadSymbolEvent implements SerialPortEventListener {
    private final SerialPort port;
    private final TextArea output;


    public ReadSymbolEvent(SerialPort port, TextArea output) {
        this.port = port;
        this.output = output;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                byte[] symbol = port.readBytes(serialPortEvent.getEventValue());
                if (symbol[0] == '0' || symbol[0] == '1' || symbol[0] == '\n') {
                    String receivedData = new String(symbol, StandardCharsets.UTF_8);
                    String frame= BitStaffingUtility.bitDeStaffing(receivedData);
                    String outputData=frame.substring(16,33);
                    output.appendText(outputData+"\n");
                }

            } catch (SerialPortException e) {
               e.printStackTrace();
            }
        }
    }
}
