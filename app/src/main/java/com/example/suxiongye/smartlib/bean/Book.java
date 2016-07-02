package com.example.suxiongye.smartlib.bean;

/**
 * 本类用于记录书本基本信息
 * Created by suxiongye on 7/3/16.
 */
public class Book {
    //书名，作者，索书号，总数，借出数
    private String name;
    private String author;
    private String id;
    private String time;
    private String num_all;
    private String num_brought;

    @Override
    public String toString() {
        return name+"\t"+author+"\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum_all() {
        return num_all;
    }

    public void setNum_all(String num_all) {
        this.num_all = num_all;
    }

    public String getNum_brought() {
        return num_brought;
    }

    public void setNum_brought(String num_brought) {
        this.num_brought = num_brought;
    }
}
