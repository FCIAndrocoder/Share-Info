package com.example.abdullahjubayer.shareinfo2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

class aj_show_employee_to_user_adapter extends BaseAdapter {

    Context context;
    ArrayList<String> name;
    ArrayList<String> email;
    ArrayList<String> phone;
    ArrayList<String> picture;
    ArrayList<String> qualification;
    LayoutInflater layoutInflater;

    public aj_show_employee_to_user_adapter(Context context, ArrayList<String> name, ArrayList<String> email, ArrayList<String> phone, ArrayList<String> picture, ArrayList<String> qualification) {
        this.context = context;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
        this.qualification = qualification;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView==null){
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.aj_employee_show_design,parent,false);
        }


        ImageView image=convertView.findViewById(R.id.employee_show_image);
        TextView t_name=convertView.findViewById(R.id.employee_show_name);
        TextView t_email=convertView.findViewById(R.id.employee_show_email);
        TextView t_phone=convertView.findViewById(R.id.employee_show_phone);
        TextView t_qualification=convertView.findViewById(R.id.employee_show_qualification);

        Glide.with(context).load(picture).into(image);
        t_name.setText("Name: "+name.get(position));
        t_email.setText("Email: "+email.get(position));
        t_phone.setText("Phone: "+phone.get(position));
        t_qualification.setText("Designation: "+qualification.get(position));


        return convertView;
    }
}
