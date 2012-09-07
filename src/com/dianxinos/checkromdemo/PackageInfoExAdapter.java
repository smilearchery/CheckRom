
package com.dianxinos.checkromdemo;

import java.util.ArrayList;
import java.util.List;

import com.dianxinos.checkromdemo.ExpandableAdapter.ChildViewHolder;
import com.dianxinos.checkromdemo.ExpandableAdapter.GroupViewHolder;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PackageInfoExAdapter extends BaseExpandableListAdapter {

    private Context ctx;
    private PackagesEntity pEntity;
    private List<List<String>> child = new ArrayList<List<String>>();
    private LayoutInflater mInflater;
    private ArrayList<String> group = new ArrayList<String>();

    static class GroupViewHolder
    {
        public TextView groupTextView;
    }

    static class ChildViewHolder
    {
        public TextView packageInfoTextView;
    }

    public PackageInfoExAdapter(Context ctx, PackagesEntity pEntity) {
        this.ctx = ctx;
        this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (pEntity.getPermissionInfos() != null && pEntity.getPermissionInfos().size() > 0) {
            group.add(ctx.getString(R.string.group_permissions));
            ArrayList<String> pList = pEntity.getPermissionInfos();
            child.add(pList);
        }
        if (pEntity.getFeatureInfos() != null && pEntity.getFeatureInfos().length > 0) {
            group.add(ctx.getString(R.string.group_features));
            ArrayList<String> fList = new ArrayList<String>();
            for (FeatureInfo fInfo : pEntity.getFeatureInfos()) {
                fList.add(fInfo.name);
            }
            child.add(fList);
        }

        if (pEntity.getUsesLibraries() != null && pEntity.getUsesLibraries().size() > 0) {
            group.add(ctx.getString(R.string.group_libraries));
            ArrayList<String> lList = new ArrayList<String>();
            for (String l : pEntity.getUsesLibraries()) {
                lList.add(l);
            }
            child.add(lList);
        }

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
        String text = child.get(groupPosition).get(childPosition);
        ChildViewHolder holder;
        if (convertView == null)
        {
            holder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.package_info_list, null);
            holder.packageInfoTextView = (TextView) convertView
                    .findViewById(R.id.tv_packageInfo);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ChildViewHolder) convertView.getTag();
        }
        Log.d("EX String", child.get(groupPosition).get(childPosition));
        holder.packageInfoTextView.setText(child.get(groupPosition).get(childPosition));
        return convertView;

    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return group.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return group.size();
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
        holder.groupTextView.setText(group.get(groupPosition));
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
