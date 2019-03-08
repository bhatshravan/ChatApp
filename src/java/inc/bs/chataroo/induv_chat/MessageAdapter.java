package inc.bs.chataroo.induv_chat;

import android.content.Context;
import android.content.Intent;
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
import inc.bs.chataroo.activiti.SinglePhotoView;
import inc.bs.chataroo.models.Messages;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private Context context;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private String user1,user2,date="",date2="",n="",date3="";
    String url="";
    public MessageAdapter(Context context,List<Messages> mMessageList ,String user1,String user2) {

        this.context = context;
        this.mMessageList = mMessageList;
        this.user1=user1;
        this.user2=user2;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.induv_chat_message_single3 ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messageTextMe;
        // public CircleImageView profileImage;
        // public TextView displayName;
        //  public ImageView messageImage;
        public TextView time,timeme,datet;

        private ImageView im,imme;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.text_message_body33);
            messageTextMe = (TextView) view.findViewById(R.id.text_message_bodyme33);
            time = (TextView) view.findViewById(R.id.text_message_time33);
            timeme = (TextView) view.findViewById(R.id.text_message_timeme33);

            datet = (TextView) view.findViewById(R.id.datet);
            //    profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            //    displayName = (TextView) view.findViewById(R.id.name_text_layout);
            //     messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            //     time = view.findViewById(R.id.time_text_layout);
            im = (ImageView) view.findViewById(R.id.message_image_layout33);
            imme = (ImageView) view.findViewById(R.id.message_image_layout_me33);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        //  Log.d("Here",String.valueOf(i));
        String current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a");
        n = sfd.format(new Date(c.getTime()));

        SimpleDateFormat sfd2 = new SimpleDateFormat("dd-MM-yyyy");
        date2 = sfd2.format(new Date(c.getTime()));
        date3=date2;
        if(!date.equals(date3))
        {
            date=date3;
            viewHolder.datet.setText(date);
            viewHolder.datet.setVisibility(View.VISIBLE);
        }


        if(message_type.equals("text"))
        {
            if(from_user.equals(current_user_id)) {
                viewHolder.messageTextMe.setText(c.getMessage());
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageTextMe.setVisibility(View.VISIBLE);

                viewHolder.timeme.setVisibility(View.VISIBLE);
                viewHolder.time.setVisibility(View.GONE);
                viewHolder.timeme.setText(n);
            }
            else
            {
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageTextMe.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);

                viewHolder.time.setText(n);
                viewHolder.timeme.setVisibility(View.GONE);
                viewHolder.time.setVisibility(View.VISIBLE);

            }
        }
        else
        {
            url=c.getMessage();

            if (from_user.equals(current_user_id))
            {
                viewHolder.imme.setVisibility(View.VISIBLE);
                Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.imme);

                viewHolder.imme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, SinglePhotoView.class);
                        i.putExtra("url",url);
                        context.startActivity(i);

                    }
                });
            }
            else
            {
                viewHolder.im.setVisibility(View.VISIBLE);
                Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.im);
                viewHolder.im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, SinglePhotoView.class);
                        i.putExtra("url",url);
                        context.startActivity(i);

                    }
                });

            }
        }

    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
