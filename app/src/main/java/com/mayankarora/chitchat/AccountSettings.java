package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettings extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseUser currentuser;
    private CircleImageView image;
    private TextView name;
    private TextView status;
    private Button changeImage;
    private Button changestatus;
    private StorageReference Imagestorage;
    private String uid;
    private ProgressDialog progress;
    private Toolbar toolbar;

    private static final int Gallery_code=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        toolbar=findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        uid=currentuser.getUid();
        name=(TextView)findViewById(R.id.setting_username);
        status=(TextView)findViewById(R.id.setting_userstatus);
        image=(CircleImageView)findViewById(R.id.setting_circleimage);
        changeImage=(Button)findViewById(R.id.setting_change_image_btn);
        changestatus=(Button)findViewById(R.id.setting_change_status_btn);
        database= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        database.keepSynced(true);
        isStoragePermissionGranted();

        Imagestorage= FirebaseStorage.getInstance().getReference();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nam=dataSnapshot.child("name").getValue().toString();
                String imag=dataSnapshot.child("image").getValue().toString();
                String stat=dataSnapshot.child("status").getValue().toString();
                String thumbimage=dataSnapshot.child("thumb_image").getValue().toString();
                name.setText(nam);
                status.setText(stat);
                if(!imag.equals("default"))
                Picasso.get().load(imag).placeholder(R.drawable.blank_avatar).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value=status.getText().toString();
                Intent statusIntent=new Intent(AccountSettings.this,StatusActivity.class);
                statusIntent.putExtra("status_value",status_value);

                startActivity(statusIntent);
                //statusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent=new Intent();
                galleryIntent.setType("Image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent,Gallery_code);
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettings.this);*/

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentuser!=null)
            database.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentuser!=null)
            database.child("online").setValue(DateFormat.getDateTimeInstance().format(new Date()));
    }

    private boolean isStoragePermissionGranted(){
        if(Build.VERSION.SDK_INT>=23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Log.v("Log", "Permission Granted");
                return true;
            }

            else{
                Log.v("Log", "Permission Revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
                return false;
            }
        }
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.v("LOG","Permission:"+permissions[0]+"was"+grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_code&&resultCode==RESULT_OK){
            Uri imageuri=data.getData();
            CropImage.activity(imageuri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progress=new ProgressDialog(AccountSettings.this);
                progress.setTitle("Uploading Image");
                progress.setMessage("Please wait while we upload and process the image");
                progress.setCanceledOnTouchOutside(false);

                Uri resultUri = result.getUri();
                StorageReference filepath=Imagestorage.child("profile_images").child(uid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            String url=task.getResult().getStorage().getDownloadUrl().toString();
                            database.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progress.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
