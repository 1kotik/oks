package com.example.lab1.event;

import com.example.lab1.utility.BitStaffingUtility;
import com.example.lab1.utility.CSMACDUtility;
import com.example.lab1.utility.HammingCodeUtility;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class ReadSymbolEvent implements SerialPortEventListener {
    private final SerialPort port;
    private final TextArea output;
    private StringBuilder result;

    public ReadSymbolEvent(SerialPort port, TextArea output) {
        this.port = port;
        this.output = output;
        result = new StringBuilder();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                byte[] symbol = port.readBytes(serialPortEvent.getEventValue());
                String receivedData = new String(symbol, StandardCharsets.UTF_8);
                result.append(receivedData);
                //System.out.println(receivedData);
                if(receivedData.contains(CSMACDUtility.JAM_SIGNAL)){
                    result.delete(result.length()-2, result.length());
                    //System.out.println(result.toString());
                }
                else if(receivedData.contains(CSMACDUtility.FRAME_END)){
                    result.delete(result.length()-1, result.length());
                    String frame = BitStaffingUtility.bitDeStaffing(result.toString());
                    frame = HammingCodeUtility.createErrorBit(frame);
                    frame = HammingCodeUtility.HammingDecode(frame);
                    String outputData = frame.substring(16, 33);
                    output.appendText(outputData);
                    result.delete(0, result.length());
                }
                else{
                    //result.append(receivedData);
                    //System.out.println(result.toString());
                }


            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    /*@Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                byte[] symbol = port.readBytes(serialPortEvent.getEventValue());
                String receivedData = new String(symbol, StandardCharsets.UTF_8);
                String frame = BitStaffingUtility.bitDeStaffing(receivedData);
                frame = HammingCodeUtility.createErrorBit(frame);
                frame = HammingCodeUtility.HammingDecode(frame);
                String outputData = frame.substring(16, 33);
                output.appendText(outputData);

            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }*/
}
