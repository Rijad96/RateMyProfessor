package com.example.rijad.ratemyprofessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userDOB, userGender, userFaculty, userStudyStatus;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        userName = (TextView) findViewById(R.id.my_user_name);
        userProfName = (TextView) findViewById(R.id.my_profile_name);
        userStatus = (TextView) findViewById(R.id.my_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userGender = (TextView) findViewById(R.id.my_gender);
        userFaculty = (TextView) findViewById(R.id.my_faculty);
        userStudyStatus = (TextView) findViewById(R.id.my_study_status);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myFullname = dataSnapshot.child("fullname").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myFaculty = dataSnapshot.child("faculty").getValue().toString();
                    String myStudystatus = dataSnapshot.child("studystatus").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@" + myUsername);
                    userProfName.setText(myFullname);
                    userStudyStatus.setText("Study Status:" + myStudystatus);
                    userDOB.setText("DoB:" + myDOB);
                    userStatus.setText("Status:" + myStatus);
                    userGender.setText("Gender:" + myGender);
                    userFaculty.setText("Faculty:" + myFaculty);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
