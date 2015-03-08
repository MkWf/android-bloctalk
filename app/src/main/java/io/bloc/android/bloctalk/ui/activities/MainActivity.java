package io.bloc.android.bloctalk.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import io.bloc.android.bloctalk.R;

/**
 * Created by Mark on 3/8/2015.
 */
public class MainActivity extends ActionBarActivity {

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_main);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        toolbar.setTitle("Bloctalk");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
