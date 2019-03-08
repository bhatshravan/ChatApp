package inc.bs.chataroo.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.bs.chataroo.R;

/**
 * Created by Shravan on 23-01-2018.
 */
public class UsersViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    public UsersViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDisplayName(String name){
        TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);
    }
    public void setUserStatus(String status){
        TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
        userStatusView.setText(status);
    }

    public void setUserImage(String thumb_image, Context ctx){
         CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(userImageView);
     //   Picasso.with(ctx).load(R.drawable.default_avatar).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(userImageView);
    }

}
