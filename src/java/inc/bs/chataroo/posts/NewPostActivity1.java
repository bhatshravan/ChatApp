package inc.bs.chataroo.posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import inc.bs.chataroo.R;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.FileUtil;

/**
 * Created by Shravan on 26-12-2017.
 */

public class NewPostActivity1 extends AppCompatActivity {

    private EditText inputtitle, inputcontent;
    ImageView contentImage;
    private Button btnNext,btnuploadPic;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference,mountainsRef2;
    private DatabaseReference databaseReference,userinfo;
    private boolean hasImageChanged = false;
    private static final int SELECT_PHOTO = 1;
    private String title,content,uid,myuserName,push_id;
    byte[] thumb_byte;
    Bitmap ThumbImage;
    Uri imageuri,thumburi;

    InputStream comstream;
    private File actualImage;
    private File compressedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_new_post1);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        uid =  getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);


        btnNext = (Button) findViewById(R.id.nextbtnpost1);
        btnuploadPic = (Button) findViewById(R.id.post_upload1);
        contentImage = (ImageView) findViewById(R.id.ImageViewNewPost1);
        inputtitle = (EditText) findViewById(R.id.Title_post1);
        inputcontent = (EditText) findViewById(R.id.Post_Content1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnuploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = inputtitle.getText().toString().trim();
                content = inputcontent.getText().toString().trim();

                if (TextUtils.isEmpty(title)||title.length()<5) {
                    Toast.makeText(getApplicationContext(), "Enter a title greater than 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!hasImageChanged) {
                    if (TextUtils.isEmpty(content) || content.length() < 5) {
                        Toast.makeText(getApplicationContext(), "Enter content with greater than 5 characters!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                progressBar.setVisibility(View.VISIBLE);

                uploadData();

            }});

    }

    protected void uploadData()
    {
        final Map values = new HashMap();

        values.put(Constants.FIREBASE_USERS_UID,uid);
        values.put(Constants.Postauthor,myuserName);
        values.put(Constants.Posttitle,title);
        values.put(Constants.Postviews,"0");
        values.put(Constants.Postplus,"0");
        values.put(Constants.Postminus,"0");
        values.put(Constants.Postcount,"0");
        values.put(Constants.Postcategory,"important");
        values.put(Constants.Posttime,ServerValue.TIMESTAMP);

        if (hasImageChanged) {
            contentImage.setDrawingCacheEnabled(true);
            contentImage.buildDrawingCache();

            Bitmap bitmap = contentImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            final String imageRef = UUID.randomUUID().toString() + ".jpg";
            StorageReference mountainsRef = storageReference.child("images").child(uid).child("posts").child("public").child(imageRef);
            mountainsRef2 = storageReference.child("images").child(uid).child("posts").child("public").child("thumbnail").child(imageRef);

            thumb_byte=data;


            mountainsRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()) {
                        final String download_url = task.getResult().getDownloadUrl().toString();

                        mountainsRef2.putStream(comstream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {


                                    values.put("thumb_image", task.getResult().getDownloadUrl());
                                    //Push the posts
                                    DatabaseReference user_post_push = databaseReference.child("posts").child("public").push();
                                    push_id = user_post_push.getKey();

                                    values.put(Constants.Postcontent, download_url);
                                    values.put(Constants.Postpostid, push_id);
                                    values.put(Constants.Posttype, "image");

                                    databaseReference.child("posts").child("public").child(push_id).setValue(values);

                                    DatabaseReference ss = FirebaseDatabase.getInstance().getReference().child(Constants.Posttime);
                                    ss.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long ss2 = Long.valueOf(dataSnapshot.child(Constants.Posttime).getValue().toString());
                                            ss2 = ss2 * -1;
                                            databaseReference.child("posts").child("public").child(push_id).child(Constants.Posttimeinverse).setValue(ss2);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    Toast.makeText(getApplicationContext(), "WOOT WOOT!! UPLOADED THE POST BOY!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } }
                            });
                    }}});

        }
        else {

            Log.d("Tag","Here new");

            try {
                //Push the posts
                DatabaseReference user_post_push = databaseReference.child("posts").child("public").push();
                push_id = user_post_push.getKey();

                values.put(Constants.Postcontent,inputcontent.getText().toString());
                values.put(Constants.Postpostid, push_id);
                values.put(Constants.Posttype, "text");

                databaseReference.child("posts").child("public").child(push_id).setValue(values);

                Toast.makeText(getApplicationContext(),"WOOT WOOT!! UPLOADED THE POST BOY!!",Toast.LENGTH_SHORT).show();
                finish();
            }
            catch (Exception e)
            {
                Log.d("TAG",e.toString());
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        imageuri=imageUri;
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        contentImage.setImageBitmap(selectedImage);
                        hasImageChanged = true;


                        actualImage = FileUtil.from(this, data.getData());

                        progressBar.setVisibility(View.VISIBLE);

                        compressedImage = new Compressor(this)
                                .setMaxWidth(30)
                                .setMaxHeight(30)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .compressToFile(actualImage);
                        comstream=new FileInputStream(compressedImage);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Error loading image",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}