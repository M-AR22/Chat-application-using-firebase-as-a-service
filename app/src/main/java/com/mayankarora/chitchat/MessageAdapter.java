package com.mayankarora.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<messages> MessageList;
    private FirebaseUser fuser;
    private final int MESS_TYPE_RIGHT=1;
    private final int MESS_TYPE_LEFT=0;

    public MessageAdapter(List<messages> messageList) {
        MessageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType==MESS_TYPE_RIGHT){
             v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_sender,parent,false);
        }
        else{
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        }
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        messages m=MessageList.get(position);
        holder.usermessage.setText(m.getMessage());

    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView usermessage;
        public TextView seenstatus;
        public ImageButton send;
        public CircleImageView profileImage;
        public View view;

        public MessageViewHolder(View view){
            super(view);
            this.view=view;

            usermessage=view.findViewById(R.id.message_text);
            seenstatus=view.findViewById(R.id.message_seen);
            send=view.findViewById(R.id.send_chat);
            profileImage=view.findViewById(R.id.chatuser_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=fuser.getUid();
        if(MessageList.get(position).getFrom().equals(uid))
            return MESS_TYPE_RIGHT;
        else
            return MESS_TYPE_LEFT;
    }
}
