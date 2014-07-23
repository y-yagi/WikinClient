package com.example.yaginuma.wikinclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yaginuma.wikinclient.R;
import com.example.yaginuma.wikinclient.model.Page;

/**
 * Created by yaginuma on 14/07/24.
 */
public class PageListAdapter extends ArrayAdapter<Page> {
    private LayoutInflater mInflater;
    private static final int TITLE_MAX_LENGHT = 30;


    public PageListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Page page = this.getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_list_item, parent, false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.sub);
        tv.setText(page.getTitle());

        tv = (TextView)convertView.findViewById(R.id.title);
        String title = page.getBody();
        if (title.length() > TITLE_MAX_LENGHT)  {
            title = title.substring(0, TITLE_MAX_LENGHT) + "...";
        }
        tv.setText(title);
        return convertView;
    }
}
