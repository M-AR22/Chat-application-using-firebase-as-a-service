package com.mayankarora.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatUser;
    private Toolbar chatToolbar;
    private DatabaseReference userDatabase;
    private String username;
    private TextView nameuser;
    private TextView lastseen;
    String onlineStatus;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    String currentUserId;
    private ImageButton addButton,sendButton;
    private EditText chatMessage;
    private RecyclerView messageList;
    private List<messages>messlist=new ArrayList<>();
    private LinearLayoutManager mlayoutManager;
    private MessageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser=getIntent().getStringExtra("user_id");
        username=getIntent().getStringExtra("username");
        userDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        chatToolbar=findViewById(R.id.chatActivity_appbar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_actionbar,null);
        actionBar.setCustomView(action_bar_view);

        addButton=findViewById(R.id.add_button);
        sendButton=findViewById(R.id.send_button);
        chatMessage=findViewById(R.id.chat_text);
        messageList=findViewById(R.id.message_list);
        mlayoutManager=new LinearLayoutManager(this);
        mAdapter=new MessageAdapter(messlist);

        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(mlayoutManager);
        messageList.setAdapter(mAdapter);


        nameuser=findViewById(R.id.chat_user_name);
        lastseen=findViewById(R.id.chat_last_seen);
        lastseen.setSelected(true);
        profileImage=findViewById(R.id.user_chat_image);

        loadMessages();

        nameuser.setText(username);
        userDatabase.child("users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("online"))
                {
                    onlineStatus=dataSnapshot.child("online").getValue().toString();
                    if(onlineStatus.equals("true"))
                        lastseen.setText("online");
                    else{
                        /*GetTimeAgo gettimeAgo=new GetTimeAgo();
                        long lastTime=Long.parseLong(onlineStatus);
                        String lastSeentime=gettimeAgo.getTimeAgo(lastTime,getApplicationContext());*/
                        lastseen.setText("Last seen at "+onlineStatus);
                    }

                }
                String image=dataSnapshot.child("thumb_image").getValue().toString();

                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.blank_avatar).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userDatabase.child("chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatUser)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap=new HashMap();
                    chatUserMap.put("chat/"+currentUserId+"/"+chatUser,chatAddMap);
                    chatUserMap.put("chat/"+chatUser+"/"+currentUserId,chatAddMap);

                    userDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.v("chat log",databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendmessage();
            }
        });


    }

    private void loadMessages() {
        userDatabase.child("messages").child(currentUserId).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                messages m=dataSnapshot.getValue(messages.class);
                messlist.add(m);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendmessage() {
        String message=chatMessage.getText().toString();
        String currentuserRef="messages/"+currentUserId+"/"+chatUser;
        String chatUserRef= "messages/"+chatUser+"/"+currentUserId;

        DatabaseReference user_message_push=userDatabase.child("messages")
                .child(currentUserId).child(chatUser).push();
        String push_id=user_message_push.getKey();

        if(!TextUtils.isEmpty(message)){
            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",currentUserId);

            Map messageUserMap=new HashMap();
            messageUserMap.put(currentuserRef+"/"+push_id,messageMap);
            messageUserMap.put(chatUserRef+"/"+push_id,messageMap);

            userDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.v("chat_log",databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
