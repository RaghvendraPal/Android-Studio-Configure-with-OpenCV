package com.image.infosys.imageprocess;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Tess_OCR extends AppCompatActivity{
    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = ImageActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private TessBaseAPI tessBaseAPI;
    private final Activity activity;

    public Tess_OCR(String mCurrentPhotoPath, TextView textView, Activity Imageactivity){
        System.out.println("mCurrentPhotoPath : "+DATA_PATH+TESS_DATA);
        this.prepareTessData();
        activity = Imageactivity;
//        this.startOCR(mCurrentPhotoPath, textView);

    }

    private void prepareTessData(){
        System.out.println("mCurrentPhotoPath : "+DATA_PATH +TESS_DATA);
        try{

//            System.out.println(getExternalFilesDir(DATA_PATH+TESS_DATA));
//            System.out.println("mCurrentPhotoPath : "+getExternalFilesDir(DATA_PATH+TESS_DATA));
//            File dir = getExternalFilesDir(DATA_PATH+TESS_DATA);
//            File dir = new File(DATA_PATH+TESS_DATA);
            File folder = new File(Environment.getExternalStorageDirectory() + "/Tess");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
//            if(!dir.isDirectory()){
//                if (!dir.mkdir()) {
//                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
//                }
//            }
            System.out.println("Success ; "+success);
            if (success) {
                AssetManager assetManager = activity.getAssets();
                System.out.println("Asset Manager: " +assetManager.toString());
                String fileList[] = assetManager.list("");
                System.out.println("Assets File : " + fileList.length);
                for (int i = 0; i < fileList.length; i++)
                {
                    Log.d("Files", "FileName:" + fileList[i]);
                }
//                for (String fileName : fileList) {
//                    String pathToDataFile = Environment.getExternalStorageDirectory() + "/Tess" + "/" + fileName;
//                    System.out.println("Data File : " + pathToDataFile);
//
//                    if (!(new File(pathToDataFile)).exists()) {
//                        InputStream in = getAssets().open(fileName);
//                        OutputStream out = new FileOutputStream(pathToDataFile);
//                        byte[] buff = new byte[1024];
//                        int len;
//                        while ((len = in.read(buff)) > 0) {
//                            out.write(buff, 0, len);
//                        }
//                        in.close();
//                        out.close();
//                    }
//                }
            }
            else{

            }
        } catch (Exception e) {

            Log.e(TAG+" Msg", e.getMessage());
        }
    }

    private void startOCR(String mCurrentPhotoPath, TextView textView){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            String result = this.getText(bitmap);
            textView.setText(result);
        }catch (Exception e){
            Log.e(TAG + " Start OCR ", e.getMessage());
        }
    }

    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
//        String dataPath = getExternalFilesDir("/").getPath() + "/";
//        System.out.print("Class Tess DataPath: "+dataPath);
        tessBaseAPI.init(DATA_PATH, "eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG + " getText ", e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

}
