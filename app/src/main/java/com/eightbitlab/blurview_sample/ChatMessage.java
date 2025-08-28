package com.eightbitlab.blurview_sample;

public class ChatMessage {
    private String message;
    private boolean isSentByUser;
    private long timestamp;

    public ChatMessage(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public long getTimestamp() {
        return timestamp;
    }
}