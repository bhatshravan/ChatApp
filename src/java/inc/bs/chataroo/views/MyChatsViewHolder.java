package inc.bs.chataroo.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.bs.chataroo.R;

/**
 * Created by Shravan on 23-01-2018.
 */
public class MyChatsViewHolder extends RecyclerView.ViewHolder {

    public View mView;
    String umm="https://firebasestorage.googleapis.com/v0/b/chataroo-b026b.appspot.com/o/images%2F2UuDi6OeZjck6oD3Rui2xF1A4Sy1%2Fprofile_images%2F2UuDi6OeZjck6oD3Rui2xF1A4Sy1.jpg?alt=media&token=996884d6-baeb-4999-b159-e54ced13c8fc";
    StorageReference storageReference;

    public MyChatsViewHolder(View itemView) {
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

    public void setUserImage(String uid, Context ctx){
        CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        umm="https://firebasestorage.googleapis.com/v0/b/chataroo-b026b.appspot.com/o/thumbs%"+uid+"%2Fprofile_images%"+uid+".jpg?alt=media";
        Picasso.with(ctx).load(umm).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(userImageView);    //   Picasso.with(ctx).load(R.drawable.default_avatar).error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(userImageView);
    }

}
