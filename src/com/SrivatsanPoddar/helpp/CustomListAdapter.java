package com.SrivatsanPoddar.helpp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CustomListAdapter<T> extends ArrayAdapter<T> 
{
    T[] nodes;
    
    public CustomListAdapter(Context context, int resource, T[] myNodes) {
        super(context, resource, R.id.display_text, myNodes);
        nodes = myNodes;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        TextView displayText = (TextView) row.findViewById(R.id.display_text);
                
        Style.toOpenSans(getContext(), displayText, "light");
        
        return(row);
    }
}