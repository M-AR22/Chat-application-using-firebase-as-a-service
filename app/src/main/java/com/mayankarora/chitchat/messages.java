package com.mayankarora.chitchat;

public class messages {
    private String message,type,from;
    private long time;
    private boolean seen;

    public messages() {
    }

    public messages(String message, boolean seen, String type, long time, String from) {
        this.message = message;
        this.seen = seen;
        this.type = type;
        this.time = time;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
