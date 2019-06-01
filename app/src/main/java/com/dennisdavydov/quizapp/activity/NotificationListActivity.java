package com.dennisdavydov.quizapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.adapters.NotificationAdapter;
import com.dennisdavydov.quizapp.constants.AppConstants;
import com.dennisdavydov.quizapp.data.sqlite.NotificationDbController;
import com.dennisdavydov.quizapp.listeners.ListItemClickListener;
import com.dennisdavydov.quizapp.models.notification.NotificationModel;
import com.dennisdavydov.quizapp.utilities.ActivityUtilities;
import com.dennisdavydov.quizapp.utilities.DialogUtilities;

import java.util.ArrayList;

public class NotificationListActivity extends BaseActivity implements DialogUtilities.OnCompleteListener {

    private Context mContext;
    private Activity mActivity;

    private RecyclerView mRecycler;
    private NotificationAdapter mNotificationAdapter;
    private ArrayList<NotificationModel> mNotificationList;
    private MenuItem mMenuItemDeleteAll;
    private NotificationDbController mNotificationDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mActivity = NotificationListActivity.this;
        mContext = mActivity.getApplicationContext();

        initVar();
        initView();
        initFunctionality();
        initListener();

    }

    private void initVar() {
        mNotificationList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_notification);

        mRecycler = findViewById(R.id.rv_recycler);
        mNotificationAdapter = new NotificationAdapter(mActivity,mNotificationList);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setAdapter(mNotificationAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getResources().getString(R.string.notifications));
        enableUpButton();
    }

    private void initFunctionality() {
        //TODO show banner ads
    }

    private void updateUI() {
        showLoader();
        if (mNotificationDbController == null) {
            mNotificationDbController = new NotificationDbController(mContext);
        }
        mNotificationList.clear();
        mNotificationList.addAll(mNotificationDbController.getAllData());
        mNotificationAdapter.notifyDataSetChanged();

        hideLoader();

        if (mNotificationList.size() == 0) {
            showEmptyView();
            if (mMenuItemDeleteAll !=null) {
                mMenuItemDeleteAll.setVisible(false);
            }
        } else {
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(true);
            }
        }
    }

    private void initListener() {
        //recycler list item click listener
        mNotificationAdapter.setmItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mNotificationDbController.updateStatus(mNotificationList.get(position).getId(),true);

                ActivityUtilities.getInstance().invokeCustomUrlActivity(mActivity,CustomUrlActivity.class,
                        mNotificationList.get(position).getTitle(),
                        mNotificationList.get(position).getUrl(),
                        false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home: {
                finish();
                return true;
            }
            case R.id.menus_delete_all: {
                FragmentManager manager = getSupportFragmentManager();
                DialogUtilities dialog = DialogUtilities.newInstance(
                        getResources().getString(R.string.notifications),
                        getResources().getString(R.string.delete),
                        getResources().getString(R.string.yes),
                        getResources().getString(R.string.no),
                        AppConstants.BUNDLE_KEY_DELETE_ALL_NOT);
                dialog.show(manager,AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
            }
        }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_all,menu);
        mMenuItemDeleteAll = menu.findItem(R.id.menus_delete_all);

        updateUI();

        return true;
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {

    }
}
