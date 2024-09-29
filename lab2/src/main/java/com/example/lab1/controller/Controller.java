package com.example.lab1.controller;

import com.example.lab1.event.ReadSymbolEvent;
import com.example.lab1.event.SendSymbolEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.*;

public class Controller {
    SerialPort readPort;
    SerialPort writePort;
    int bytesInFrameCount;
    @FXML
    private TextArea input_window;

    @FXML
    private TextArea output_window;

    @FXML
    private Label baud_rate;

    @FXML
    private Label symbols_sent;

    @FXML
    private ComboBox<String> input_choice;

    @FXML
    private ComboBox<String> output_choice;


    @FXML
    private Label data_bits;

    @FXML
    private Label stop_bits;

    @FXML
    private Label parity;

    @FXML
    private Label modifiedFrame;

    @FXML
    public void initialize() {
        bytesInFrameCount = 0;
        readPort = new SerialPort("");
        writePort = new SerialPort("");
        symbols_sent.setText("0");
        baud_rate.setText("-");
        data_bits.setText("-");
        stop_bits.setText("-");
        parity.setText("-");
        input_choice.setValue("Select COM-port to transmit");
        output_choice.setValue("Select COM-port to receive");
        input_choice.addEventHandler(MouseEvent.MOUSE_CLICKED,
                _ -> input_choice.setItems(getPorts(true)));
        output_choice.addEventHandler(MouseEvent.MOUSE_CLICKED,
                _ -> output_choice.setItems(getPorts(false)));
        TextFormatter<String> inputFormatter = new TextFormatter<>(change -> {
            if (change.isDeleted() || change.getText().isEmpty()) {
                return null;
            }
            if (input_choice.getValue().equals("-") || input_choice.getValue().equals("Select COM-port to transmit")) {
                setAlert("Error!", "No transmitter selected");
                return null;
            }
            if (change.getControlNewText().matches("[0-1\n]*") && writePort.isOpened()) {
                Platform.runLater(() -> {
                    if (!change.getControlNewText().endsWith("\n")) {
                        bytesInFrameCount++;
                    }
                    if (bytesInFrameCount % 17 == 0 && !change.getControlNewText().endsWith("\n")) {
                        String currentCount = symbols_sent.getText();
                        int newCount = Integer.parseInt(currentCount) + 17;
                        symbols_sent.setText(String.valueOf(newCount));
                        input_window.appendText("\n");
                    }
                });
                return change;
            }
            return null;
        });
        input_window.setTextFormatter(inputFormatter);

        TextFormatter<String> outputFormatter = new TextFormatter<>(change -> {
            return change;
        });
        output_window.setTextFormatter(outputFormatter);

        input_choice.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && !Objects.equals(oldItem, newItem)) {
                try {
                    writePortSelected();
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        output_choice.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                try {
                    readPortSelected();
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void writePortSelected() throws SerialPortException {
        try {
            int status = initPort(writePort, input_choice, true);
            SendSymbolEvent sendSymbolEvent = new SendSymbolEvent(input_window, writePort, modifiedFrame);
            if (status == 2) {
                writePort = new SerialPort(input_choice.getValue());
                if (writePort.isOpened()) {
                    return;
                }
                try {
                    writePort.openPort();
                } catch (SerialPortException e) {
                    setAlert("Error!", String.format("%s: Unable to open port\n", writePort.getPortName()));
                    input_choice.setValue("-");
                }
            }
            if (writePort.isOpened()) {
                sendSymbolEvent = new SendSymbolEvent(input_window, writePort, modifiedFrame);
                SendSymbolEvent finalSendSymbolEvent = sendSymbolEvent;
                input_window.addEventHandler(KeyEvent.KEY_TYPED, event -> {
                    byte symbol = event.getCharacter().getBytes()[0];
                    if ((writePort.isOpened()) && (symbol == '0' || symbol == '1') || symbol == 13) {
                        finalSendSymbolEvent.handle(event);
                    } else {
                        event.consume();
                    }
                });
                baud_rate.setText("9600");
                data_bits.setText("8");
                stop_bits.setText("1");
                parity.setText("None");
            }
            if (status == 1) {
                input_window.removeEventHandler(KeyEvent.KEY_TYPED, sendSymbolEvent);
                writePort = new SerialPort("");
                baud_rate.setText("-");
                data_bits.setText("-");
                stop_bits.setText("-");
                parity.setText("-");
            }
        } catch (SerialPortException _) {
        }
    }

    public void readPortSelected() throws SerialPortException {

        try {
            int status = initPort(readPort, output_choice, false);
            if (status == 1) {
                readPort = new SerialPort("");
                return;
            } else if (status == 2) {
                readPort = new SerialPort(output_choice.getValue());
                if (readPort.isOpened()) {
                    return;
                }
                try {
                    readPort.openPort();
                } catch (SerialPortException _) {
                    setAlert("Error!", String.format("%s: Unable to open port\n", readPort.getPortName()));
                    output_choice.setValue("-");
                }
            }
            if (readPort.isOpened()) {
                readPort.addEventListener(new ReadSymbolEvent(readPort, output_window), SerialPort.MASK_RXCHAR);
            }
        } catch (SerialPortException _) {

        }
    }

    public int initPort(SerialPort port, ComboBox<String> comboBox, boolean write) throws SerialPortException {

        if (comboBox.getValue().equals("-")) {
            if (port.isOpened()) {
                port.closePort();
            }
            return 1;
        }

        if (port.isOpened()) {
            port.closePort();
        }
        return 2;
    }

    public ObservableList<String> getPorts(boolean write) {
        ObservableList<String> result = FXCollections.observableArrayList("-");
        String[] ports = SerialPortList.getPortNames();
        ArrayList<String> sortedPorts = new ArrayList<>();
        if (write) {
            for (int i = 0; i < ports.length; i += 2) {
                if (Integer.parseInt(ports[i].substring(3)) <= 15) {
                    sortedPorts.add(ports[i]);
                }
            }
            if (!output_choice.getValue().equals("-") && !output_choice.getValue().equals("Select COM-port to receive")) {
                sortedPorts.remove(output_choice.getValue());
                sortedPorts.remove(String.format("COM%d",
                        Integer.parseInt(output_choice.getValue().substring(3)) - 1));
            }
        } else {
            for (int i = 1; i < ports.length; i += 2) {
                if (Integer.parseInt(ports[i].substring(3)) <= 16) {
                    sortedPorts.add(ports[i]);
                }
            }
            if (!input_choice.getValue().equals("-") && !input_choice.getValue().equals("Select COM-port to transmit")) {
                sortedPorts.remove(input_choice.getValue());
                sortedPorts.remove(String.format("COM%d",
                        Integer.parseInt(input_choice.getValue().substring(3)) + 1));
            }
        }
        sortedPorts.remove("COM3");
        sortedPorts.remove("COM4");
        result.addAll(sortedPorts);
        return result;
    }


    EventHandler<WindowEvent> closeEventHandler = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            try {
                if (readPort.isOpened()) {
                    readPort.closePort();
                }
                if (writePort.isOpened()) {
                    writePort.closePort();
                }

            } catch (SerialPortException _) {

            }
        }
    };

    public EventHandler<WindowEvent> onClose() {
        return closeEventHandler;
    }


    public void setAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}