package com.example.thesis.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.Activities.InstructorHome;
import com.example.thesis.Activities.TraineeHome;
import com.example.thesis.Class.User;
import com.example.thesis.Models.InjuryModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PersonalExerciseAdapter extends RecyclerView.Adapter<PersonalExerciseAdapter.UserViewHolder>{
    private List<ProgramExerciseModel> list;
    Activity context;
    DatabaseReference reff;
    FirebaseAuth fAuth;
    String userId;
    Integer Type;

    public PersonalExerciseAdapter(List<ProgramExerciseModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_trainee_exer, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        final ProgramExerciseModel user = list.get(position);
        if(user.part.equals("Cardio")){
            holder.part.setText(user.part + "   ");
            holder.name.setText(user.name);
            holder.set.setText("resistance: " + user.set + "   ");
            holder.reps.setText(user.reps + "mins   ");
            holder.weight.setVisibility(View.INVISIBLE);
            holder.alt.setVisibility(View.INVISIBLE);

        }else {
            holder.part.setText(user.part + "   ");
            holder.name.setText(user.name);
            holder.set.setText(user.set + "set   ");
            holder.reps.setText(user.reps + "reps   ");
            holder.weight.setText(user.weight + "kg");
            holder.alt.setText("Alternative: " + user.alternative);
        }


        String stat = "0";
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        final String dateToday = format.format(today);
        try {
            Date curDate = format.parse(dateToday);
            Date comDate = format.parse(user.date);
            long diff = curDate.getTime() - comDate.getTime();
            if(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) == 0){
                holder.checkBox.setEnabled(true);
            }else {
                holder.checkBox.setEnabled(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(user.status.equals(stat)){
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.checkBox.isChecked()){
                    reff = FirebaseDatabase.getInstance().getReference().child("DailyExercise");
                    ProgramExerciseModel user = list.get(holder.getAdapterPosition());
                    user.status = "1";
                    Map<String, Object> userValues = user.toMaps();
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put(user.id, userValues);
                    reff.updateChildren(newUser);
                }else{
                    reff = FirebaseDatabase.getInstance().getReference().child("DailyExercise");
                    ProgramExerciseModel user = list.get(holder.getAdapterPosition());
                    user.status = "0";
                    Map<String, Object> userValues = user.toMaps();
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put(user.id, userValues);
                    reff.updateChildren(newUser);
                }
            }
        } );
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "View Tutorial");
                menu.add(holder.getAdapterPosition(), 1,0, "View Alternative Workout Tutorial");

            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView part, name, set, reps, weight, alt;
        CheckBox checkBox;
        RelativeLayout layout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            part = itemView.findViewById(R.id.part);
            name = itemView.findViewById(R.id.name);
            set = itemView.findViewById(R.id.set);
            reps = itemView.findViewById(R.id.reps);
            weight = itemView.findViewById(R.id.weight);
            alt = itemView.findViewById(R.id.alt);
            checkBox = itemView.findViewById(R.id.checkBox);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
