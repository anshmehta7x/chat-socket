package org.example.clientside;

public class Main {
    public static void main(String[] args) {
        ChatClient client = new ChatClient("ansh","1234",5000);
        client.sendBroadcast("OKAY7SFIUHJS");
        client.sendBroadcast("WHAT THE HELLY");
    }
}
