package com.example.suxiongye.smartlib.network;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.suxiongye.smartlib.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 该类实现一个简单的搜索测试，实现Runnable接口，因为网络相关功能不能直接在主线程调用，否则会发生死锁
 * Created by suxiongye on 7/1/16.
 */
public class LibSearch implements Runnable{
    //网络请求相关
    HttpURLConnection conn;
    //搜索结果
    String resultData = "";
    //搜索字段
    String searchContent = "";
    String urlString = "http://libaleph.bjut.edu.cn:8991/F?" +
            "func=find-m&find_code=WTI&adjacent=Y&x=0&" +
            "y=0&find_base=BGD01&filter_code_1=WLN&filter_request_1=&" +
            "filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFT" +
            "&filter_request_4=&filter_code_5=WSL&filter_request_5=LZLT";
    URL url;
    InputStream is;


    @Override
    public void run() {
        getTest();
    }


    public String getTest() {
        try {
            resultData = "";
            url = new URL(urlString+"&request="+searchContent);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine  = "";
            while((inputLine = bufferedReader.readLine()) != null){
                if (inputLine!="")
                resultData += inputLine + "\n";
            }
            is.close();
            isr.close();
            bufferedReader.close();
        } catch (Exception e) {
            Log.e("ex", e.toString());
        }

        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("result", resultData);
        msg.setData(b);
        MainActivity.handler.sendMessage(msg);
        return resultData;
    }

    /**
     * 设置需要搜素的字段
     * @param string
     */
    public void setSearchContent(String string){
        this.searchContent = string;
    }
}
