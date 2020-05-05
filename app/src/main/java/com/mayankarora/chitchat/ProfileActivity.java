package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

        private ImageView profileImage;
        private TextView profileName,profileStatus,profileFriendsCount;
        private Button sendRequest,cancelRequest;
        private DatabaseReference database,userDatabase;
        private String current_status;
        private DatabaseReference friendRequestdatabase;
        private FirebaseUser current_user;
        private DatabaseReference friendsDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id=getIntent().getStringExtra("user_id");

        profileImage=findViewById(R.id.profile_image);
        profileName=findViewById(R.id.profile_name);
        profileStatus=findViewById(R.id.profile_status);
        profileFriendsCount=findViewById(R.id.profile_friends);
        sendRequest=findViewById(R.id.friend_request_button);
        cancelRequest=findViewById(R.id.remove_request);
        final LinearLayout.LayoutParams buttonparams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams cancelbtn=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        current_status="not_friends";

        database= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendRequestdatabase=FirebaseDatabase.getInstance().getReference().child("friend_request");
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        userDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(current_user.getUid());
        friendsDatabase=FirebaseDatabase.getInstance().getReference().child("friends");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayname=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                profileName.setText(displayname);
                profileStatus.setText(status);
                if(!image.equals("default"))
                    Picasso.get().load(image).placeholder(R.drawable.blank_avatar).into(profileImage);

                friendRequestdatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("recieved")){
                                current_status="request_recieved";
                                sendRequest.setText("Accept");
                                cancelRequest.setVisibility(View.VISIBLE);
                                cancelRequest.setEnabled(true);
                                sendRequest.setLayoutParams(cancelbtn);
                            }
                            else if(req_type.equals("sent")){
                                current_status="request_sent";
                                sendRequest.setText("cancel Request");
                                cancelRequest.setVisibility(View.INVISIBLE);
                                cancelRequest.setEnabled(false);
                                sendRequest.setLayoutParams(buttonparams);
                            }

                        }

                        else{
                            current_status="not_friends";
                            sendRequest.setText("add friend");
                            cancelRequest.setVisibility(View.VISIBLE);
                            cancelRequest.setEnabled(true);
                            sendRequest.setLayoutParams(cancelbtn);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                friendsDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            current_status="friends";
                            sendRequest.setText("unfriend");
                            sendRequest.setLayoutParams(buttonparams);
                            cancelRequest.setVisibility(View.INVISIBLE);
                            cancelRequest.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest.setEnabled(false);
                if(current_status.equals("not_friends")){
                    friendRequestdatabase.child(current_user.getUid()).child(user_id).child("request_type")
                    .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequestdatabase.child(user_id).child(current_user.getUid()).child("request_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                        sendRequest.setEnabled(true);
                                        current_status="request_sent";
                                        sendRequest.setText("cancel Request");
                                        sendRequest.setLayoutParams(buttonparams);

                                        cancelRequest.setVisibility(View.INVISIBLE);
                                        cancelRequest.setEnabled(false);
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "failed sending request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //*******Request Sent State*************
                if(current_status.equals("request_sent")){
                    friendRequestdatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestdatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    current_status="not_friends";
                                    sendRequest.setEnabled(true);
                                    sendRequest.setText("add friend");

                                    cancelRequest.setVisibility(View.VISIBLE);
                                    cancelRequest.setEnabled(true);
                                    sendRequest.setLayoutParams(cancelbtn);
                                }
                            });
                        }
                    });

                }

                //********Request Recieved State***************
                if(current_status.equals("request_recieved")){
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    friendsDatabase.child(current_user.getUid()).child(user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsDatabase.child(user_id).child(current_user.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendRequestdatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendRequestdatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    current_status="friends";
                                                    sendRequest.setEnabled(true);
                                                    sendRequest.setText("unfriend");
                                                    cancelRequest.setVisibility(View.INVISIBLE);
                                                    cancelRequest.setEnabled(false);
                                                    sendRequest.setLayoutParams(buttonparams);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                //***********Friends state*********
                if(current_status.equals("friends")){
                    friendsDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    current_status="not_friends";
                                    sendRequest.setEnabled(true);
                                    sendRequest.setText("add friend");
                                }
                            });
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(current_user!=null)
            userDatabase.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(current_user!=null)
            userDatabase.child("online").setValue(DateFormat.getDateTimeInstance().format(new Date()));
    }
}
