package inc.bs.chataroo.group_chat.pub;

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

public class NewGroupChatPublic extends AppCompatActivity {

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
    private String title,content,uid,myuserName,push_id,gid;
    byte[] thumb_byte;
    Bitmap ThumbImage;
    Uri imageuri,thumburi;
    private DatabaseReference mRootRef;
    private DatabaseReference chatRef;
    Map messageMap2;
    String category="topic";

    InputStream comstream;
    private File actualImage;
    private File compressedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_public_new_group);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mRootRef=databaseReference;

        uid =  getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);
        category = getIntent().getStringExtra("cat");


        btnNext = (Button) findViewById(R.id.nextbtnpostg);
        btnuploadPic = (Button) findViewById(R.id.post_uploadg);
        contentImage = (ImageView) findViewById(R.id.ImageViewNewPostg);
        inputtitle = (EditText) findViewById(R.id.Title_postg);
        inputcontent = (EditText) findViewById(R.id.Post_Contentg);
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

                if (TextUtils.isEmpty(title)||title.length()<3) {
                    Toast.makeText(getApplicationContext(), "Enter a title greater than 3 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!hasImageChanged) {
                    if (TextUtils.isEmpty(content) || content.length() < 5) {
                        Toast.makeText(getApplicationContext(), "Enter description with greater than 5 characters!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                progressBar.setVisibility(View.VISIBLE);

                userinfo= mRootRef.child("groups").child("public").child("names").child(title.toLowerCase());
                userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("name")) {
                            uploadData();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Group name already taken\nChoose a different groupName", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }});
        //    Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT).show();

    }

    protected void uploadData()
    {
        final Map values = new HashMap();

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat").child("group").child("public").child(title);

        DatabaseReference gid_push = mRootRef.child("groups").child("public").child("ids").push();
        gid = gid_push.getKey();

        Map messageMap = new HashMap();
        messageMap.put("created", ServerValue.TIMESTAMP);
        messageMap.put("author",myuserName);
        messageMap.put("gid",gid);
        messageMap.put("name",title);
        messageMap.put("category",category);
        messageMap.put("description",content);


        mRootRef.child("groups").child("public").child("ids").child(gid).setValue(messageMap);
        mRootRef.child("groups").child("public").child("names").child(title.toLowerCase()).setValue(messageMap);


        messageMap2 = new HashMap();
        messageMap2.put("name", title);
        messageMap2.put("author",myuserName);
        messageMap2.put("category",category);
        messageMap2.put("gid",gid);
        messageMap2.put("description",content);


        if (hasImageChanged) {
            contentImage.setDrawingCacheEnabled(true);
            contentImage.buildDrawingCache();

            Bitmap bitmap = contentImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            final String imageRef = UUID.randomUUID().toString() + ".jpg";
            StorageReference mountainsRef = storageReference.child("images").child(uid).child("posts").child("public").child(imageRef);
            final StorageReference mountainsRef2 = storageReference.child("images").child(uid).child("posts").child("public").child("thumbails").child(imageRef);

            thumb_byte=data;


            mountainsRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();
                        messageMap2.put("image",download_url);

                        mountainsRef2.putStream(comstream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {

                                    String download_url2 = task.getResult().getDownloadUrl().toString();

                                    messageMap2.put("image_thumbnail",download_url2);

                                    chatRef.setValue(messageMap2);


                                    Toast.makeText(getApplicationContext(),"SUCCESFULLY CREATED A GROUP!!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }}});

        }
        else {


            messageMap2.put("image","noimage");
            messageMap2.put("image_thumbnail","noimage");

            chatRef.setValue(messageMap2);


            Toast.makeText(getApplicationContext(),"SUCCESFULLY UPLOADED A POST!!",Toast.LENGTH_SHORT).show();
            finish();
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