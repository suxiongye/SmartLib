package com.example.suxiongye.smartlib;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.suxiongye.smartlib.bean.BookManager;
import com.example.suxiongye.smartlib.network.LibSearch;

public class MainActivity extends AppCompatActivity {

    private Button button_get;
    //搜索结果内容
    private TextView tv_content;
    //搜索字段
    private EditText editText_search;
    //搜索结果
    private EditText editText_bookNum;

    public static Handler handler;
    public Thread searchThread;
    private LibSearch libSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI 初始化
        button_get = (Button) findViewById(R.id.button_get);
        tv_content = (TextView) findViewById(R.id.textView_content);
        editText_search = (EditText) findViewById(R.id.editText_search);
        editText_bookNum = (EditText) findViewById(R.id.editText_bookNum);

        //搜索类
        libSearch = new LibSearch();

        //搜索UI监听
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                //获取搜索结果并更新UI
                BookManager bookManager = (BookManager) b.getSerializable("bookManager");

                if (bookManager != null) {
                    //显示搜索数目
                    MainActivity.this.editText_bookNum.setText("搜索记录数："+bookManager.getBooksNum());
                    MainActivity.this.editText_bookNum.setVisibility(View.VISIBLE);
                    //显示具体内容
                    MainActivity.this.tv_content.setText(bookManager.toString());
                } else MainActivity.this.tv_content.setText("搜索记录为空！");

            }
        };


        //设置Get按钮
        button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("search", editText_search.getText().toString());
                tv_content.setText("搜索中，请稍等。。。");
                //设置搜索字段
                libSearch.setSearchContent(editText_search.getText().toString());

                editText_bookNum.setVisibility(View.INVISIBLE);
                //开始搜索
                searchThread = new Thread(libSearch);
                searchThread.start();
            }
        });
    }
}
