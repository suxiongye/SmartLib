package com.example.suxiongye.smartlib.network;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.suxiongye.smartlib.MainActivity;
import com.example.suxiongye.smartlib.bean.Book;
import com.example.suxiongye.smartlib.bean.BookManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类实现一个简单的搜索测试，实现Runnable接口，因为网络相关功能不能直接在主线程调用，否则会发生死锁
 * Created by suxiongye on 7/1/16.
 */
public class LibSearch implements Runnable {
    //书籍管理
    BookManager bookManager;
    //网络请求相关
    HttpURLConnection conn;
    //搜索结果
    String resultData = "";
    //搜索字段
    String searchContent = "";
    //此处可把参数包装为HashMap，新增函数构造urlString
    String urlString = "http://libaleph.bjut.edu.cn:8991/F?" +
            "func=find-m&find_code=WTI&adjacent=Y&x=0&" +
            "y=0&find_base=BGD01&filter_code_1=WLN&filter_request_1=&" +
            "filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFT" +
            "&filter_request_4=&filter_code_5=WSL&filter_request_5=LZLT";
    URL url;
    InputStream is;


    @Override
    public void run() {
        getBooks();
    }


    /**
     * 获取搜索结果
     *
     * @return
     */

    public void getBooks() {
        try {
            resultData = "";
            url = new URL(urlString + "&request=" + searchContent);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine = "";

            //设置过滤正则表达式
            Pattern pattern_name = Pattern.compile("format=999>(.*)</a>");
            Pattern pattern_author = Pattern.compile("作者：<td class=content valign=top>.*&nbsp;");
            Pattern pattern_id = Pattern.compile("号：<td class=content valign=top>.*<td class=label>");
            Pattern pattern_time = Pattern.compile("年份：<td class=content valign=top>.*<td class=label>");
            Pattern pattern_numAll = Pattern.compile("馆藏复本.*，已出");
            Pattern pattern_numBrought = Pattern.compile("已出借复本.*，点击查看馆藏");

            String book_name;
            String book_author;
            String book_id;
            String book_time;
            String book_numAll;
            String book_nunBrought;

            Book book = null;
            bookManager = new BookManager();

            //逐行读取html截取有用信息，形成书本对象
            while ((inputLine = bufferedReader.readLine()) != null) {
                book_name = this.getTheBooksFromString(pattern_name, inputLine);
                book_author = this.getBookAuthorFromString(pattern_author, inputLine);
                book_id = this.getBookIdFromString(pattern_id, inputLine);
                book_time = this.getBookTimeFromString(pattern_time, inputLine);
                book_numAll = this.getBookNumAllFromString(pattern_numAll, inputLine);
                book_nunBrought = this.getBookNumBroughtFromString(pattern_numBrought, inputLine);

                //若找到书名则新建一个书对象
                if (book_name != null) {
                    book = new Book();
                    book.setName(book_name);
                }
                if (book_author != null) {
                    book.setAuthor(book_author);
                }
                if (book_id != null) {
                    book.setId(book_id);
                }
                if (book_time != null){
                    book.setTime(book_time);
                }
                if (book_numAll != null){
                    book.setNum_all(book_numAll);
                }
                if (book_nunBrought != null){
                    book.setNum_brought(book_nunBrought);
                    bookManager.addBook(book);
                }
            }
            is.close();
            isr.close();
            bufferedReader.close();
        } catch (Exception e) {
            Log.e("Search error", e.toString());
        }

        //将搜索结果对象化后传回
        Message msg = new Message();
        Bundle b = new Bundle();
        if (bookManager != null)
            b.putSerializable("bookManager", bookManager);
        msg.setData(b);
        MainActivity.handler.sendMessage(msg);
    }

    /**
     * 设置需要搜素的字段
     *
     * @param string
     */
    public void setSearchContent(String string) {
        this.searchContent = string;
    }

    /**
     * 采用pattern定义的正则表达式获取书名
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getTheBooksFromString(Pattern pattern, String string) {
        //根据源代码获取相应字段
        Matcher matcher = pattern.matcher(string);
        String bookTitle = null;
        //若匹配到相应标题，则进行过滤处理
        if (matcher.find()) {
            bookTitle = matcher.group();
            //获取尾部值
            int end = bookTitle.indexOf("</a>");
            //截取题目
            bookTitle = matcher.group().substring(11, end);
        }
        return bookTitle;
    }

    /**
     * 采用pattern定义的正则表达式获取作者
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getBookAuthorFromString(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        String bookAuthor = null;
        if (matcher.find()) {
            bookAuthor = matcher.group();
            int start = bookAuthor.indexOf("valign=top>");
            int end = bookAuthor.indexOf("&nbsp");
            bookAuthor = bookAuthor.substring(start + 11, end);
        }
        return bookAuthor;
    }

    /**
     * 采用pattern截取索书号
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getBookIdFromString(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        String bookId = null;
        if (matcher.find()) {
            bookId = matcher.group();
            int start = bookId.indexOf("top>");
            int end = bookId.indexOf("<td class=label>");
            bookId = bookId.substring(start + 4, end);
        }
        return bookId;
    }

    /**
     * 采用pattern截取年份
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getBookTimeFromString(Pattern pattern, String string){
        Matcher matcher = pattern.matcher(string);
        String bookTime = null;
        if (matcher.find()) {
            bookTime = matcher.group();
            int start = bookTime.indexOf("top>");
            int end = bookTime.indexOf("<td class=label>");
            bookTime = bookTime.substring(start + 4, end);
        }
        return bookTime;
    }

    /**
     * 采用pattern获取馆藏复本
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getBookNumAllFromString(Pattern pattern, String string){
        Matcher matcher = pattern.matcher(string);
        String bookNumAll = null;
        if(matcher.find()){
            bookNumAll = matcher.group().replaceAll(" ", "");
            int start = bookNumAll.indexOf("馆藏复本");
            int end = bookNumAll.indexOf("，已出");
            bookNumAll = bookNumAll.substring(start+5, end);
        }
        return bookNumAll;
    }

    /**
     * 采用pattern获取已借出
     *
     * @param pattern
     * @param string
     * @return
     */
    private String getBookNumBroughtFromString(Pattern pattern, String string){
        Matcher matcher = pattern.matcher(string);
        String bookNumBrought = null;
        if (matcher.find()){
            bookNumBrought = matcher.group().replaceAll(" ","");
            int start = bookNumBrought.indexOf("出借复本:");
            int end = bookNumBrought.indexOf("，点击");
            bookNumBrought = bookNumBrought.substring(start+5, end);
        }
        return bookNumBrought;
    }

}
