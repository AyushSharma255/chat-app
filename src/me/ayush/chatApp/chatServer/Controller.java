package me.ayush.chatApp.chatServer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Controller {
    @FXML TextArea chatArea;
    @FXML TextField chatField;
    @FXML Button chatSend;

    ServerSocket server;
    ArrayList<PrintWriter> writers;

    public void initialize() {
        writers = new ArrayList<>();

        try {
            chatArea.setText(String.format("Server IP: %s%n", InetAddress.getLocalHost().getHostAddress()));
            server = new ServerSocket(5050);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            message("Unknown Host", "Unknown Host.", AlertType.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread serverHelper = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket client = server.accept();
                        writers.add(new PrintWriter(client.getOutputStream()));

                        Thread clientReader = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                    String line = null;
                                    while ((line = reader.readLine()) != null) {
                                        chatArea.appendText(String.format("%s%n", line));
                                        for (PrintWriter writer : writers) {
                                            writer.println(line);
                                            writer.flush();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        clientReader.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        serverHelper.start();
    }

    public void message(String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
