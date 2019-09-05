package com.naitik.piechat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private DatabaseReference mReqRef;
    private DatabaseReference mRootref;

    private RecyclerView mReqList;

    private FirebaseAuth mAuth;

    private View mMainView;

    private String current_user;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();

        mRootref = FirebaseDatabase.getInstance().getReference();
        mRootref.keepSynced(true);

        mReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(current_user);
        mReqRef.orderByChild("request_type").equalTo("received");
        mReqRef.keepSynced(true);

        mReqList = (RecyclerView) mMainView.findViewById(R.id.requests_list);
        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query requests = mReqRef.orderByChild("request_type").equalTo("received");

        final FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(

                Requests.class,
                R.layout.users_single_layout,
                RequestViewHolder.class,
                requests

        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, final Requests model, int position) {

                final String list_user_id = getRef(position).getKey();

                mRootref.child("Users").child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setImage(image, getContext());

                        viewHolder.setName(name);
                        viewHolder.setStatus(status);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mReqList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class RequestViewHolder extends  RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setStatus(String a) {
            TextView t = (TextView) mView.findViewById(R.id.user_single_status);
            t.setText(a);
        }

        public void setName(String a) {
            TextView t = (TextView) mView.findViewById(R.id.user_single_name);
            t.setText(a);
        }

        public void setImage(String thumb_image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_pic).into(userImageView);

        }

    }


}
