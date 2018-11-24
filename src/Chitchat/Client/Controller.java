package Chitchat.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller {

    @FXML
    TextField textField;

    @FXML
    ListView listView;

    @FXML
    Button btn1;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientList;

    Socket socket;

    DataInputStream in;
    DataOutputStream out;

    private boolean isAuthorized;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;

        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // цикл для авторизации
                        while (true) {
                            // если получаем ответ /authok то значит мы авторизовались корректно
                            String str = in.readUTF();
                            if (str.startsWith("/authok")) {
                                setAuthorized(true);
                                break;
                            } else {
                                //textArea.appendText(str + "\n");
                                Label message = new Label(str + "\n");
                                VBox messageBox = new VBox(message);
                                listView.getItems().add(messageBox);
                            }
                        }
                        // цикл для работы
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/")) {
                                if (str.equals("/serverClosed")) break;
                                if (str.startsWith("/clientlist")) {
                                    String[] tokens = str.split(" ");

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            clientList.getItems().clear();
                                            for (int i = 1; i < tokens.length; i++) {
                                                clientList.getItems().add(tokens[i]);
                                            }
                                        }
                                    });
                                }
                            } else {
                                //textArea.appendText(str + "\n");
                                Label message = new Label(str);
                                VBox messageBox = new VBox(message);
                                listView.getItems().add(messageBox);
//                                Platform.runLater(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            createWindow();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });

                            }


//                            if (str.equals("/serverClosed")) break;
//                            //textArea.appendText(str + "\n");
//                            Label message = new Label(str);
//                            VBox messageBox = new VBox(message);
//                            listView.getItems().add(messageBox);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void createWindow() throws IOException {
//        MiniStage miniStage = new MiniStage();
//        miniStage.show();
//    }

    // отправка сообщений
    public void sendMsg() {

        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();

            //sound();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // метод для авторизации
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
