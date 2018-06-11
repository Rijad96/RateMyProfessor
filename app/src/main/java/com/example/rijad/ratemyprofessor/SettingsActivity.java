package com.example.rijad.ratemyprofessor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.FeatureGroupInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText userName, userProfName, userStatus, userDOB, userGender, userFaculty, userStudyStatus;
    private Button updateSettings;
    private CircleImageView settingsImage;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingBar = new ProgressDialog(this);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfName = (EditText) findViewById(R.id.settings_fullname);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userDOB = (EditText) findViewById(R.id.settings_dob);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userFaculty = (EditText) findViewById(R.id.settings_faculty);
        userStudyStatus = (EditText) findViewById(R.id.settings_study_status);
        updateSettings = (Button) findViewById(R.id.settings_update_button);
        settingsImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myFullname = dataSnapshot.child("fullname").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myFaculty = dataSnapshot.child("faculty").getValue().toString();
                    String myStudystatus = dataSnapshot.child("studystatus").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(settingsImage);

                    userName.setText(myUsername);
                    userProfName.setText(myFullname);
                    userStudyStatus.setText(myStudystatus);
                    userDOB.setText(myDOB);
                    userStatus.setText(myStatus);
                    userGender.setText(myGender);
                    userFaculty.setText(myFaculty);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

        settingsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Updating your profile image");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Profile Image stored", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingsActivity.this, "Profile Image stored to Database", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occurred: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String fullname = userProfName.getText().toString();
        String dob = userDOB.getText().toString();
        String gender = userGender.getText().toString();
        String faculty = userFaculty.getText().toString();
        String status = userStatus.getText().toString();
        String studystatus = userStudyStatus.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this,"Username can not be empty please enter it",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"Profile Name can not be empty please enter it",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(dob)){
            Toast.makeText(this,"Please enter your Date of Birth",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(gender)){
            Toast.makeText(this,"Please enter your gender",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(this,"Please enter your Status",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(faculty)){
            Toast.makeText(this,"Please enter your faculty",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(studystatus)){
            Toast.makeText(this,"Please enter your study status",Toast.LENGTH_SHORT);
        }
        else{
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Updating your profile image");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccInfo(username,fullname,dob,gender,faculty,status,studystatus);
        }
    }

    private void UpdateAccInfo(String username, String fullname, String dob, String gender, String faculty, String status, String studystatus) {
        HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("dob",dob);
            userMap.put("gender",gender);
            userMap.put("status",status);
            userMap.put("studystatus",studystatus);
            userMap.put("faculty",faculty);
        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this,"Account Info Updated Successfully", Toast.LENGTH_SHORT);
                    loadingBar.dismiss();
                }
                else{
                    Toast.makeText(SettingsActivity.this,"Error occurred", Toast.LENGTH_SHORT);
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
