package com.example.thesis.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;



import java.util.List;


public class PersonalExerciseAddAdapter extends RecyclerView.Adapter<PersonalExerciseAddAdapter.UserViewHolder>{
    private List<ProgramExerciseModel> list;

    public PersonalExerciseAddAdapter(List<ProgramExerciseModel> list){
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
        holder.checkBox.setEnabled(false);
        if(user.status.equals(stat)){
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setChecked(true);
        }

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "Edit");
                menu.add(holder.getAdapterPosition(), 1,0, "Remove");

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
            alt = itemView.findViewById(R.id.alt);
            weight = itemView.findViewById(R.id.weight);
            checkBox = itemView.findViewById(R.id.checkBox);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
