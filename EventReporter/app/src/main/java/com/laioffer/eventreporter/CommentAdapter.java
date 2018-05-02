package com.laioffer.eventreporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.laioffer.eventreporter.artifacts.Comment;
import com.laioffer.eventreporter.artifacts.Event;
import com.laioffer.eventreporter.artifacts.Like;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yukening on 2018/3/27.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private final static int TYPE_EVENT = 0;
    private final static int TYPE_COMMENT = 1;

    private List<Comment> commentList;
    private Event event;

    private DatabaseReference databaseReference;
    private LayoutInflater inflater;

    public CommentAdapter(Context context) {
        this.context = context;
        commentList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public void setComments(final List<Comment> comments) {
        this.commentList = comments;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_EVENT : TYPE_COMMENT;
    }

    @Override
    public int getItemCount() {
        return commentList.size() + 1;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventUser;
        public TextView eventTitle;
        public TextView eventLocation;
        public TextView eventDescription;
        public TextView eventTime;
        public ImageView eventImgView;
        public ImageView eventImgViewGood;
        public ImageView eventImgViewComment;

        public TextView eventLikeNumber;
        public TextView eventCommentNumber;
        public View layout;

        public EventViewHolder(View v) {
            super(v);
            layout = v;
            eventUser = (TextView) v.findViewById(R.id.comment_main_user);
            eventTitle = (TextView) v.findViewById(R.id.comment_main_title);
            eventLocation = (TextView) v.findViewById(R.id.comment_main_location);
            eventDescription = (TextView) v.findViewById(R.id.comment_main_description);
            eventTime = (TextView) v.findViewById(R.id.comment_main_time);
            eventImgView = (ImageView) v.findViewById(R.id.comment_main_image);
            eventImgViewGood = (ImageView) v.findViewById(R.id.comment_main_like_img);
            eventImgViewComment = (ImageView) v.findViewById(R.id.comment_main_comment_img);
            eventLikeNumber = (TextView) v.findViewById(R.id.comment_main_like_number);
            eventCommentNumber = (TextView) v.findViewById(R.id.comment_main_comment_number);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUser;
        public TextView commentDescription;
        public TextView commentTime;
        public View layout;

        public CommentViewHolder(View v) {
            super(v);
            layout = v;
            commentUser = (TextView) v.findViewById(R.id.comment_item_user);
            commentDescription = (TextView)v.findViewById(R.id.comment_item_description);
            commentTime = (TextView)v.findViewById(R.id.comment_item_time);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case TYPE_EVENT:
                v = inflater.inflate(R.layout.comment_main, parent, false);
                viewHolder = new EventViewHolder(v);
                break;
            case TYPE_COMMENT:
                v = inflater.inflate(R.layout.comment_item, parent, false);
                viewHolder = new CommentViewHolder(v);
                break;
        }
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_EVENT:
                EventViewHolder viewHolderEvent = (EventViewHolder) holder;
                configureEventView(viewHolderEvent);
                break;
            case TYPE_COMMENT:
                CommentViewHolder viewHolderAds = (CommentViewHolder) holder;
                configureCommentView(viewHolderAds, position);
                break;
        }
    }

    private void configureEventView(final EventViewHolder holder) {
        holder.eventUser.setText(event.getUsername());
        holder.eventTitle.setText(event.getTitle());
        String[] locations = event.getAddress().split(",");
        holder.eventLocation.setText(locations[1] + "," + locations[2]);
        holder.eventDescription.setText(event.getDescription());
        holder.eventTime.setText(Utils.timeTransformer(event.getTime()));
        holder.eventCommentNumber.setText(String.valueOf(event.getCommentNumber()));
        holder.eventLikeNumber.setText(String.valueOf(event.getLike()));

        if (event.getImgUri() != null) {
            final String url = event.getImgUri();
            holder.eventImgView.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Utils.getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.eventImgView.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.eventImgView.setVisibility(View.GONE);
        }

        holder.eventImgViewGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Event recordedevent = snapshot.getValue(Event.class);
                            if (recordedevent.getId().equals(event.getId())) {
                                int number = recordedevent.getLike();
                                //holder.eventLikeNumber.setText(String.valueOf(number + 1));
                                //snapshot.getRef().child("like").setValue(number + 1);
                                setLike(snapshot, holder, number, event);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void configureCommentView(final CommentViewHolder commentHolder, final int position) {
        //Why is position - 1?
        final Comment comment = commentList.get(position - 1);
        commentHolder.commentUser.setText(comment.getCommenter());
        commentHolder.commentDescription.setText(comment.getDescription());
        commentHolder.commentTime.setText(Utils.timeTransformer(comment.getTime()));
    }

    // When the user click on "Like" to add one "Like" for an event, first we should check whether
    // this user has "Liked" this event, and if so, the like number should be decreased by one, and
    // if not, the like number is increased by one.
    // How to check whether one user has liked one event??
    // We use another table called "likes" which has userId, eventId. We check this table to see
    // whether this user has liked this event, if so, decrease the like number by one and meanwhile
    // delete the record from the table; if not, increase the like number by one and insert a record
    // into the table
    private void setLike(final DataSnapshot eventsSnapshot, final EventViewHolder holder,
                         final int number, final Event event) {
        databaseReference.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // iterate over the "likes" table to check whether this user has liked this event
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Like like = snapshot.getValue(Like.class);

                    if (like.getUserId().equals(Utils.username)
                            && like.getEventId().equals(event.getId())) {
                        snapshot.getRef().removeValue();
                        holder.eventLikeNumber.setText(String.valueOf(number - 1));
                        holder.eventImgViewGood.setImageResource(R.drawable.like);
                        eventsSnapshot.getRef().child("like").setValue(number - 1);
                        return;
                    }
                }
                // if this user does not liked this event, increase the like number by one and
                // insert a record into "likes" table to mark that this user has already liked
                // this event
                Like like = new Like();
                like.setEventId(event.getId());
                like.setUserId(Utils.username);
                String key = databaseReference.child("likes").push().getKey();
                like.setLikeId(key);
                databaseReference.child("likes").child(key).setValue(like);
                holder.eventLikeNumber.setText(String.valueOf(number + 1));
                //TODO : create an image and replace with liked in drawable
                holder.eventImgViewGood.setImageResource(R.drawable.liked);
                eventsSnapshot.getRef().child("like").setValue(number + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
