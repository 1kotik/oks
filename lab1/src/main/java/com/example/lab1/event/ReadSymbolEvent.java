package com.example.lab1.event;

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
                byte[] symbol = port.readBytes(1);
                if ((symbol[0] > 31 && symbol[0] < 127) || symbol[0] == '\n') {
                    String outputData = new String(symbol, StandardCharsets.UTF_8);
                    output.appendText(outputData);

                }

            } catch (SerialPortException e) {
               e.printStackTrace();
            }
        }
    }
}
