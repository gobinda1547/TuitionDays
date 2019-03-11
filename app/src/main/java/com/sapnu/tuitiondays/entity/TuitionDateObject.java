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

    public String getComment() {
        return comment;
    }
}
