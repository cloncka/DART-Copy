package com.bradley.dart;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//this class isn't something to display. Instead, it's a helper class to assist in
//displaying objects on screen. This is why it extends an adapter and not an activity.
public class EntryViewAdapter extends BaseAdapter
{
    Activity context;
    String Date[];

    String Glucose[];

    //the variables that are stored oncreation are assigned below.
    //here, we grab the dates and the Glucose readings and store them in this class.
    public EntryViewAdapter(Activity context, String[] title, String[] ID) {
        super();
        this.context = context;
        this.Date = title;
        this.Glucose = ID;
    }
//returns the number of entries to dispaly by getting the length of the dates that are stored.
    public int getCount() {

        return Date.length;
    }
//helper functions that must be present but aren't needed for this activity.
    public Object getItem(int position) {

        return null;
    }

    public long getItemId(int position) {

        return 0;
    }
//stores the textviews which are stored inside of the linearlayout on the current screen.
    // which should be in AllEntries or PatientEntryList
    class ViewHolder {
        TextView txtViewDate;
        TextView txtViewGluc;

    }
//this function actually assigns what to put in the linearlayout on the page.
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //grab the current view and make sure the content fills as much of it as possible.
        //with the inflater.
        //viewholder is used to store changes to inside the layout for later.
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();
        //if nothing is being displayed yet
        if (convertView == null)
        {
            //use the inflator as described above
            convertView = inflater.inflate(R.layout.patient_entry_row, null);
            holder = new ViewHolder();
            //grab the two textviews inside the linearlayout
            holder.txtViewDate = (TextView) convertView.findViewById(R.id.textViewdate);
            holder.txtViewGluc = (TextView) convertView.findViewById(R.id.textViewglucose);
            //store that we have made changes to the display
            convertView.setTag(holder);
        }
        //if anything is already being displayed in the textview, instead append to the
        //list of textviews
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        //add the list of entries to the textview.
        //since the page has two textviews in a linearlayout, it assigns each
        // position with one data's entry
        holder.txtViewDate.setText(Date[position]);
        holder.txtViewGluc.setText(Glucose[position]);
        //alternate background colors for each row
        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.GRAY);
        } else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        //return the view so it can be displayed in the class that called it
        return convertView;
    }

}