package Chitchat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ClientHandler {

//    private ServerMain server;
//
//    private Socket socket;
//    private DataOutputStream out;
//    private DataInputStream in;
//    private String nick;

    Socket socket = null;
    DataInputStream in;
    DataOutputStream out;
    ServerMain server;
    ArrayList<String> blackList;

    public String getNick() {
        return nick;
    }

    String nick;

    public ClientHandler(ServerMain server, Socket socket) {

//        try {
//            this.server = server;
//            this.socket = socket;
//            this.in = new DataInputStream(socket.getInputStream());
//            this.out = new DataOutputStream(socket.getOutputStream());
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        // цикл для авторизации
//                        while (true) {
//                            String str = in.readUTF();
//                            // если приходит сообщение начинающееся с /auth значит пользователь хочет авторизоваться
//                            if(str.startsWith("/auth")) {
//                                String[] tokens = str.split(" ");
//                                // запрашиваем ник в БД
//                                String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
//                                // если ответ не равен null отправляем ответ клиенту о том, что авторизация прошла успешно
//                                if(newNick != null) {
//                                    sendMsg("/authok");
//                                    nick = newNick;
//                                    server.subscribe(ClientHandler.this);
//                                    break;
//                                } else {
//                                    sendMsg("Неверный логин/пароль!");
//                                }
//                            }
//                        }
//                        // цикл для работы
//                        while (true) {
//                            String str = in.readUTF();
//                            if(str.equals("/end")) {
//                                out.writeUTF("/serverClosed");
//                                break;
//                            }
//                            server.broadCastMsg(nick + ": " + str);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            in.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            out.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            socket.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        server.unsubscribe(ClientHandler.this);
//                    }
//                }
//            }).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        sendMsg("/authok");
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        break;
                                    } else {
                                        sendMsg("Учетная запись уже используется!");
                                    }
                                } else {
                                    sendMsg("Неверный логин/пароль!");
                                }
                            }
                        }

                        HashSet<String> s = new LinkedHashSet<>();

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    out.writeUTF("/serverClosed");
                                }
                                if (str.startsWith("/w ")) {
                                    String[] tokens = str.split(" ", 3);
                                    server.sendPersonalMsg(ClientHandler.this, tokens[1], tokens[2]);
                                }
                                if (str.startsWith("/blacklist ")) {
                                    String[] tokens = str.split(" ");
                                    blackList.add(tokens[1]);
                                    sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                                }
                            } else {
                                server.broadCastMsg(ClientHandler.this, nick + ": " + str);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
    }

    // метод для оправки сообщения 1 клиенту
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}