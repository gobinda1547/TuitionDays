package com.sapnu.tuitiondays.entity;

import java.io.Serializable;

public class TuitionDateObject implements Serializable {
    private String date;
    private String comment;

    public TuitionDateObject(String date, String comment) {
        this.date = date;
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
