package com.crsc.nfctest.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.crsc.nfctest.R;
import com.crsc.nfctest.adapter.RecordAdapter;
import com.crsc.nfctest.model.Record;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<Record> records;
    private ListView recordList;
    private RecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_record);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                Intent intent;
                switch (item.getItemId()){
                    case R.id.nav_test:
                        intent = new Intent(RecordActivity.this, TestActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_record:
                        break;
                    case R.id.nav_about:
                        intent = new Intent(RecordActivity.this, AboutActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_close:
                        finish();
                        break;
                    default:
                }
                return true;
            }
        });

        records = new ArrayList<>();
        loadRecords();

        adapter = new RecordAdapter(RecordActivity.this, R.layout.record_item, records);
        recordList = (ListView) findViewById(R.id.record_list);
        recordList.setAdapter(adapter);
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(position);
                Intent intent = new Intent(RecordActivity.this, RecordDetailActivity.class);
                intent.putExtra("tag_id", record.getTagId());
                startActivity(intent);
                finish();
            }
        });
    }


    private void loadRecords(){
        records = DataSupport.findAll(Record.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }
}
