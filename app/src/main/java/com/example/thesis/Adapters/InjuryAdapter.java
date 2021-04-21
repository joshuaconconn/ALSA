package com.example.thesis.Adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.Models.InjuryModel;
import com.example.thesis.R;

import java.util.List;

public class InjuryAdapter extends RecyclerView.Adapter<InjuryAdapter.UserViewHolder>{
    private List<InjuryModel> list;

    public InjuryAdapter(List<InjuryModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_injury, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InjuryAdapter.UserViewHolder holder, int position) {
        final InjuryModel user = list.get(position);
        holder.uDate.setText(user.date + "  ");
        holder.uPart.setText(user.part + "  ");
        holder.uWeight.setText(user.name + "  ");
        holder.uHeight.setText(user.recovery );

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0,0, "Update");
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView uDate, uWeight, uHeight, uPart;
        RelativeLayout layout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uDate = itemView.findViewById(R.id.date);
            uWeight = itemView.findViewById(R.id.injuryName);
            uHeight = itemView.findViewById(R.id.injuryStatus);
            uPart =  itemView.findViewById(R.id.part);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }
}
