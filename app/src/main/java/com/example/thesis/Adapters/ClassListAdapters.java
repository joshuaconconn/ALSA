package com.example.thesis.Adapters;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.Models.ClassListModel;
import com.example.thesis.R;

import java.util.List;

public class ClassListAdapters extends RecyclerView.Adapter<ClassListAdapters.UserViewHolder>{


    private List<ClassListModel> list;
    Activity context;

    public ClassListAdapters(List<ClassListModel> list){
        this.list = list;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_accept_class, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final ClassListModel user = list.get(position);

        holder.uName.setText("Name: " + user.traineeName);
        holder.uEmail.setText("Exercise Type: " + user.classType);
        holder.uLevel.setText("Exercise Level: " + user.level);
        holder.uType.setText("Request Date: " + user.date);


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "Open");
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView uName, uEmail, uType, uLevel;
        RelativeLayout layout;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uName = itemView.findViewById(R.id.name);
            uEmail = itemView.findViewById(R.id.email);
            uLevel = itemView.findViewById(R.id.level);
            uType = itemView.findViewById(R.id.type);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }
    private void showMessage(String message) {
        Toast.makeText(context.getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

}

