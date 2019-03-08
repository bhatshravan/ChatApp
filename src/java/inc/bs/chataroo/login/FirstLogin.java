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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import inc.bs.chataroo.MainActivity;
import inc.bs.chataroo.R;
import inc.bs.chataroo.Splash;
import inc.bs.chataroo.misc.Constants;
import inc.bs.chataroo.misc.FileUtil;

/**
 * Created by Shravan on 26-12-2017.
 */

public class FirstLogin extends AppCompatActivity {

    private EditText inputname, inputuname;
    ImageView profileImageView;
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
    private String name,uname,uid,userThumb="";
    byte[] thumb_byte;
    Bitmap ThumbImage;
    Uri imageuri,thumburi;
    Intent i;


    InputStream comstream;
    private File actualImage;
    private File compressedImage;

    ProgressDialog mProgressDialog;
    /*SharedPreferences pref = getSharedPreferences("Chataroo", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_firstlogin);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        uid = firebaseUser.getUid();

        btnNext = (Button) findViewById(R.id.nextbtn);
        btnuploadPic = (Button) findViewById(R.id.btn_uploadpic);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        inputname = (EditText) findViewById(R.id.name);
        inputuname = (EditText) findViewById(R.id.uname);
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

                name = inputname.getText().toString().trim();
                uname = inputuname.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(uname)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uname.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Username too short, enter minimum 5 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                userinfo= FirebaseDatabase.getInstance().getReference().child(Constants.USERNAME_DATA).child(uname.toLowerCase());
                userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(Constants.TAKEN)) {
                            uploadData();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Username already taken\nChoose a different username", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }});
    }

    protected void uploadData()
    {
        final HashMap<String,Object> values = new HashMap<>();
        values.put(Constants.FIREBASE_LASTLOGIN, ServerValue.TIMESTAMP);
        values.put(Constants.FIREBASE_BANNED,"no");
        values.put(Constants.FIREBASE_USERNAME,uname);
        values.put("created",ServerValue.TIMESTAMP);
        values.put(Constants.FIREBASE_STATUS,"*NO STATUS*");
        values.put(Constants.FIREBASE_USERS_UID,uid);
        values.put(Constants.FIREBASE_FCM_TOKEN,FirebaseInstanceId.getInstance().getToken());

        mProgressDialog = new ProgressDialog(FirstLogin.this);
        mProgressDialog.setTitle("Creating user...");
        mProgressDialog.setMessage("Please wait while we upload and process the data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        progressBar.setVisibility(View.VISIBLE);


        if (hasImageChanged) {

            final String imageRef = uid + ".jpg";
            StorageReference mountainsRef = storageReference.child("images").child(uid).child("profile_images").child(imageRef);
            mountainsRef2 = storageReference.child("thumbs").child(uid).child("profile_images").child(imageRef);


            mountainsRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();
                        values.put(Constants.FIREBASE_USERS_IMAGE,download_url);


                        mountainsRef2.putStream(comstream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {

                                    String download_url2 = task.getResult().getDownloadUrl().toString();

                                    values.put(Constants.FIREBASE_USERS_THUMB,download_url2);

                                    databaseReference.child(Constants.FIREBASE_USERS).child(firebaseUser.getUid()).setValue(values);

                                    userThumb=download_url2;

                                    values.put(Constants.FIREBASE_USERS_EMAIL,FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    values.put(Constants.FIREBASE_USERS_NAME,name);
                                    databaseReference.child(Constants.FIREBASE_USERS_PRIVATE).child(firebaseUser.getUid()).setValue(values);

                                    setToken();
                                    uploaduname();
                                }
                            }
                        });
                    }}});
        }
        else {
            userThumb="noimage";

            values.put(Constants.FIREBASE_USERS_IMAGE,"noimage");
            values.put(Constants.FIREBASE_USERS_THUMB,"noimage");
            databaseReference.child(Constants.FIREBASE_USERS).child(firebaseUser.getUid()).setValue(values);

            final String imageRef = uid + ".jpg";
            mountainsRef2 =storageReference.child("thumbs").child(uid).child("profile_images").child(imageRef);

            Bitmap bm = BitmapFactory.decodeResource(FirstLogin.this.getResources(), R.drawable.default_avatar);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();


            mountainsRef2.putBytes(data2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()) {

                        values.put(Constants.FIREBASE_USERS_EMAIL,FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        values.put(Constants.FIREBASE_USERS_NAME,name);

                        databaseReference.child(Constants.FIREBASE_USERS_PRIVATE).child(firebaseUser.getUid()).setValue(values);

                        setToken();
                        uploaduname();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Error uploading data,try again",Toast.LENGTH_SHORT).show();
                }});


        }
    }

    protected void uploaduname()
    {
        mProgressDialog.dismiss();
        userinfo= FirebaseDatabase.getInstance().getReference().child(Constants.USERNAME_DATA).child(uname.toLowerCase());
        //userinfo.child(Constants.TAKEN).setValue("yes");
        //userinfo.child("by").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final HashMap<String,Object> values2 = new HashMap<>();
        values2.put(Constants.TAKEN,"yes");
        values2.put("by",FirebaseAuth.getInstance().getCurrentUser().getUid());

        userinfo.setValue(values2);


        i = new Intent(FirstLogin.this, Splash.class);

        i.putExtra(Constants.FIREBASE_USERNAME,uname);
        i.putExtra(Constants.FIREBASE_USERS_THUMB,userThumb);

        startActivity(i);
        finish();
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
                        profileImageView.setImageBitmap(selectedImage);


                        actualImage = FileUtil.from(this, data.getData());


                        compressedImage = new Compressor(this)
                                .setMaxWidth(50)
                                .setMaxHeight(50)
                                .setQuality(100)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .compressToFile(actualImage);
                        comstream=new FileInputStream(compressedImage);


                        hasImageChanged = true;
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Error loading image",Toast.LENGTH_SHORT).show();
                        hasImageChanged=false;
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
    private void setToken() {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_FCM);
        tokenRef.child(firebaseUser.getUid() + "/" + Constants.FIREBASE_FCM_TOKEN).setValue(FirebaseInstanceId.getInstance().getToken());
        tokenRef.child(firebaseUser.getUid() + "/username").setValue(uname);
        tokenRef.child(firebaseUser.getUid() + "/" + Constants.FIREBASE_FCM_ENABLED).setValue(Boolean.TRUE.toString());
    }
}