package com.example.lab1.event;

import com.example.lab1.initializer.PortInitializer;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import jssc.SerialPort;
import jssc.SerialPortException;
import java.nio.charset.StandardCharsets;

public class SendSymbolEvent implements EventHandler<KeyEvent> {
    private final TextArea input;
    private final SerialPort port;

    public SendSymbolEvent(TextArea input, SerialPort port) {
        this.input = input;
        this.port = port;
    }
    @Override
    public void handle(KeyEvent event) {
        try {
            PortInitializer initializer = new PortInitializer();
            initializer.initializePort(port);
            byte[] message = input.getText().getBytes(StandardCharsets.UTF_8);
            byte symbol = message[message.length - 1];
            if ((symbol > 31 && symbol < 127) || symbol == '\n') {
                port.writeByte(symbol);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
