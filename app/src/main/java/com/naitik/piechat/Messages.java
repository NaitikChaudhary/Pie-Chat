package com.naitik.piechat;

public class Messages {

    private String message;
    private String type;
    private String from;
    private String id;
    private String thumb_message;
    private String to;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    private boolean seen;
    private long time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String message, boolean seen, String type, long time, String id, String to, String thumb_message) {
        this.message = message;
        this.seen = seen;
        this.type = type;
        this.time = time;
        this.id = id;
        this.to = to;
        this.thumb_message = thumb_message;
    }

    public String getThumb_message() {
        return thumb_message;
    }

    public void setThumb_message(String thumb_message) {
        this.thumb_message = thumb_message;
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

    public Messages() {

    }

}
