package com.example.bmic;

public class Model {

    String id, result, comment, userId,date;

    public Model() {

    }

    public Model(String id, String result, String comment, String userId, String date) {
        this.id = id;
        this.result = result;
        this.comment = comment;
        this.userId = userId;
        this.date =  date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result =  result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
