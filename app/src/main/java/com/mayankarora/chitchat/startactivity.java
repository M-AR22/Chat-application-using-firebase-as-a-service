package com.mayankarora.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class startactivity extends AppCompatActivity {

    private Button regBtn;
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);

        regBtn=(Button)findViewById(R.id.start_reg_button);
        loginbtn=(Button)findViewById(R.id.start_login_btn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent=new Intent(startactivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIntent=new Intent(startactivity.this,LoginActivity.class);
                startActivity(logIntent);
            }
        });
    }
}
