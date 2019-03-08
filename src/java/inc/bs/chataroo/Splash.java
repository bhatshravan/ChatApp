package inc.bs.chataroo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import inc.bs.chataroo.login.FirstLogin;
import inc.bs.chataroo.login.LoginActivity;
import inc.bs.chataroo.misc.Constants;

/**
 * Created by Shravan on 27-12-2017.
 */

public class Splash extends Activity {
    // Splash screen timer
    int cacheint=0;
    private static int SPLASH_TIME_OUT = 400;

    String PREFE = "chataroo";
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    private FirebaseAuth auth;
    Boolean b;
    String username,userThumb,token,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            setContentView(R.layout.activity_splash);
            pref = getSharedPreferences(PREFE, Context.MODE_PRIVATE);
            editor=pref.edit();

            if (Build.VERSION.SDK_INT >= 23) {
                //do your check here
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //File write logic here
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Splash.this);
                    builder1.setMessage("Please enable storage permission for storage of messages");
                    builder1.setCancelable(true);
                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                    //                  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }
                    );
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    ActivityCompat.requestPermissions(Splash.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }
                    );
                }
            }



            if(!isNetworkAvailable())
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Splash.this);
                builder1.setMessage("Please connect to internet and then click reload");
                builder1.setCancelable(true);
                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                builder1.setPositiveButton(
                        "Reload",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                recreate();
                            }
                        }
                );
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);*/
            else
                login();
        }
        catch (Exception E)
        {
            Toast.makeText(getApplicationContext(),"Error eroor "+E.toString(),Toast.LENGTH_LONG).show();
/*
            if(!isNetworkAvailable())
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Splash.this);
                builder1.setMessage("Please connect to internet and then click reload");
                builder1.setCancelable(true);
                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                builder1.setPositiveButton(
                        "Positive",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                recreate();
                            }
                        }
                );
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            login();
  */
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
        else
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Splash.this);
            builder1.setMessage("Please enable storage permission for storage of messages");
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                            //                  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }
            );
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(Splash.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }
            );
            AlertDialog alert11 = builder1.create();
            alert11.show();

            recreate();
        }
    }

    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference userinfo;
    String useremail;

    protected void login()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }

        else {

            uid=user.getUid();
            userinfo = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USERS).child(uid);
            userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(Constants.FIREBASE_USERNAME)) {

                        startActivity(new Intent(Splash.this, FirstLogin.class));
                        finish();

                    }
                    else
                    {

                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("online").setValue(ServerValue.TIMESTAMP);

                                username = dataSnapshot.child(Constants.FIREBASE_USERNAME).getValue().toString();
                                userThumb = dataSnapshot.child(Constants.FIREBASE_USERS_THUMB).getValue().toString();
                                token = dataSnapshot.child(Constants.FIREBASE_FCM_TOKEN).getValue().toString();


                                FirebaseDatabase.getInstance().getReference().child("ads").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Intent i = new Intent(Splash.this, MainActivity.class);
                                        i.putExtra("first", b);
                                        i.putExtra(Constants.FIREBASE_MYUSERNAME, username);
                                        i.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);
                                        i.putExtra(Constants.FIREBASE_MY_FCM_TOKEN, token);
                                        i.putExtra("banner", dataSnapshot.child("banner").getValue().toString());
                                        i.putExtra("inter", dataSnapshot.child("inter").getValue().toString());
                                        i.putExtra("upd", dataSnapshot.child("upd").getValue().toString());
                                        i.putExtra("manupd", dataSnapshot.child("manupd").getValue().toString());

                                        //Log.e("logggg",dataSnapshot.child("banner").toString());
                                        //Log.e("logggg",dataSnapshot.toString());

                                        editor.putString(Constants.FIREBASE_USERNAME, username);
                                        editor.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
                                        editor.putString(Constants.FIREBASE_MY_FCM_TOKEN, token);
                                        editor.apply();

                                        startActivity(i);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(),"Internet error, restarting app",Toast.LENGTH_SHORT).show();
                                        recreate();

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),"Internet error, restarting app",Toast.LENGTH_SHORT).show();
                                recreate();
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
        /*    authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                } else {

                    userinfo = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USERS).child(user.getUid());
                    Toast.makeText(getApplicationContext(),user.getUid(),Toast.LENGTH_SHORT).show();
                    userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(getApplicationContext(),dataSnapshot.toString(),Toast.LENGTH_SHORT).show();

                            if (!dataSnapshot.hasChild(Constants.FIREBASE_USERNAME)) {
                                startActivity(new Intent(Splash.this, FirstLogin.class));
                                finish();
                            } else {
                                username = dataSnapshot.child(Constants.FIREBASE_USERNAME).getValue().toString();
                                userThumb = dataSnapshot.child(Constants.FIREBASE_USERS_THUMB).getValue().toString();

                                Intent i = new Intent(Splash.this, ProfileActivity.class);
                                i.putExtra("first", b);
                                i.putExtra(Constants.FIREBASE_USERNAME, username);
                                i.putExtra(Constants.FIREBASE_USERS_THUMB, userThumb);

                                editor.putString(Constants.FIREBASE_USERNAME, username);
                                editor.putString(Constants.FIREBASE_USERS_THUMB, userThumb);
                                editor.apply();

                                startActivity(i);
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };*/
    }
    public void clearcac()
    {
        deleteCache(getApplicationContext());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
