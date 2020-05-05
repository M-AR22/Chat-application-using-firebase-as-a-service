package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout displayname;
    private TextInputLayout email;
    private TextInputLayout password;
    private Button create;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ImageButton showPassword;
    //progress dialog
    private ProgressDialog progress;
    private DatabaseReference database;
    private int FLAG=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        displayname=(TextInputLayout)findViewById(R.id.reg_display_name);
        email=(TextInputLayout)findViewById(R.id.reg_email);
        password=(TextInputLayout)findViewById(R.id.reg_password);
        create=(Button)findViewById(R.id.reg_create_button);
        mtoolbar=(Toolbar)findViewById(R.id.reg_appbar);
        showPassword=findViewById(R.id.show_hide_password);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress=new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disname=displayname.getEditText().getText().toString();
                String em=email.getEditText().getText().toString();
                String pass=password.getEditText().getText().toString();
                if(!TextUtils.isEmpty(disname)&&!TextUtils.isEmpty(em)&&!TextUtils.isEmpty(pass)){
                    progress.setTitle("Registering user");
                    progress.setMessage("Please wait while create your Account");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    register_user(disname,em,pass);
                }

            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FLAG==0){
                    password.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    FLAG=1;
                    showPassword.setImageResource(R.drawable.visibility);
                }
                else{
                    password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    FLAG=0;
                    showPassword.setImageResource(R.drawable.visibility_off);
                }
            }
        });

    }

    private void register_user(final String disname, String em, String pass) {
        mAuth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=current_user.getUid();
                    database= FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    HashMap<String,String> user=new HashMap<>();
                    user.put("name",disname);
                    user.put("status","Hi there i'm using chat App");
                    user.put("image","default");
                    user.put("thumb_image","default");
                    database.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                progress.dismiss();
                                Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                //to clear the previous intent so that while pressing back button we may not see the used intents
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });


                }
                else{
                    progress.hide();
                    Toast.makeText(RegisterActivity.this, "you got some error", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
