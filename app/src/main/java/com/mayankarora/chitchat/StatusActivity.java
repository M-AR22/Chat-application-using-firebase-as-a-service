package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout status;
    private Button savebtn;
    private DatabaseReference database;
    private FirebaseUser user;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar=(Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        status=(TextInputLayout)findViewById(R.id.text);
        savebtn=(Button)findViewById(R.id.status_savechanges_btn);
        String status_value=getIntent().getStringExtra("status_value");
        user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();

        status.getEditText().setText(status_value);

        database= FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st=status.getEditText().getText().toString();
                progress=new ProgressDialog(StatusActivity.this);
                progress.setTitle("Saving Changes");
                progress.setMessage("Please wait while we save the changes");
                progress.show();
                database.child("status").setValue(st).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progress.dismiss();
                            Intent status=new Intent(StatusActivity.this,AccountSettings.class);

                            startActivity(status);
                            finish();
                            //status.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        else
                        {
                            progress.hide();
                            Toast.makeText(StatusActivity.this, "There was error in saving changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
