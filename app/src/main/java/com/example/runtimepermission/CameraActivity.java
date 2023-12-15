package com.example.runtimepermission;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.runtimepermission.databinding.ActivityCameraBinding;

import java.sql.Time;
import java.util.Random;

public class CameraActivity extends AppCompatActivity {

    CameraSelector cameraSelector;
    public static final String TAG = "MainActivity";
    public static final int CAMERA_PERMISSION_CODE = 1;
    boolean is_camera_permitted = false;

    String[] required_permissions = new String[]{Manifest.permission.CAMERA};

    ActivityCameraBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.camerapermissionbtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!allPermissionResultCheck33()) {
                    requestPermissionCamera();
                } else {
                    startCamera();
                }
            } else {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    startCamera();
                }
            }
        });
    }

    private void requestPermissionCamera() {
        if (ContextCompat.checkSelfPermission(CameraActivity.this, required_permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, required_permissions[0] + " Granted");
            is_camera_permitted = true;
        } else {
            request_permission_launcher_camera.launch(required_permissions[0]);
        }
    }

    private boolean allPermissionResultCheck33() {
        return is_camera_permitted;
    }

    private ActivityResultLauncher<String> request_permission_launcher_camera = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Log.e(TAG, required_permissions[0] + " Granted");
            is_camera_permitted = true;
            startCamera();
        } else {
            Log.e(TAG, required_permissions[0] + " Not Granted");
            is_camera_permitted = false;
            sendToSettingDialog();
        }
    });

    private void sendToSettingDialog() {
        new AlertDialog.Builder(CameraActivity.this).setTitle("Alert for Permissions").setCancelable(false).setMessage("Go to Settings for Permission").setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int microphonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && microphonePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: " + " Permission Granted ");
                startCamera();
            } else {
                Log.e(TAG, "onRequestPermissionsResult: " + " Permission Denied ");
                sendToSettingDialog();
            }
        }
    }

    private void startCamera() {
        ImageCapture.Builder builder = new ImageCapture.Builder();
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        binding.viewFinder.bindToLifecycle((LifecycleOwner) CameraActivity.this);
        binding.swichCamera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onClick(View view) {
                if (binding.viewFinder.isRecording()) {
                    return;
                }
                if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                binding.viewFinder.toggleCamera();
            }
        });
    }


}