package inc.bs.chataroo.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import inc.bs.chataroo.R;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.FileUtil;

/**
 * Created by Shravan on 22-01-2018.
 */

public class Status extends AppCompatActivity {

    private EditText inppassword, inputcontent;
    Button change;
    CircleImageView contentImage;
    private Button btnpassword,btnnewpass;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference,mountainsRef2;
    private DatabaseReference databaseReference,userinfo;
    private boolean hasImageChanged = false;
    private static final int SELECT_PHOTO = 1;
    private String title,content,uid,myuserName,push_id,gid;
    byte[] thumb_byte;

    Uri imageuri;
    private DatabaseReference mRootRef;
    private DatabaseReference chatRef;
    Map messageMap2;

    Button ch2;
    boolean bb1=true;
    InputStream comstream;
     private File actualImage;
    private File compressedImage;
    ProgressDialog mProgressDialog;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_status);


        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        uid =  getIntent().getStringExtra(Constants.FIREBASE_USERS_UID);
        myuserName = getIntent().getStringExtra(Constants.FIREBASE_MYUSERNAME);

        tv=findViewById(R.id.username);
        tv.setText("Username: "+myuserName);

        mRootRef=databaseReference.child("users").child(uid);

        contentImage = (CircleImageView) findViewById(R.id.ImageViewNewPostl);
        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        inputcontent = (EditText) findViewById(R.id.status_change);

        inppassword = findViewById(R.id.change_passworde);
        change=findViewById(R.id.change_password_btn2);

        ch2=findViewById(R.id.change_password_btn);

        progressBar = (ProgressBar) findViewById(R.id.progressBarl);

        userinfo = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USERS).child(user.getUid());
        userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                inputcontent.setText(dataSnapshot.child(Constants.FIREBASE_STATUS).getValue().toString());

                 tv.setText("Username: "+dataSnapshot.child(Constants.FIREBASE_USERNAME).getValue().toString());

                Picasso.with(getApplicationContext()).load(dataSnapshot.child(Constants.FIREBASE_USERS_THUMB).getValue().toString()).placeholder(R.drawable.default_avatar).into(contentImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

            if (user.getProviderId().equals("google.com")) {
                //For linked Google account
                bb1=false;
            }

        }
        if(!bb1)
        {
            ch2.setVisibility(View.GONE);
        }

    }

    public void StatusClick(View view)
    {
        switch (view.getId())
        {
            case R.id.change_picture_btn:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;

            case R.id.change_status_btn:
                content = inputcontent.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                mRootRef.child(Constants.FIREBASE_STATUS).setValue(content);
                Toast.makeText(getApplicationContext(),"Successfully updated status",Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                break;
            case R.id.change_password_btn:
                if(bb1)
                {
                    inppassword.setVisibility(View.VISIBLE);
                    change.setVisibility(View.VISIBLE);
                }
                break;
            case  R.id.change_password_btn2:
                progressBar.setVisibility(View.VISIBLE);

                if (user != null && !inppassword.getText().toString().trim().equals("")) {
                    if (inppassword.getText().toString().trim().length() < 6) {
                        inppassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(inppassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Status.this, "Password is updated", Toast.LENGTH_SHORT).show();
                                            change.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(Status.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (inppassword.getText().toString().trim().equals("")) {
                    inppassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }

        }
    }

    void uploadData()
    {
        mProgressDialog = new ProgressDialog(Status.this);
        mProgressDialog.setTitle("Uploading Image...");
        mProgressDialog.setMessage("Please wait while we upload and process the image.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        contentImage.setDrawingCacheEnabled(true);
        contentImage.buildDrawingCache();


        final String imageRef = uid + ".jpg";
        StorageReference mountainsRef = storageReference.child("images").child(uid).child("profile_images").child(imageRef);
        mountainsRef2 = storageReference.child("thumbs").child(uid).child("profile_images").child(imageRef);

        mountainsRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    String download_url = task.getResult().getDownloadUrl().toString();

                    mRootRef.child(Constants.FIREBASE_USERS_IMAGE).setValue(download_url);

                    mountainsRef2.putStream(comstream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {

                                String download_url2 = task.getResult().getDownloadUrl().toString();

                                mRootRef.child(Constants.FIREBASE_USERS_THUMB).setValue(download_url2);

                                Toast.makeText(getApplicationContext(),"Successfully updated photo",Toast.LENGTH_SHORT).show();

                                mProgressDialog.dismiss();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }}});


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

                        uploadData();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to read picture data!",Toast.LENGTH_SHORT).show();
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