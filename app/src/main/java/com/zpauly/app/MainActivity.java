package com.zpauly.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    private Toolbar mToolbar;

    private SlideFragment mLeftFragment;
    private SlideFragment mUpFragment;
    private SlideFragment mRightFragment;
    private SlideFragment mDownFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolbar();

        initFragments();

        changeFragment(mLeftFragment);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initFragments() {
        mLeftFragment = SlideFragment.create(SlideFragment.LEFT);
        mUpFragment = SlideFragment.create(SlideFragment.UP);
        mRightFragment = SlideFragment.create(SlideFragment.RIGHT);
        mDownFragment = SlideFragment.create(SlideFragment.DOWN);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.slide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "item title = " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.direction_left:
                changeFragment(mLeftFragment);
                item.setChecked(true);
                break;
            case R.id.direction_up:
                changeFragment(mUpFragment);
                item.setChecked(true);
                break;
            case R.id.direction_right:
                changeFragment(mRightFragment);
                item.setChecked(true);
                break;
            case R.id.direction_down:
                changeFragment(mDownFragment);
                item.setChecked(true);
                break;
            default:
                break;
        }
        return true;
    }
}
