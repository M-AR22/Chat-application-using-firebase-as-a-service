package com.mayankarora.chitchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView friendsList;

    private DatabaseReference friendsDatabase,usersDatabase;
    private FirebaseAuth mauth;

    private View view;
    private String current_user;
    FirebaseRecyclerAdapter<friends,FriendsFragment.friendsViewHolder>firebaseRecyclerAdapter;
    private String onlinestatus;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_friends, container, false);
        friendsList=view.findViewById(R.id.friends_list);
        mauth=FirebaseAuth.getInstance();
        current_user=mauth.getCurrentUser().getUid();
        friendsDatabase= FirebaseDatabase.getInstance().getReference().child("friends").child(current_user);
        friendsDatabase.keepSynced(true);
        usersDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        usersDatabase.keepSynced(true);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<friends> options =
                new FirebaseRecyclerOptions.Builder<friends>()
                        .setQuery(friendsDatabase, friends.class)
                        .build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<friends, friendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final friendsViewHolder friendsViewHolder, int i, @NonNull friends friends) {
                friendsViewHolder.setDate(friends.getDate());
                final String user_id=getRef(i).getKey();
                usersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name=dataSnapshot.child("name").getValue().toString();
                        String thumbImage=dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")){
                            onlinestatus=dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setOnlineStatus(onlinestatus);
                        }

                        friendsViewHolder.setName(name);
                        friendsViewHolder.setThumbImage(thumbImage);

                        friendsViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //***********alert dialog**********
                                Log.v("clicked","insideOnclick");
                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Click event for each item
                                        if(i==0){
                                            Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("user_id",user_id);
                                            startActivity(profileIntent);
                                        }
                                        else
                                        {
                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",user_id);
                                            chatIntent.putExtra("username",name);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public friendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_layout,parent,false);
                return new FriendsFragment.friendsViewHolder(view);
            }
        };
        friendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public class friendsViewHolder extends RecyclerView.ViewHolder{
        View view;
        public friendsViewHolder(View itemview){
            super(itemview);
            view=itemview;
        }

        public void setDate(String date) {
            TextView Date=view.findViewById(R.id.user_status);
            Date.setText(date);
        }

        public void setName(String username){
            TextView name=view.findViewById(R.id.user_name);
            name.setText(username);
        }
        public void setThumbImage(String thumb_image){
            CircleImageView image=view.findViewById(R.id.users_profile_image);
            if(!thumb_image.equals("default"))
                Picasso.get().load(thumb_image).placeholder(R.drawable.blank_avatar).into(image);
        }

        public void setOnlineStatus(String onlinestatus) {
            ImageView onlineIcon=view.findViewById(R.id.online_icon);
            if(onlinestatus.equals("true")){
                onlineIcon.setVisibility(View.VISIBLE);
            }
            else
                onlineIcon.setVisibility(View.INVISIBLE);
        }
    }


}
