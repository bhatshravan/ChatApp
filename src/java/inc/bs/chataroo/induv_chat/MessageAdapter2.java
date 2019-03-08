package inc.bs.chataroo.induv_chat;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import inc.bs.chataroo.R;
import inc.bs.chataroo.models.Messages;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter2 extends RecyclerView.Adapter<MessageAdapter2.MessageViewHolder>{

    private Context context;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private String user1,user2,date="",date2="";

    public MessageAdapter2(Context context,List<Messages> mMessageList ,String user1,String user2) {

        this.context = context;
        this.mMessageList = mMessageList;
        this.user1=user1;
        this.user2=user2;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.induv_chat_message_single2 ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messageTextme,datet;
        public TextView time,timeme;
        private ImageView im,imme;
        private ConstraintLayout cs,csme;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.text_message_body);
            messageTextme = (TextView) view.findViewById(R.id.text_message_body_me);
            im = (ImageView) view.findViewById(R.id.message_image_layout);
            imme = (ImageView) view.findViewById(R.id.message_image_layout_me);
            time = view.findViewById(R.id.text_message_time);
            timeme = view.findViewById(R.id.text_message_time_me);


            datet = (TextView) view.findViewById(R.id.datet);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

/*
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Constants.FIREBASE_USERNAME).getValue().toString();
                String image = dataSnapshot.child(Constants.FIREBASE_USERS_THUMB).getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        String n = sfd.format(new Date(c.getTime()));

        SimpleDateFormat sfd2 = new SimpleDateFormat("dd-MM-yyyy");
        date2 = sfd2.format(new Date(c.getTime()));
        if(!date.equals(date2))
        {
            date=date2;
            viewHolder.datet.setText(date);
        }

        if (from_user.equals(current_user_id)) {
            viewHolder.timeme.setText(n);
            viewHolder.timeme.setVisibility(View.VISIBLE);

            if (message_type.equals("text")) {
                viewHolder.messageTextme.setText(c.getMessage());
                viewHolder.messageTextme.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imme.setVisibility(View.VISIBLE);
                Picasso.with(context).load(c.getMessage()).into(viewHolder.imme);
            }
        }
        else
        {
            viewHolder.time.setVisibility(View.VISIBLE);
            viewHolder.time.setText(n);

            if (message_type.equals("text")) {
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageText.setVisibility(View.VISIBLE);
            } else {
                viewHolder.im.setVisibility(View.VISIBLE);
                Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.im);
            }
        }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
