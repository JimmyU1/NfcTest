package com.crsc.nfctest.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.crsc.nfctest.R;
import com.crsc.nfctest.model.Record;
import com.crsc.nfctest.resource.AppResource;
import com.crsc.nfctest.util.NfcUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private final static String formatString = "00000000000000000000000000000000";
    private final static String testString = "11111111111111111111111111111111";
    private final static int testCount = 200;
    private static WakeLock wakeLock;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private IntentFilter[] WriterFilters = null;
    private EditText tagIdEdit;
    private EditText typeEdit;
    private EditText dateEdit;
    private EditText isCompleteEdit;
    private TextView progressText;
    private Record record;

    private int successCount = 0;
    private int failedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_test);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_test:
                        break;
                    case R.id.nav_record:
                        intent = new Intent(TestActivity.this, RecordActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_about:
                        intent = new Intent(TestActivity.this, AboutActivity.class);
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
        //保持屏幕常亮
        keepScreenOn(TestActivity.this, true);

        tagIdEdit = (EditText) findViewById(R.id.tagid_edt);
        typeEdit = (EditText) findViewById(R.id.type_edt);
        dateEdit = (EditText) findViewById(R.id.date_edt);
        isCompleteEdit = (EditText) findViewById(R.id.is_complete_edt);
        progressText = (TextView) findViewById(R.id.test_progress);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addCategory("*/*");
        record = new Record();


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, WriterFilters,
                    new String[][]{{MifareClassic.class.getName()}});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        keepScreenOn(TestActivity.this, false);
    }


    public void onNewIntent(Intent intent) {
        setIntent(intent);
        final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        final String action = intent.getAction();
        final MifareClassic mMifareClassic = MifareClassic.get(tagFromIntent);

        String tagId = mMifareClassic.getTag().getId().toString();
        record.setTagId(tagId);
        tagIdEdit.setText(tagId);
        int type = mMifareClassic.getType();
        String tagType = "";
        switch (type) {
            case MifareClassic.TYPE_CLASSIC:
                tagType = "TYPE_CLASSIC";
                break;
            case MifareClassic.TYPE_PLUS:
                tagType = "TYPE_PLUS";
                break;
            case MifareClassic.TYPE_PRO:
                tagType = "TYPE_PRO";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                tagType = "TYPE_UNKNOWN";
                break;
        }
        typeEdit.setText(tagType);
        record.setTpye(tagType);
        String testDate = formatDate(new Date());
        record.setDate(testDate);
        dateEdit.setText(testDate);

        final ProgressDialog progressDialog = new ProgressDialog(TestActivity.this);
        progressDialog.setTitle("读写测试");
        progressDialog.setMessage("测试中...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Thread thread = new Thread(new Runnable() {
            int sectorNum = 0;
            int blockNum = 0;
            int sectorCount = 0;
            int blockCount = 0;
            boolean authenticate = false;
            int index = 0;
            int testNum = 0;

            @Override
            public void run() {
                while (testNum < testCount) {
                    try {
                        if (mMifareClassic.isConnected())
                            mMifareClassic.close();
                        mMifareClassic.connect();
                        sectorCount = mMifareClassic.getSectorCount();
                        for (sectorNum = 0; sectorNum < sectorCount; sectorNum++) {
                            blockCount = NfcUtil.getBlockNumberBySector(sectorNum);
                            authenticate = mMifareClassic.authenticateSectorWithKeyA(sectorNum,
                                    NfcUtil.hexStringToBytes(AppResource.defaultAuthenticateKey));
                            if (authenticate) {
                                for (blockNum = 0; blockNum < blockCount; blockNum++) {
                                    if (sectorNum == 0 && blockNum == 0)
                                        continue;
                                    mMifareClassic.writeBlock(NfcUtil.blockNumber(sectorNum, blockNum), NfcUtil.hexStringToBytes(testString));
                                    mMifareClassic.writeBlock(NfcUtil.blockNumber(sectorNum, blockNum), NfcUtil.hexStringToBytes(formatString));
                                    if (blockNum == (blockCount - 1)) {
                                        successCount++;
                                    }
                                }
                            } else {
                                failedCount++;
                            }
                            testNum++;
                            if (testNum % 100 == 0) {
                                final String testString = "测试次数：" + testNum + "\t\t成功：" + successCount + "次\t\t失败：" + failedCount + "\n";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressText.append(testString);
                                    }
                                });
                                index++;
                            }
                        }
                        mMifareClassic.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failedCount == 0) {
                            isCompleteEdit.setText("完整");
                            isCompleteEdit.setTextColor(Color.BLUE);
                            record.setIsComplete(1);
                            record.setCount(testCount);
                            if (successCount == testCount)
                                record.setResult(1);
                            else
                                record.setResult(0);
                        } else {
                            isCompleteEdit.setText("不完整");
                            isCompleteEdit.setTextColor(Color.RED);
                            record.setIsComplete(0);
                            record.setCount(testCount);
                            record.setResult(0);
                        }

                        progressDialog.dismiss();
                        AlertDialog.Builder diolag = new AlertDialog.Builder(TestActivity.this);
                        diolag.setTitle("测试结果");
                        diolag.setMessage("测试次数：" + testCount + "\t\t成功：" + successCount + "次\t\t失败：" + failedCount);
                        diolag.setCancelable(false);
                        diolag.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                record.save();
                            }
                        });
                        diolag.show();
                    }
                });
            }
        });
        thread.start();
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

    public static void keepScreenOn(Context context, boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wakeLock.acquire();
        } else {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
