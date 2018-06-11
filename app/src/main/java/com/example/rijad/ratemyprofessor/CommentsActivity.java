package com.example.rijad.ratemyprofessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;

public class CommentsActivity extends AppCompatActivity {

    private ImageButton postCommentButton;
    private EditText commentInputText;
    private RecyclerView commentsList;

    private String Post_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        commentsList = (RecyclerView) findViewById(R.id.comments_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        commentInputText = (EditText) findViewById(R.id.comment_input);
        postCommentButton = (ImageButton) findViewById(R.id.post_com_btn);
    }
}
