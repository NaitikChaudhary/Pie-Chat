package com.naitik.piechat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messageTextUser;
        public CircleImageView profileImage;
        public ImageView messageImage;
        public RelativeLayout mainLayout;
        public ProgressBar mProgressbar;
        public CardView mCardView;

        public MessageViewHolder(View view) {
            super(view);

            mainLayout = (RelativeLayout) view.findViewById(R.id.image_single_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_image_profile_image);
            messageImage = (ImageView) view.findViewById(R.id.image_sent_message);
            messageTextUser = (TextView) view.findViewById(R.id.message_text_user);
            mProgressbar = (ProgressBar) view.findViewById(R.id.message_image_progress);
            mCardView = (CardView)view.findViewById(R.id.message_image_card);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int i) {

        final Messages c = mMessageList.get(i);
        Messages k = null;
        final String from_user_before;
        if (i > 0) {
            k = mMessageList.get(i - 1);
        }

        if(k == null) {
            from_user_before = null;
        } else {
            from_user_before = k.getFrom();
        }

        final String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.profile_pic).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String displayUser = mUserDatabase.getKey();

        viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(viewHolder.messageImage.getContext(), ImageActivity.class);
                i.putExtra("image_url", c.getMessage());
                viewHolder.messageImage.getContext().startActivity(i);

            }
        });


        viewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final int position = viewHolder.getAdapterPosition();

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                CharSequence options[] = new CharSequence[]{"Delete this message for me", "Cancel"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.messageText.getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {
                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mMessageList.remove(position);
                            notifyDataSetChanged();
                        } else if(which == 1) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();

                return false;

            }

        });

        viewHolder.messageTextUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final int position = viewHolder.getAdapterPosition();

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                CharSequence options[] = new CharSequence[]{"Delete this message for me", "Delete for everyone", "Cancel"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.messageText.getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {

                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mMessageList.remove(position);
                            notifyDataSetChanged();

                        } else if(which == 1) {

                            reference.child(c.getTo()).child(currentUser).child(c.getId()).removeValue();
                            reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                            mMessageList.remove(position);
                            notifyDataSetChanged();

                        } else if (which == 2) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();

                return false;
            }
        });

        viewHolder.messageImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (displayUser.equals(currentUser)) {
                    final int position = viewHolder.getAdapterPosition();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                    CharSequence options[] = new CharSequence[]{"Delete this message for me", "Delete for everyone", "Cancel"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.messageText.getContext());

                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which == 0) {

                                reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                                mMessageList.remove(position);
                                notifyDataSetChanged();

                            } else if(which == 1) {

                                reference.child(c.getTo()).child(currentUser).child(c.getId()).removeValue();
                                reference.child(currentUser).child(c.getTo()).child(c.getId()).removeValue();
                                mMessageList.remove(position);
                                notifyDataSetChanged();

                            } else if (which == 2) {
                                dialog.dismiss();
                            }

                        }
                    });

                    builder.show();

                } else {

                    final int position = viewHolder.getAdapterPosition();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");

                    CharSequence options[] = new CharSequence[]{"Delete this message for me", "Cancel"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.messageText.getContext());

                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which == 0) {

                                reference.child(currentUser).child(c.getFrom()).child(c.getId()).removeValue();
                                mMessageList.remove(position);
                                notifyDataSetChanged();

                            } else if (which == 1) {
                                dialog.dismiss();
                            }

                        }
                    });

                    builder.show();


                }

                return false;
            }
        });


        viewHolder.messageText.setVisibility(View.GONE);
        viewHolder.profileImage.setVisibility(View.GONE);
        viewHolder.messageImage.setVisibility(View.GONE);
        viewHolder.messageTextUser.setVisibility(View.GONE);
        viewHolder.mProgressbar.setVisibility(View.GONE);


        if (message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageTextUser.setText(c.getMessage());
            if (displayUser.equals(currentUser)) {
                viewHolder.messageTextUser.setVisibility(View.VISIBLE);
                if(from_user_before != null && from_user_before.equals(from_user)) {
                    viewHolder.mainLayout.setPadding(0,0,0,0);
                }

            } else {
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.profileImage.setVisibility(View.VISIBLE);
                if(from_user_before != null && from_user_before.equals(from_user)) {
                    viewHolder.profileImage.setVisibility(View.GONE);
                    viewHolder.mainLayout.setPadding(0,0,0,0);
                }
            }

        } else {

            viewHolder.messageImage.setVisibility(View.VISIBLE);
            viewHolder.mProgressbar.setVisibility(View.VISIBLE);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getThumb_message()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_pic).into(viewHolder.messageImage, new Callback() {
                @Override
                public void onSuccess() {
                    viewHolder.mProgressbar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    Picasso.with(viewHolder.profileImage.getContext()).load(c.getThumb_message())
                            .placeholder(R.drawable.default_pic).into(viewHolder.messageImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.mProgressbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            viewHolder.mProgressbar.setVisibility(View.GONE);
                        }
                    });
                }
            });

            if (displayUser.equals(currentUser)) {
                if(from_user_before != null && from_user_before.equals(from_user)) {
                    viewHolder.mainLayout.setPadding(0,0,0,0);
                }

            } else {
                viewHolder.profileImage.setVisibility(View.VISIBLE);
                if(from_user_before != null && from_user_before.equals(from_user)) {
                    viewHolder.profileImage.setVisibility(View.GONE);
                    viewHolder.mainLayout.setPadding(0,0,0,0);
                }
            }

        }

    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}