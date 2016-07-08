package com.fudan.im.bean;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Moment implements Serializable {

    private int id;

    private int userid;

    private String text;

    private byte[] photo;

    private String sendDate;

    public Moment() {
        this.id = -1;
        this.userid = -1;
        this.text = null;
        this.photo = null;
        this.sendDate = null;
    }

    public Moment(int id, int userid, String text) {
        this.id = id;
        this.userid = userid;
        this.text = text;
        this.photo = null;
        this.sendDate = this.formatDate(new Date());
    }

    public Moment(int id, int userid, byte[] photo) {
        this.id = id;
        this.userid = userid;
        this.text = null;
        this.photo = photo;
        this.sendDate = this.formatDate(new Date());
    }

    public Moment(int id, int userid, String text, byte[] photo) {
        this.id = id;
        this.userid = userid;
        this.text = text;
        this.photo = photo;
        this.sendDate = this.formatDate(new Date());
    }

    public Moment(int id, int userid, String text, byte[] photo, Date date) {
        this.id = id;
        this.userid = userid;
        this.text = text;
        this.photo = photo;
        this.sendDate = this.formatDate(date);
    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public int getUserid() { return this.userid; }

    public void setUserid(int userid) { this.userid = userid; }

    public String getText() { return this.text; }

    public void setText(String text) { this.text = text; }

    public byte[] getPhoto() { return this.photo; }

    public void setPhoto(byte[] photo) { this.photo = photo; }

    public String getSendDate() { return this.sendDate; }

    public void setSendDate(String date) { this.sendDate = date; }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

}
