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

import com.example.thesis.Models.ChooseInstructorModel;
import com.example.thesis.R;

import java.util.List;

public class ChooseInstructorAdapter extends RecyclerView.Adapter<ChooseInstructorAdapter.UserViewHolder>{
   // private OnItemClickListener listener;

    private List<ChooseInstructorModel> list;
    Activity context;

    public ChooseInstructorAdapter(List<ChooseInstructorModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        final ChooseInstructorModel user = list.get(position);
        holder.uName.setText(user.name);
        holder.uEmail.setText(user.email);
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "Choose Coach");
            }
       });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView uName, uEmail;
        RelativeLayout layout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uName = itemView.findViewById(R.id.name);
            uEmail = itemView.findViewById(R.id.email);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }
    private void showMessage(String message) {
        Toast.makeText(context.getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }
}
