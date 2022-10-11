package com.tds.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList;
    private ArrayList<ArrayList<String>> childList;
    private ArrayList<ArrayList> iconChildList;

    private Context mContext;

    MyBaseExpandableListAdapter(ArrayList<String> groupList,
                                ArrayList<ArrayList<String>> childList,
                                ArrayList<ArrayList> iconChildList,
                                Context mContext) {
        this.groupList = groupList;
        this.childList = childList;
        this.iconChildList = iconChildList;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup groupHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_group, parent, false);
            groupHolder = new ViewHolderGroup();
            groupHolder.tv_group_name = (TextView) convertView.findViewById(R.id.type_name);
            groupHolder.group_icon = (ImageView) convertView.findViewById(R.id.group_icon);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.tv_group_name.setText(groupList.get(groupPosition));

        if (isExpanded) {
            groupHolder.group_icon.setBackgroundResource(R.mipmap.bg_arrow_up);
        } else {
            groupHolder.group_icon.setBackgroundResource(R.mipmap.bg_arrow_down);
        }
        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_children, parent, false);
            itemHolder = new ViewHolderItem();
            itemHolder.img_icon = (ImageView) convertView.findViewById(R.id.sdk_icon);
            itemHolder.tv_name = (TextView) convertView.findViewById(R.id.sdk_name);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewHolderItem) convertView.getTag();
        }

        itemHolder.img_icon.setImageResource((int)iconChildList.get(groupPosition).get(childPosition));
        itemHolder.tv_name.setText(childList.get(groupPosition).get(childPosition));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, childList.get(groupPosition).get(childPosition));
            }
        });

        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ViewHolderGroup{
        private TextView tv_group_name;
        private ImageView group_icon;
    }

    private static class ViewHolderItem{
        private ImageView img_icon;
        private TextView tv_name;
    }

    private MyBaseExpandableListAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(MyBaseExpandableListAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, String SDKType);
    }

}