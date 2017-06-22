package com.hpeprint;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private Button print;
    private String TAG = MainActivity.class.getSimpleName();
    private File folder;
    private String folderName = "printApp";
    private String fileName = "test.pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void setListener() {
        print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openHpeprint(MainActivity.this, getPdfFile());
            }
        });
    }

    private void initView() {
        print = (Button) findViewById(R.id.print);
    }

    public static void openHpeprint(Context mContext, File file) {
        if (file.exists()) {
            Intent emailIntent = new Intent();
            emailIntent.setAction(Intent.ACTION_SEND);
             /*   Uri uri = Uri.fromFile(file);*/
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
            emailIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
            emailIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
            // set the type to 'email'
            emailIntent.setType("vnd.android.cursor.dir/email");
            emailIntent.setPackage("com.google.android.gm");
            String to[] = {"piyush.dubey@oodlestechnologies.com"};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
// the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            try {
                mContext.startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Log.e("", "No Application Available to View Pdf");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public File getPdfFile() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions
            if(!checkPermissions()){
                requestPermission();
            }else{
                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), folderName);
                Log.e(TAG, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName());
            }
        } else {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            folder = new File(extStorageDirectory, folderName);
        }
        File pdfFile = new File(folder, fileName);
        return pdfFile;
    }

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED ;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(
                new String[]{ READ_EXTERNAL_STORAGE},1);
    }

}
