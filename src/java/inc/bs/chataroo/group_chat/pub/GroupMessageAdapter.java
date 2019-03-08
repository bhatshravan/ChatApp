package inc.bs.chataroo.group_chat.pub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.bs.chataroo.R;
import inc.bs.chataroo.activiti.SinglePhotoView;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.models.GroupMessagesPublic;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder>{


    private List<GroupMessagesPublic> mMessageList;
    private DatabaseReference mUserDatabase;
    private String currentUser;
    Context context;

    public GroupMessageAdapter(List<GroupMessagesPublic> mMessageList , Context context,String currentuser) {

        this.mMessageList = mMessageList;
        this.context=context;
        this.currentUser=currentuser;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_public_single ,parent, false);

        return new MessageViewHolder(v);

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText,messageText_me;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage,messageImage_me;
        public TextView time,time_me;
        public RelativeLayout rel,rel_me;
        public RelativeLayout rltotal;

        public MessageViewHolder(View view) {
            super(view);

            rltotal= view.findViewById(R.id.message_single_layout);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            time = view.findViewById(R.id.time_text_layout);

            time_me = view.findViewById(R.id.group_public_message_time_me);
            messageImage_me=view.findViewById(R.id.group_public_message_image_me);
            messageText_me=view.findViewById(R.id.group_public_message_body_me);

            rel=view.findViewById(R.id.group_pub_relat);
            rel_me=view.findViewById(R.id.group_pub_me_relat);

        }
        private void onLongClick() {
            itemView.showContextMenu();
        }

    }

    String imageUrl="";
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int i) {


        GroupMessagesPublic c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();

        String url="";

        imageUrl=c.getMessage();

        //SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a dd-MM");
        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a");
        String n = sfd.format(new Date(c.getTime()));

        //  Picasso.with(context).load(c.getImage()).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(viewHolder.messageImage);


       // if(false) {
            if (!(from_user.equals(currentUser))) {
                //Load name
                viewHolder.displayName.setText(c.getName());

                url="https://firebasestorage.googleapis.com/v0/b/chataroo-b026b.appspot.com/o/thumbs%2F"+from_user+"%2Fprofile_images%2F"+from_user+".jpg?alt=media";

                //Load user profile image
                Picasso.with(context).load(url).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);
                //Load time
                viewHolder.time.setText(n);

                //Check if text or image
                if (message_type.equals("text")) {
                    viewHolder.messageText.setText(c.getMessage());
                    viewHolder.messageImage.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.messageText.setVisibility(View.GONE);
                    Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.messageImage);
                    viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, SinglePhotoView.class);
                            i.putExtra("url",imageUrl);
                            context.startActivity(i);

                        }
                    });
                    viewHolder.messageImage.setVisibility(View.VISIBLE);

                }

            } else {

                viewHolder.rel.setVisibility(View.GONE);
                viewHolder.rel_me.setVisibility(View.VISIBLE);

                //Load time
                viewHolder.time_me.setText(n);
                viewHolder.time_me.setVisibility(View.VISIBLE);
                //Check if text or image
                if (message_type.equals("text")) {
                    viewHolder.messageText_me.setText(c.getMessage());
                    viewHolder.messageImage_me.setVisibility(View.GONE);
                } else {
                    viewHolder.messageText_me.setVisibility(View.GONE);
                    Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.messageImage_me);
                    viewHolder.messageImage_me.setVisibility(View.VISIBLE);
                    viewHolder.messageImage_me.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, SinglePhotoView.class);
                            i.putExtra("url",imageUrl);
                            context.startActivity(i);

                        }
                    });
                }

            }
       // }
        //Load name
       /* viewHolder.displayName.setText(c.getName());

        url="https://firebasestorage.googleapis.com/v0/b/chataroo-b026b.appspot.com/o/thumbs%2F"+from_user+"%2Fprofile_images%2F"+from_user+".jpg?alt=media";

        //Load user profile image
        Picasso.with(context).load(url).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);
        //Load time
        viewHolder.time.setText(n);

        //Check if text or image
        if (message_type.equals("text")) {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.messageText.setVisibility(View.GONE);
            Picasso.with(context).load(c.getMessage()).placeholder(R.mipmap.ic_launcher).into(viewHolder.messageImage);
        }*/

        viewHolder.rltotal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(viewHolder.getPosition());

                SharedPreferences pref = context.getSharedPreferences(Constants.prefs, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("frl",from_user);
                editor.putString("frname",mMessageList.get(i).getName());
                editor.apply();

                return false;
            }
        });
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
    private int position;

    public int getPosition() {
        return position;
    }
   public void setPosition(int position) {
        this.position = position;
    }

}
