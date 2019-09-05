package com.naitik.piechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar mProgessBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final String image = getIntent().getStringExtra("image_url");

        mProgessBar = (ProgressBar) findViewById(R.id.image_view_progress);
        mProgessBar.setVisibility(View.VISIBLE);

        imageView = (ImageView) findViewById(R.id.view_image);
        Picasso.with(imageView.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_pic).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgessBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(imageView.getContext()).load(image)
                                .placeholder(R.drawable.default_pic).into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                mProgessBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                mProgessBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });

        PhotoViewAttacher photoAttacher;
        photoAttacher= new PhotoViewAttacher(imageView);
        photoAttacher.update();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String  currentUid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("online").setValue("true");


    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(currentUser != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
