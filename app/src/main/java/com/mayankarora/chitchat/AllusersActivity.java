package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllusersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView userslist;
    private Query database;
    private FirebaseAuth mauth;
    private DatabaseReference userDatabase;
    FirebaseRecyclerAdapter<users,UsersViewHolder>firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);
        toolbar=findViewById(R.id.allusers_toolbar);
        userslist=findViewById(R.id.users_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userslist.setLayoutManager(new LinearLayoutManager(this));
        //userslist.setHasFixedSize(true);
        mauth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid());
        fetch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        if(mauth.getCurrentUser()!=null)
            userDatabase.child("online").setValue("true");
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mauth.getCurrentUser()!=null)
            userDatabase.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
        if(mauth.getCurrentUser()!=null)
            userDatabase.child("online").setValue(DateFormat.getDateTimeInstance().format(new Date()));
    }

    public void fetch(){
        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(database, users.class)
                        .build();


        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getThumb_image(),getApplicationContext());

                final String user_id=getRef(position).getKey();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent=new Intent(AllusersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout,parent,false);
                return new UsersViewHolder(view);
            }
        };
        userslist.setAdapter(firebaseRecyclerAdapter);
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View view;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setName(String name) {
            TextView username=view.findViewById(R.id.user_name);
            username.setText(name);
        }

        public void setStatus(String status) {
            TextView userstatus=view.findViewById(R.id.user_status);
            userstatus.setText(status);
        }

        public void setImage(String thumb_image, Context context) {
            CircleImageView imag=findViewById(R.id.users_profile_image);
            if(!thumb_image.equals("default"))
            Picasso.get().load(thumb_image).placeholder(R.drawable.blank_avatar).into(imag);
        }
    }
}
