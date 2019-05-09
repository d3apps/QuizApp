package com.dennisdavydov.quizapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.utilities.ActivityUtilities;
import com.dennisdavydov.quizapp.utilities.AppUtilities;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends BaseActivity {

    private Activity activity;
    private Context context;

    private Toolbar toolbar;
    private AccountHeader header = null;
    private Drawer drawer= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = MainActivity.this;
        context = getApplicationContext();

        final IProfile profile =new ProfileDrawerItem().withIcon(R.drawable.ic_dev);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        ActivityUtilities.getInstance()
                                .invokeCustomUrlActivity(activity, CustomUrlActivity.class,
                                        getResources().getString(R.string.site),
                                        getResources().getString(R.string.site_url),false);
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .addProfiles(profile)
                .build();
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.about_app).withIcon(R.drawable.ic_dev).withIdentifier(10).withSelectable(false),

                        new SecondaryDrawerItem().withName(R.string.you_tube).withIcon(R.drawable.ic_youtube).withIdentifier(20).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.facebook).withIcon(R.drawable.ic_facebook).withIdentifier(21).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.twitter).withIcon(R.drawable.ic_twitter).withIdentifier(22).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.googleplus).withIcon(R.drawable.ic_google_plus).withIdentifier(23).withSelectable(false),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(R.drawable.ic_settings).withIdentifier(30).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.rate_us).withIcon(R.drawable.ic_rating).withIdentifier(31).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.share).withIcon(R.drawable.ic_share).withIdentifier(32).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.privacy).withIcon(R.drawable.ic_privacy_policy).withIdentifier(33).withSelectable(false),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.exit).withIcon(R.drawable.ic_dev).withIdentifier(40).withSelectable(false)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null){
                            Intent intent = null;
                            switch (String.valueOf(drawerItem.getIdentifier())){
                                case "10": {
                                    ActivityUtilities.getInstance()
                                            .invokeNewActivity(activity,AboutDevActivity.class,false);
                                }break;
                                case "20": {
                                    AppUtilities.youtubeLink(activity);
                                }break;
                                case "21": {
                                    AppUtilities.facebookLink(activity);
                                }break;
                                case "22": {
                                    AppUtilities.twiterLink(activity);
                                }break;
                                case "23": {
                                    AppUtilities.googlePlusLink(activity);
                                }break;
                                case "30": {
                                    // TODO invoke SettingsActivity
                                }break;
                                case "31": {
                                    AppUtilities.rateThisApp(activity);
                                }break;
                                case "32": {
                                    AppUtilities.shareApp(activity);
                                }break;
                                case "33": {
                                    ActivityUtilities.getInstance().invokeCustomUrlActivity(activity,CustomUrlActivity.class,
                                            getResources().getString(R.string.privacy),
                                            getResources().getString(R.string.privacy_url),false);
                                }break;
                                case "40": {

                                }break;
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withShowDrawerUntilDraggedOpened(true)
                .build();


    }

    @Override
    public void onBackPressed() {
        if (drawer !=null && drawer.isDrawerOpen()){
            drawer.closeDrawer();
        } else {
            AppUtilities.tapPromptToExit(this);
        }
    }
}
