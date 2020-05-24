package com.nbt.comp2100_bunker_survival.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nbt.comp2100_bunker_survival.R;

import java.util.List;
import java.util.Map;

// DERIVED FROM: https://www.journaldev.com/9942/android-expandablelistview-example-tutorial //TODO ref in documentation
// adapter for expanding lists for displaying the player's list of items
public class ItemExpListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<String> items;
    Map<String, List<String>> details;

    public ItemExpListAdapter(Context context, List<String> items, Map<String, List<String>> details) {
        this.context = context;
        this.items = items;
        this.details = details;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return details.get(items.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return items.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return details.get(items.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String item = (String)getGroup(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_parent, null);
        }

        TextView parentText = (TextView) view.findViewById(R.id.parentText);
        parentText.setText(item);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        String details = (String) getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_child, null);
        }

        TextView childText = (TextView) view.findViewById(R.id.childText);
        childText.setText(details);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
