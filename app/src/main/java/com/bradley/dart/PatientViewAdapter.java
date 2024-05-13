package com.bradley.dart;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
//this works similarly to entryviewadapter, but has 3 variables for displaying
//patient information instead.

public class PatientViewAdapter extends BaseAdapter
{
    Activity context;
    String title[];

    String IDs[];
    String description[];

    public PatientViewAdapter(Activity context, String[] title, String[] ID, String[] description) {
        super();
        this.context = context;
        this.title = title;
        this.IDs = ID;
        this.description = description;
    }

    public int getCount() {

        return title.length;
    }

    public Object getItem(int position) {

        return null;
    }

    public long getItemId(int position) {

        return 0;
    }

    class ViewHolder {
        TextView txtViewTitle;
        TextView txtViewDescription;

        TextView txtViewIDs;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.patient_dashboard_row, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.textView3);
            holder.txtViewIDs = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle.setText(title[position]);
        holder.txtViewDescription.setText(description[position]);
        holder.txtViewIDs.setText(IDs[position]);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.GRAY);
        } else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        return convertView;
    }

}