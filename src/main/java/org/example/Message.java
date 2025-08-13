package org.example;

import java.time.LocalDateTime;

public class Message {
    LocalDateTime messageDateTime;
    String sender;
    String contents;
    boolean privateMessage;

    public Message(String sender, String contents){
        this.messageDateTime = LocalDateTime.now();
        this.sender = sender;
        this.contents = contents;
        this.privateMessage = false;
    }

    public Message(String sender, String receiver, String contents){
        this.messageDateTime = LocalDateTime.now();
        this.sender = sender;
        this.contents = contents;
        this.privateMessage = true;
    }

    public String stringify(){
        String date = messageDateTime.toLocalDate().toString();
        String time = messageDateTime.toLocalTime().toString();
        if(this.privateMessage){
            return "[PRIVATE] " + date + " " + time + " | " + this.sender + " : " + this.contents;

        }
        return date + " " + time + " | " + this.sender + " : " + this.contents;
    }
}
