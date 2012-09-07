
package com.dianxinos.checkromdemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableAdapter extends BaseExpandableListAdapter {

    private Context ctx;
    private PackagesEntity pEntity;
    private List<List<PackagesEntity>> child = new ArrayList<List<PackagesEntity>>();
    private LayoutInflater mInflater;
    private String[] group;

    static class GroupViewHolder
    {
        public TextView groupTextView;
    }

    static class ChildViewHolder
    {
        public ImageView appLogoImageView;
        public TextView appNameTextView;
        public TextView notShowReasonTextView;
    }

    public ExpandableAdapter(Context ctx, String[] group, List<PackagesEntity> unuseable,
            List<PackagesEntity> useable) {
        child.add(unuseable);
        child.add(useable);
        this.ctx = ctx;
        this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.group = group;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return child.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return child.get(groupPosition).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ChildViewHolder holder;
        if (convertView == null)
        {
            holder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.package_list, null);
            holder.appLogoImageView = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.appNameTextView = (TextView) convertView.findViewById(R.id.tv_appName);
            holder.notShowReasonTextView = (TextView) convertView
                    .findViewById(R.id.tv_notShowReason);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.appLogoImageView.setImageDrawable(child.get(groupPosition).get(childPosition)
                .getAppLogo());
        holder.appNameTextView.setText(child.get(groupPosition).get(childPosition).getName());
        holder.notShowReasonTextView.setText(child.get(groupPosition).get(childPosition)
                .getReason());
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return group[groupPosition];
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return group.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        GroupViewHolder holder;
        if (convertView == null)
        {
            holder = new GroupViewHolder();
            convertView = mInflater.inflate(R.layout.package_list_group, null);
            holder.groupTextView = (TextView) convertView.findViewById(R.id.tv_group);

            convertView.setTag(holder);
        }
        else
        {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.groupTextView.setText(group[groupPosition]);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}
