package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nuradadmin.Models.Model_ContactInfo;
import com.example.nuradadmin.Models.Model_Feedback;
import com.example.nuradadmin.Models.Model_User;
import com.example.nuradadmin.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Adapter_Feedback  extends RecyclerView.Adapter<Adapter_Feedback.MyViewHolder> {
    private Context context;
    private List<Model_Feedback> feedbackList;
    private DatabaseReference feedback_DBref;
    private DatabaseReference users_DBref;

    public Adapter_Feedback(Context context, List<Model_Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
        feedback_DBref = FirebaseDatabase.getInstance().getReference("Customer Feedback");
        users_DBref = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public Adapter_Feedback.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new Adapter_Feedback.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_Feedback feedback = feedbackList.get(position);
        fetchContactInfo(feedback.getUserId(), holder);
        holder.optionType.setText(feedback.getOption());
        holder.feedback.setText(feedback.getMessage());
        holder.rating.setNumStars(5);
        holder.rating.setRating(Float.parseFloat(String.valueOf(feedback.getRating())));
        holder.profilePic.setImageResource(R.drawable.user);

        // Format submission date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(feedback.getSubmissionDate());
            if (date != null) {
                String formattedDate = outputFormat.format(date);
                holder.date.setText(formattedDate);
            } else {
                holder.date.setText(feedback.getSubmissionDate()); // Fallback to original string if parsing fails
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText(feedback.getSubmissionDate()); // Handle parsing exception
        }
    }

    private void fetchContactInfo(String userID, Adapter_Feedback.MyViewHolder holder) {
        users_DBref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Model_User user = snapshot.getValue(Model_User.class);
                    if (user != null) {
                        holder.guestName.setText(user.getFirstName() + " " + user.getLastName());
                    } else {
                        Log.e("Adapter_Feedback", "Contact Information is null for User ID: " + userID);
                    }
                } else {
                    Log.e("Adapter_Feedback", "Contact Information is null for User ID: " + userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Adapter_Feedback", "Failed to retrieve Contact ID: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView guestName, optionType, feedback, date;
        private ImageView profilePic;
        private RatingBar rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            guestName = itemView.findViewById(R.id.Reviews_ProfileName);
            optionType = itemView.findViewById(R.id.OptionType);
            feedback = itemView.findViewById(R.id.Feedback);
            date = itemView.findViewById(R.id.Reviews_Date);
            profilePic = itemView.findViewById(R.id.Reviews_ProfileImage);
            rating = itemView.findViewById(R.id.Reviews_StarRating);
        }
    }
}
