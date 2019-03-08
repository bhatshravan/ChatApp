package inc.bs.chataroo.activiti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import inc.bs.chataroo.R;

/**
 * Created by Shravan on 14-02-2018.
 */

public class SinglePhotoView extends AppCompatActivity {

    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        url=getIntent().getStringExtra("url");

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        //photoView.setImageResource(R.drawable.image);
        Picasso.with(SinglePhotoView.this).load(url).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(photoView);
    }
}