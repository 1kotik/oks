package com.example.lab1.initializer;

import jssc.SerialPort;
import jssc.SerialPortException;

public class PortInitializer {
    public void initializePort(SerialPort serialPort) throws SerialPortException{
        try {
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            //System.out.println(serialPort.getFlowControlMode());
        }catch (SerialPortException e){
            e.printStackTrace();
        }
    }
}
