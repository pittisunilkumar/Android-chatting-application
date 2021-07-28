package com.example.gochats.Adapter;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.R;
import com.example.gochats.modes.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages>userMessageslist;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    public MessageAdapter(List<Messages>userMessageslist)
    {
        this.userMessageslist = userMessageslist;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText,receiverMessageText,sendertime,recevertime;
        public CircularImageView receiverprofileImage;
        public ImageView messagesenderpic,messagereceverpic;
        public VideoView sendervideo,recevervideo;
        public ImageView vedioplaybtn,videopause;


        public MessageViewHolder(View iteamView)
        {
            super((iteamView));
            senderMessageText = (TextView)iteamView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView)iteamView.findViewById(R.id.recevier_message_text);
            receiverprofileImage = (CircularImageView) iteamView.findViewById(R.id.message_profile77);
            messagesenderpic = (ImageView) iteamView.findViewById(R.id.message_sender_image_view);
            messagereceverpic = (ImageView) iteamView.findViewById(R.id.message_recevier_image_view);
            sendertime =(TextView)iteamView.findViewById(R.id.sendertime);
            recevertime=(TextView)iteamView.findViewById(R.id.recevertime);
            sendervideo =(VideoView)iteamView.findViewById(R.id.message_sender_vedio_view);
            recevervideo =(VideoView)iteamView.findViewById(R.id.message_recevier_vedio_view);
            vedioplaybtn=(ImageButton)iteamView.findViewById(R.id.vedioplay);
            videopause =(ImageButton)iteamView.findViewById(R.id.vediopause);

        }
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelayout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        String messageSenderId= mAuth.getCurrentUser().getUid();
        Messages messages = userMessageslist.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.hasChild("image"))
                {
                    String receiverImage = snapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.icon_male_ph).into(holder.receiverprofileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverprofileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messagesenderpic.setVisibility(View.GONE);
        holder.messagereceverpic.setVisibility(View.GONE);
        holder.recevertime.setVisibility(View.GONE);
        holder.sendertime.setVisibility(View.GONE);
        holder.sendervideo.setVisibility(View.GONE);
        holder.recevervideo.setVisibility(View.GONE);
        holder.vedioplaybtn.setVisibility(View.GONE);
        holder.videopause.setVisibility(View.GONE);

        if(fromMessageType.equals("text"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.sendertime.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.bg_text_chat_right);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage());
                holder.sendertime.setText(messages.getTime() + " - " + messages.getDate());
            }else
                {
                    holder.receiverprofileImage.setVisibility(View.VISIBLE);
                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.recevertime.setVisibility(View.VISIBLE);
                    holder.receiverMessageText.setBackgroundResource(R.drawable.bg_text_chat_left);
                    holder.receiverMessageText.setTextColor(Color.BLACK);
                    holder.receiverMessageText.setText(messages.getMessage());
                    holder.recevertime.setText(messages.getTime() + " - " + messages.getDate()); }
        }
        else if (fromMessageType.equals("image"))
        {
            if(fromUserID.equals((messageSenderId)))
            {
                holder.messagesenderpic.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messagesenderpic);

            }else
                {
                    holder.receiverprofileImage.setVisibility(View.VISIBLE);
                    holder.messagereceverpic.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(holder.messagereceverpic);
            }
        }else if(fromMessageType.equals("video"))
        {

            if(fromUserID.equals(messageSenderId))
            {
                holder.sendervideo.setVisibility(View.VISIBLE);
                holder.sendervideo.setVideoURI(Uri.parse(messages.getMessage()));
                holder.sendervideo.requestFocus();
                holder.sendervideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.vedioplaybtn.setVisibility(View.VISIBLE);
                    }
                });
                holder.vedioplaybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.sendervideo.start();
                        holder.vedioplaybtn.setVisibility(View.INVISIBLE);
                        holder.videopause.setVisibility(View.VISIBLE);
                    }
                });
                holder.videopause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.sendervideo.pause();
                        holder.vedioplaybtn.setVisibility(View.VISIBLE);
                        holder.videopause.setVisibility(View.GONE);
                    }
                });

            }else
                {

                    holder.recevervideo.setVisibility(View.VISIBLE);
                    holder.recevervideo.setVideoURI(Uri.parse(messages.getMessage()));
                    holder.recevervideo.requestFocus();
                    holder.recevervideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            holder.vedioplaybtn.setVisibility(View.VISIBLE);
                        }
                    });

                    holder.vedioplaybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.recevervideo.start();
                            holder.vedioplaybtn.setVisibility(View.INVISIBLE);
                            holder.videopause.setVisibility(View.VISIBLE);
                        }
                    });
                    holder.videopause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.recevervideo.pause();
                            holder.vedioplaybtn.setVisibility(View.VISIBLE);
                            holder.videopause.setVisibility(View.GONE);
                        }
                    });

                }
        }
    }
    @Override
    public int getItemCount() {
        return userMessageslist.size();
    }
}
