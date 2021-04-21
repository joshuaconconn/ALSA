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
import com.example.thesis.Models.CreateWorkOutProgModel;
import com.example.thesis.R;
import java.util.List;

public class GetFromMyExerciseAdapter extends RecyclerView.Adapter<GetFromMyExerciseAdapter.UserViewHolder>{
    private List<CreateWorkOutProgModel> list;
    Activity context;

    public GetFromMyExerciseAdapter(List<CreateWorkOutProgModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_program, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final CreateWorkOutProgModel user = list.get(position);
        holder.uName.setText(user.progName);

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "View Exercises");
                menu.add(holder.getAdapterPosition(), 1,0, "Copy");
            }
        });

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView uName;
        RelativeLayout layout;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uName = itemView.findViewById(R.id.name);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
