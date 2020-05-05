package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

public class LoginActivity extends AppCompatActivity {

    private Button loginbtn;
    private TextInputLayout em;
    private TextInputLayout pass;
    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private ImageButton showPassword;
    private int FLAG=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginbtn=findViewById(R.id.login_btn);
        em=findViewById(R.id.login_email_btn);
        pass=findViewById(R.id.login_password_btn);
        progress=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        showPassword=findViewById(R.id.login_show_hide_password);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=em.getEditText().getText().toString();
                String password=pass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    progress.setTitle("Logging In");
                    progress.setMessage("Please wait while we check your credentials");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    loginUser(email,password);
                }
            }
        });
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FLAG==0){
                    pass.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    FLAG=1;
                    showPassword.setImageResource(R.drawable.visibility);
                }
                else{
                    pass.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    FLAG=0;
                    showPassword.setImageResource(R.drawable.visibility_off);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progress.dismiss();
                    Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                    //to clear the previous intent so that while pressing back button we may not see the used intents
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    progress.hide();
                    Toast.makeText(LoginActivity.this, "You have got some error check the credentials again ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
