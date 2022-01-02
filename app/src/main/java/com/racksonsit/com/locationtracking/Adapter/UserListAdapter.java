package com.racksonsit.com.locationtracking.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.racksonsit.com.locationtracking.R;

/**
 * Created by Android on 12/29/2018.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {
    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


       View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_users_list,parent,false);
       UserHolder holder=new UserHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        public UserHolder(View itemView) {
            super(itemView);

        }
    }
}
