package com.example.thesis.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.thesis.Models.BmiModel;
import com.example.thesis.R;
import java.util.List;

public class BmiAdapter extends RecyclerView.Adapter<BmiAdapter.UserViewHolder>{
    private List<BmiModel> list;
    Activity context;

    public BmiAdapter(List<BmiModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_bmi, parent, false));
        //return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_instructor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final BmiModel user = list.get(position);
        if(user.weight.equals(0) || user.height.equals(0)){
            holder.uWeight.setText("N/A     ");
            holder.uHeight.setText("N/A     ");
        }else{
            holder.uWeight.setText(user.weight + "kg     ");
            holder.uHeight.setText(user.height + "cm     ");
        }
            holder.uDate.setText(user.date + "     ");
        holder.uLevel.setText(user.status);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView uDate, uWeight, uHeight, uLevel;
        RelativeLayout layout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uDate = itemView.findViewById(R.id.date);
            uWeight = itemView.findViewById(R.id.weight);
            uHeight = itemView.findViewById(R.id.height);
            uLevel = itemView.findViewById(R.id.status);
            layout = itemView.findViewById(R.id.linear_layout);
        }
    }

}
