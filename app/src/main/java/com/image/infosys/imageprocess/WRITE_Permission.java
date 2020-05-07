package com.image.infosys.imageprocess;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class WRITE_Permission extends AppCompatActivity{

    public static final int STORAGE_PERMISSION_REQUEST_CODE= 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted Read Permission", Toast.LENGTH_LONG).show();  //do what you want
                        //do what you want;
                    }
                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "Not Granted Read Permission", Toast.LENGTH_LONG).show();  //do what you want
                    }
                }
                break;
            default:
                break;
        }
    }

    public boolean isStoragePermissionGranted(final Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            }
            else{
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;

        }
    }
}
