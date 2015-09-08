package com.divapps.aipok.devclub.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.divapps.aipok.devclub.R;
import com.divapps.aipok.devclub.fragments.FeedListFragment;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_VIEW_REPRESENTATION = "key_ui";
    public static final String KEY_TYPE_REPRESENTATION = "key_data_binding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.top_logo);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setTitle(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_select_view);
        if(getPresentedAsGrid()){
            menuItem.setTitle(getString(R.string.action_list));
            menuItem.setIcon(R.drawable.action_list);
        }else {
            menuItem.setTitle(getString(R.string.action_grid));
            menuItem.setIcon(R.drawable.action_grid);
        }

        menuItem = menu.findItem(R.id.action_select_type);
        if(getType()){
            menuItem.setTitle(getString(R.string.action_list));
            menuItem.setIcon(R.drawable.ic_action_data_binding_enabled);
        }else {
            menuItem.setTitle(getString(R.string.action_grid));
            menuItem.setIcon(R.drawable.ic_action_data_binding_disabled);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_view) {
            boolean current = getPresentedAsGrid();
            SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pm.edit();
            editor.putBoolean(KEY_VIEW_REPRESENTATION, !current);
            editor.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    supportInvalidateOptionsMenu();
                }
            });
            FeedListFragment fragment = (FeedListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            fragment.updateCollectionView();
            return true;
        }else if (id == R.id.action_select_type){
            boolean current = getType();
            SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pm.edit();
            editor.putBoolean(KEY_TYPE_REPRESENTATION, !current);
            editor.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    supportInvalidateOptionsMenu();
                }
            });
            FeedListFragment fragment = (FeedListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            fragment.updateCollectionView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean getPresentedAsGrid() {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
        return pm.getBoolean(KEY_VIEW_REPRESENTATION, false);
    }

    private boolean getType(){
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
        return pm.getBoolean(KEY_TYPE_REPRESENTATION, false);
    }
}
