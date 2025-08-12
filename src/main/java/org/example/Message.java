package org.example;

import java.time.LocalDateTime;

public class Message {
    LocalDateTime messageDateTime;
    String sender;
    String contents;
    boolean privateMessage;

    public Message(String sender, String contents, boolean isPrivate){
        this.messageDateTime = LocalDateTime.now();
        this.sender = sender;
        this.contents = contents;
        this.privateMessage = isPrivate;
    }

    public String stringify(){
        return this.messageDateTime.toString() + " " + this.sender + " " + this.contents;
    }
}
