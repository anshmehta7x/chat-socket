package org.example.clientside;

import org.example.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    String name;
    String password;
    int port = 5000;
    Socket socket;
    String URL = "localhost";
    BufferedReader in;
    PrintWriter out;

    public ChatClient(String name, String password, int port) {
        this.name = name;
        this.password = password;
        this.port = port;
        this.connect();
        new Thread(this::listen).start();
        this.startUserInput();
    }

    private void listen() {
        try {
            String str;
            while ((str = this.in.readLine()) != null) {
                if (str.equals("AUTH")) {
                    this.authenticate();
                    if (this.in.readLine().startsWith("Invalid")) {
                        throw new RuntimeException("Invalid Credentials, Disconnecting... ");
                    }
                }
                System.out.println("Received: " + str);
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect() {
        try {
            this.socket = new Socket(URL, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticate() {
        this.out.println(this.name + ":" + this.password);
    }

    public void sendBroadcast(String content) {
        Message msg = new Message(this.name, content);
        this.out.println(msg.stringify());
    }

    public void sendPrivateMessage(String receiver, String content) {
        Message msg = new Message(this.name, receiver, content);
        this.out.println(msg.stringify());
    }

    // Added: lets user type messages in console
    private void startUserInput() {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = console.readLine()) != null) {
                if (input.equalsIgnoreCase("quit")) {
                    this.out.println("QUIT");
                    break;
                }
                if (input.startsWith("@")) { // private message format: @username message
                    int spaceIndex = input.indexOf(' ');
                    if (spaceIndex > 1) {
                        String receiver = input.substring(1, spaceIndex);
                        String content = input.substring(spaceIndex + 1);
                        this.sendPrivateMessage(receiver, content);
                    }
                } else {
                    this.sendBroadcast(input);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading input", e);
        }
    }

}
