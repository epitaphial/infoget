package com.example.infoget;


import android.content.Intent;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] arrayList = {"Hardware","Telephone","Apps","Activities"};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        ((ListView) findViewById(R.id.list)).setAdapter(adapter);
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                    intent.putExtra("item",String.valueOf(i));
                    startActivity(intent);
                }
        );

    }
}