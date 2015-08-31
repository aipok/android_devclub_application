package com.divapps.aipok.devclub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_VIEW_REPRESENTATION = "key_ui";

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
//            Drawable drawable;
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                drawable = getResources().getDrawable(R.drawable.action_bar_bg, getTheme());
//            else
//                drawable = getResources().getDrawable(R.drawable.action_bar_bg);
//            if(drawable != null) {
//                Drawable dr = drawable.mutate();
//                dr.setAlpha(150);
//                ab.setBackgroundDrawable(dr);
//            }
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
        final MenuItem menuItem = menu.findItem(R.id.action_select_view);
        if(getPresentedAsGrid()){
            menuItem.setTitle(getString(R.string.action_list));
            menuItem.setIcon(R.drawable.action_list);
        }else {
            menuItem.setTitle(getString(R.string.action_grid));
            menuItem.setIcon(R.drawable.action_grid);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean getPresentedAsGrid() {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
        return pm.getBoolean(KEY_VIEW_REPRESENTATION, false);
    }
}
