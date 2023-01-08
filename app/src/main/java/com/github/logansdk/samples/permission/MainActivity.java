package com.github.logansdk.samples.permission;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.logansdk.permission.CheckPermissionListener;
import com.github.logansdk.permission.PermissionManager;
import com.github.logansdk.permission.RequirePermissionListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private String[] permissions;
    private TextView tvResult;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultListener resultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (resultListener != null)
                resultListener.onResult(result);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        else
            permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        tvResult = findViewById(R.id.tv_result);

        Button btnCheck = findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(onCheckListener);

        Button btnRequest = findViewById(R.id.btn_request);
        btnRequest.setOnClickListener(onRequestListener);

        Button btnRequire = findViewById(R.id.btn_require);
        btnRequire.setOnClickListener(onRequireListener);
    }

    // Check Permission
    private final View.OnClickListener onCheckListener = v ->
    {
        // It only checks the status for granted and denied of the requested permission. (No permission request dialog)
        PermissionManager.with(this, permissions).check(new CheckPermissionListener()
        {
            @Override
            public void onResult(ArrayList<String> granted, ArrayList<String> denied)
            {
                StringBuilder sb = new StringBuilder();

                // Permission granted
                if (!granted.isEmpty())
                    sb.append("[granted]\n").append(granted).append("\n\n");

                // Permission denied
                if (!denied.isEmpty())
                    sb.append("[denied]\n").append(denied);

                tvResult.setText(sb.toString());
            }
        });
    };

    // Request Permission
    private final View.OnClickListener onRequestListener = v ->
    {
        // After all permission processing is completed, the result value for granted, denied, rejected.
        PermissionManager.with(this, permissions).check((granted, denied, rejected) ->
        {
            StringBuilder sb = new StringBuilder();

            // Permission granted
            if (!granted.isEmpty())
                sb.append("[granted]\n").append(granted).append("\n\n");

            // Permission denied
            if (!denied.isEmpty())
                sb.append("[denied]\n").append(denied).append("\n\n");

            // Permission rejected
            if (!rejected.isEmpty())
                sb.append("[rejected]\n").append(rejected);

            tvResult.setText(sb.toString());
        });
    };

    // Require Permission
    private final View.OnClickListener onRequireListener = v ->
    {
        // After all permission processing is completed, one of the three events below is called.
        //  - onGranted : All rights granted
        //  - onDenied : At least one permission is denied
        //  - onRejected : At least one permission is rejected
        PermissionManager.with(this, permissions).check(new RequirePermissionListener()
        {
            @Override
            public void onGranted()
            {
                // All rights granted
                String message = "All rights granted!";
                tvResult.setText(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(ArrayList<String> denied)
            {
                // At least one permission is denied
                String message = "At least one permission is denied.\n\n[denied]\n" + denied;
                tvResult.setText(message);
                Toast.makeText(MainActivity.this, "Permissions must be granted.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRejected(ArrayList<String> denied, ArrayList<String> rejected)
            {
                // At least one permission is rejected
                // Denied permission without ask never again
                StringBuilder sb = new StringBuilder();
                sb.append("At least one permission is rejected.\n\n");

                if (!denied.isEmpty())
                    sb.append("[denied]\n").append(denied).append("\n\n");

                sb.append("[rejected]\n").append(rejected);
                tvResult.setText(sb.toString());

                // The permission request dialog box can no longer be displayed
                if (denied.isEmpty())
                    showPermissionRationale(rejected);
                else
                    Toast.makeText(MainActivity.this, "Permissions must be granted.", Toast.LENGTH_SHORT).show();
            }
        });
    };

    private void showPermissionRationale(ArrayList<String> rejected)
    {
        PermissionUtils.showPermissionInfo(MainActivity.this, rejected, (dialog, which) ->
        {
            resultListener = result -> PermissionManager.with(MainActivity.this, rejected.toArray(new String[]{})).check((granted, denied) ->
            {
                String message;

                if (denied.isEmpty())
                {
                    message = "All rights granted!";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    message = "At least one permission is rejected.\n\n[rejected]\n" + denied;
                    Toast.makeText(MainActivity.this, "Permissions must be granted.", Toast.LENGTH_SHORT).show();
                }

                tvResult.setText(message);
            });

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            resultLauncher.launch(intent);
        });
    }

    interface ActivityResultListener
    {
        void onResult(ActivityResult result);
    }
}