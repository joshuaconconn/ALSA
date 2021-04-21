package com.example.thesis.Adapters;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.thesis.Models.BmiModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;
import java.util.List;

public class ProgramExerciseAdapter extends RecyclerView.Adapter<ProgramExerciseAdapter.UserViewHolder>{
    private List<ProgramExerciseModel> list;
    Activity context;

    public ProgramExerciseAdapter(List<ProgramExerciseModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_prog_exer, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final ProgramExerciseModel user = list.get(position);
        holder.part.setText(user.part + "   ");
        holder.name.setText(user.name);
        holder.set.setText(user.set + "set   ");
        holder.reps.setText(user.reps+ "reps   ");
        holder.weight.setText(user.weight + "kg");

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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

        TextView part, name, set, reps, weight;
        RelativeLayout layout;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            part = itemView.findViewById(R.id.part);
            name = itemView.findViewById(R.id.name);
            set = itemView.findViewById(R.id.set);
            reps = itemView.findViewById(R.id.reps);
            weight = itemView.findViewById(R.id.weight);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
