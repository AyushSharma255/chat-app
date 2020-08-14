package me.ayush.chatApp.chatClient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller {
    @FXML TextArea chatArea;
    @FXML TextField chatField;
    @FXML Button chatSend;

    Socket client;
    String username;

    public void initialize() {
        chatArea.setText(String.format("Please give us the server IP that you would like to connect to. (Put it in the chat box, and send)%n"));
    }

    public void send() {
        if (chatField.getText().length() != 0) {
            if (client == null) {
                // Create Socket
                try {
                    client = new Socket(chatField.getText(), 5050);
                    chatArea.setText(String.format("You are now in server IP: %s%n", chatField.getText()));
                    chatField.setText("");
                    // Ask for Username
                    TextInputDialog dialog = new TextInputDialog(String.format("User%d", (int) (Math.random() * 500) + 1 ));
                    dialog.setHeaderText("Username");
                    dialog.setContentText("Please put a username! Or leave it blank, and get a random name.");
                    dialog.getEditor().setText("");
                    dialog.showAndWait();
                    if (dialog.getEditor().getText().trim().length() != 0) {
                        username = dialog.getEditor().getText().trim();
                    } else {
                        username = dialog.getDefaultValue();
                    }

                    Thread serverReader = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                String line = null;

                                while ((line = reader.readLine()) != null) {
                                    chatArea.appendText(String.format("%s%n", line));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    serverReader.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    message("Invalid IP", "Your server IP is invalid.", AlertType.ERROR);
                }
            } else {
                // Send to Socket
                try {
                    PrintWriter writer = new PrintWriter(client.getOutputStream());
                    writer.println(String.format("<@%s>: %s", username, chatField.getText()));
                    writer.flush();
                    chatField.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void message(String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
