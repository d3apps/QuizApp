package com.dennisdavydov.quizapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.listeners.ListItemClickListener;
import com.dennisdavydov.quizapp.models.notification.NotificationModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context mContext;

    private ArrayList<NotificationModel> mNotificationList;
    private ListItemClickListener mListItemClickListener;

    public NotificationAdapter(Context mContext, ArrayList<NotificationModel> mNotificationList) {
        this.mContext = mContext;
        this.mNotificationList = mNotificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new ViewHolder(view, viewType, mListItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = mNotificationList.get(position).getTitle();
        String message = mNotificationList.get(position).getMessage();

        if (title != null) {
            if (mNotificationList.get(position).isUnread()) {
                holder.tvTitle.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            }
            holder.tvTitle.setText(title);
            holder.tvSubTitle.setText(message);
        }

    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle, tvSubTitle;
        private ImageView imageView;

        private ListItemClickListener itemClickListener;

        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvSubTitle = itemView.findViewById(R.id.tv_noti_sub_title);
            imageView = itemView.findViewById(R.id.img_noti);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(),view);
            }
        }
    }
    public void setmItemClickListener (ListItemClickListener itemClickListener) {
        this.mListItemClickListener =itemClickListener;
    }

}
