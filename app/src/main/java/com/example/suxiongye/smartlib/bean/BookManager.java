package com.example.suxiongye.smartlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 本类用于管理记录的书籍
 * Created by suxiongye on 7/3/16.
 */
public class BookManager implements Serializable {
    ArrayList<Book> books;

    public BookManager(){
        books = new ArrayList<Book>();
    }

    /**
     * 获取书本数目
     * @return
     */
    public int getBooksNum(){
        return books.size();
    }

    public void addBook(Book book){
        books.add(book);
    }

    @Override
    public String toString() {
        String string = "";
        for(Book book : books){
            string += book.toString()+"\n";
        }
        return string;
    }
}
