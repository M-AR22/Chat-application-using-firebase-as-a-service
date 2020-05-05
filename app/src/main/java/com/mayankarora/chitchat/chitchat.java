package com.mayankarora.chitchat;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class chitchat extends Application {

    private DatabaseReference userDatabase;
    private FirebaseAuth mauth;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mauth=FirebaseAuth.getInstance();

        if(mauth.getCurrentUser()!=null){
            userDatabase=FirebaseDatabase.getInstance().getReference()
                    .child("users").child(mauth.getCurrentUser().getUid());
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null)
                        userDatabase.child("online").onDisconnect().setValue(DateFormat.getDateTimeInstance().format(new Date()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
