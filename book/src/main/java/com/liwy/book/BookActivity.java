package com.liwy.book;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mzule.activityrouter.annotation.Router;

@Router("book")
public class BookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity_book);
    }
}
