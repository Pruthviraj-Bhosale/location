package com.racksonsit.com.locationtracking.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.racksonsit.com.locationtracking.Activity.LoginActivity;
import com.racksonsit.com.locationtracking.Activity.MapsActivity;
import com.racksonsit.com.locationtracking.Activity.WelcomeActivity;
import com.racksonsit.com.locationtracking.R;
import com.racksonsit.com.locationtracking.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.racksonsit.com.locationtracking.Activity.WelcomeActivity.straddress;


public class UsersLIstRecycleViewAdapter extends RecyclerView.Adapter<UsersLIstRecycleViewAdapter.JobHolder>  {

    Context context;
    ArrayList<User> usersLists;
    Boolean first=true;
    List<String> selected;

    public UsersLIstRecycleViewAdapter(Context context, ArrayList<User> usersLists ) {
        this.context=context;
        this.usersLists=usersLists;
    }





    @Override
    public JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_users_list, parent, false);

        UsersLIstRecycleViewAdapter.JobHolder myViewHolder = new UsersLIstRecycleViewAdapter.JobHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final JobHolder holder, final int position) {

        final User userList = usersLists.get(position);



        holder.tv_name.setText(userList.getName());
        if(userList.getAddress().equals("null,null")) {
            holder.tv_address.setText("Not Availble");
        }else {
            //holder.tv_address.setText(userList.getAddress());
            straddress=userList.getAddress();
            Integer commaindex = straddress.lastIndexOf(",");
            String  strlatitude = straddress.substring(0, commaindex - 1);
            String  strlongitude = straddress.substring(commaindex + 1);

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(strlatitude), Double.parseDouble(strlongitude), 1);

                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                holder.tv_address.setText(cityName);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }




        holder.cv_group.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if(userList.getAddress().equals("null,null")) {
                    Toast.makeText(context, "Location not available...", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i=new Intent(context,MapsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name",userList.getName());
                    i.putExtra("email",userList.getAddress());
                    i.putExtra("androidid",userList.getAndroidid());
                    context.startActivity(i);
                }




            }
        });





    }

    @Override
    public int getItemCount() {
        return usersLists.size();
    }

    public class JobHolder extends RecyclerView.ViewHolder {



        //new
        CardView cv_group;
        TextView tv_name,tv_address;


        public JobHolder(View itemView) {
            super(itemView);



            cv_group=(CardView)itemView.findViewById(R.id.cv_group);
            tv_name=(TextView)itemView.findViewById(R.id.txt_user_name);
            tv_address=(TextView)itemView.findViewById(R.id.txt_user_address);


        }
    }




}
