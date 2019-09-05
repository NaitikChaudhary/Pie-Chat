package com.naitik.piechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;

    private EditText mSearch;
    private ImageView searchBtn;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");


        mToolbar = (Toolbar) findViewById(R.id.allusers_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList = (RecyclerView) findViewById(R.id.users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        mSearch = (EditText) findViewById(R.id.search_name_users);
        searchBtn = (ImageView) findViewById(R.id.search_button_users);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSearch.getText().toString().equals("")) {
                    searchUser(mSearch.getText().toString());
                }
            }
        });

    }

    private void searchUser(String name) {

        Query searchQuery = mDatabaseRef.orderByChild("name").startAt(name).endAt(name + "\uf8ff");

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                searchQuery

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int position) {
                viewHolder.setName(users.getName());
                viewHolder.setImage(users.getThumb_image(), getApplicationContext());
                viewHolder.setStatus(users.getStatus());

                final String users_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", users_id);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String  currentUid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("online").setValue("true");

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setStatus(String status) {
            TextView userStatus = (TextView) mView.findViewById(R.id.user_single_status);
            userStatus.setText(status);
        }

        public void setImage(String thumb_image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_pic).into(userImageView);


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(currentUser != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("online").setValue("true");
        }

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

