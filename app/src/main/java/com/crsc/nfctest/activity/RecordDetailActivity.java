package com.crsc.nfctest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.crsc.nfctest.R;
import com.crsc.nfctest.model.Record;
import com.crsc.nfctest.util.NfcUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 测试记录
 */
public class RecordDetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    private EditText tagId;
    private EditText tagType;
    private EditText testDate;
    private EditText isComplete;
    private EditText testCount;
    private EditText testResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        //初始化界面控件
        tagId = (EditText) findViewById(R.id.tag_id);
        tagType = (EditText) findViewById(R.id.tag_type);
        testDate = (EditText) findViewById(R.id.test_date);
        isComplete = (EditText) findViewById(R.id.is_complete);
        testCount = (EditText) findViewById(R.id.test_count);
        testResult = (EditText) findViewById(R.id.test_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //为NavigationView添加点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_test:
                        intent = new Intent(RecordDetailActivity.this, TestActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_record:
                        intent = new Intent(RecordDetailActivity.this, RecordActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_about:
                        intent = new Intent(RecordDetailActivity.this, AboutActivity.class);
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

        //获取标签ID，并在界面显示相应信息
        String tagIdString = getIntent().getStringExtra("tag_id");
        showTestInfo(tagIdString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    /**
     * 根据标签ID在界面展示对应记录的详细信息
     * @param id 标签ID
     */
    private void showTestInfo(String id) {
        List<Record> records = DataSupport.where("tagId = ?", id).find(Record.class);
        if (records.size() > 0) {
            Record record = records.get(0);
            tagId.setText(record.getTagId());
            tagType.setText(record.getTpye());
            testDate.setText(record.getDate());
            testCount.setText(record.getCount().toString());
            if (record.getResult() == 1)
                testResult.setText("通过");
            else
                testResult.setText("不通过");
            if (record.getIsComplete() == 1)
                isComplete.setText("完整");
            else
                isComplete.setText("不完整");
        } else {
            NfcUtil.toastMessage(RecordDetailActivity.this, "不存在相关数据");
        }

    }
}
