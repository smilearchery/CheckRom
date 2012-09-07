
package com.dianxinos.checkromdemo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PackageAdapter extends BaseAdapter {

    private Context ctx;
    private PackagesEntity pEntity;
    private List<PackagesEntity> list;
    private LayoutInflater mInflater;

    static class ViewHolder
    {
        public ImageView appLogoImageView;
        public TextView appNameTextView;
        public TextView notShowReasonTextView;
    }

    public PackageAdapter(Context ctx, List<PackagesEntity> list) {
        this.ctx = ctx;
        this.list = list;
        this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.package_list, null);
            holder.appLogoImageView = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.appNameTextView = (TextView) convertView.findViewById(R.id.tv_appName);
            holder.notShowReasonTextView = (TextView) convertView.findViewById(R.id.tv_notShowReason);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.appLogoImageView.setImageDrawable(list.get(position).getAppLogo());
        holder.appNameTextView.setText(list.get(position).getName());
        holder.notShowReasonTextView.setText(list.get(position).getReason());
        return convertView;
    }

}
