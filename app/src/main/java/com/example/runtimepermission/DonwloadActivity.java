package com.example.runtimepermission;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.example.runtimepermission.databinding.ActivityDonwloadBinding;

public class DonwloadActivity extends AppCompatActivity {
    private long downloadId;
    private DownloadManager downloadManager;
    ProgressDialog progressDialog;
    ActivityDonwloadBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonwloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMessage("File is downloading ...");
        progressDialog.setCancelable(false);

        binding.actionDown.setOnClickListener(v -> {
            startDownload("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        });
    }

    private void startDownload(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        String currentime = getString(R.string.app_name) + " " + System.currentTimeMillis() + ".mp4";
        request.setDestinationInExternalPublicDir(Environment.getExternalStorageState(), "RUNTIME PERMISSION" + "/" + currentime);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadId = downloadManager.enqueue(request);
        progressDialog.show();

    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (receivedDownloadId == downloadId) {
                progressDialog.dismiss();
                Toast.makeText(DonwloadActivity.this, "Download complete", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }
}