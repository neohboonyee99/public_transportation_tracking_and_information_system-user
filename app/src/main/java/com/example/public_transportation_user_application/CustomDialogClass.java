package com.example.public_transportation_user_application;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

public class CustomDialogClass extends Dialog{
    int custom_layout_id;
    View view;


    public CustomDialogClass(@NonNull Context context, int custom_layout_id) {
        super(context, custom_layout_id);
        this.custom_layout_id= custom_layout_id;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(custom_layout_id);
        view = findViewById(R.id.content);
    }
}
