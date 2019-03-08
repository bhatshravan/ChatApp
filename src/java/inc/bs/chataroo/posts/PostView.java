package inc.bs.chataroo.posts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import inc.bs.chataroo.R;
import inc.bs.chataroo.misc.Constants;

/**
 * Created by Shravan on 15-01-2018.
 */


public class PostView extends AppCompatActivity {

    String postType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_public_chat);


        postType = getIntent().getStringExtra(Constants.Postauthor);

        if(postType.equals("image"));
        else if(postType.equals("gif"));
        else if(postType.equals("link"));
        else if(postType.equals("text"));

    }
}
