package inc.bs.chataroo.posts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import inc.bs.chataroo.R;
import inc.bs.chataroo.models.Posts;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MessageViewHolder>{


    private List<Posts> mMessageList;
    Context context;
    Posts c;
    int plus,minus;
    DatabaseReference root;

    public PostsAdapter(List<Posts> mMessageList , Context context) {
        this.mMessageList = mMessageList;
        this.context=context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType )
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_single_list_view ,parent, false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView postTitle,count;
        public TextView postsContent;
        public ImageView messageImage,plus,minus;

        public MessageViewHolder(View view) {
            super(view);

            count = view.findViewById(R.id.post_single_vote_count_1);
            postTitle = (TextView) view.findViewById(R.id.post_single_title_1);
            postsContent = (TextView) view.findViewById(R.id.post_single_text_1);
            messageImage = (ImageView) view.findViewById(R.id.post_single_image_1);
            plus = (ImageView) view.findViewById(R.id.posts_plus);
            minus = (ImageView) view.findViewById(R.id.posts_minus);


        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {


        c = mMessageList.get(i);

        viewHolder.postTitle.setText(c.getTitle());

        viewHolder.postsContent.setText(c.getContent());

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vote(c.getPostid());
                Toast.makeText(context,"You decided to vote negative",Toast.LENGTH_SHORT).show();
                viewHolder.minus.setImageResource(R.drawable.ic_minus_box_blue_24dp);

            }
        });
        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vote(c.getPostid());
                viewHolder.plus.setImageResource(R.drawable.ic_add_box_orange_24dp);
                Toast.makeText(context,"You decided to vote positive",Toast.LENGTH_SHORT).show();
            }
        });

        //  plus=Integer.valueOf(c.getPlus());

        if(c.getType().equals("image"))
        {
            viewHolder.postsContent.setVisibility(View.GONE);

            viewHolder.messageImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(c.getContent()).into(viewHolder.messageImage);
        }

        viewHolder.count.setText(c.getCount());
    }

    void vote(String i)
    {/*
        root=FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(i);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
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
