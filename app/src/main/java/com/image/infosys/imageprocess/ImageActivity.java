package com.image.infosys.imageprocess;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class ImageActivity extends AppCompatActivity {

    public static final String TESS_DATA = "/tessdata/";
    private static final String TAG = ImageActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private TessBaseAPI tessBaseAPI;

    private static int RESULT_LOAD_IMAGE = 1;

    static {
        System.loadLibrary("opencv_java4");
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCv Configured Successfully");
        }
        else{
            Log.d(TAG, "OpenCv not Configured Successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Button buttonClick = (Button) findViewById(R.id.uploadButton);
//        try {
//            AssetManager assetManager = getAssets();
//            System.out.println("Asset Manager: " + assetManager.toString());
//            String fileList[] = assetManager.list("");
//            System.out.println("Assets File : " + fileList.length);
//            for (int i = 0; i < fileList.length; i++) {
//                Log.d("Files", "FileName:" + fileList[i]);
//            }
//        }
//        catch(IOException e){
//            System.out.println(e.toString());
//        }

        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permission permission = new Permission(ImageActivity.this);
//                WRITE_Permission write_permission = new WRITE_Permission();
//                boolean value = permission.isStoragePermissionGranted(ImageActivity.this);
//                boolean w_value = write_permission.isStoragePermissionGranted(ImageActivity.this);
                if (permission.checkAndRequestPermissions()){
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );

                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            System.out.println("Picture Path : "+picturePath);
//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            prepareTessData();
            startOCR(picturePath);
            OpenCVTest(picturePath);
//            Tess_OCR tess_ocr = new Tess_OCR(picturePath, textView, ImageActivity.this);

//            String path = Environment.getExternalStorageDirectory().toString();
//            System.out.println(path);
//            Log.d("Files", "Path: " + path);
//            File directory = new File(path);
//            File[] files = directory.listFiles();
//            Log.d("Files", "Size: "+ files.length);
//            for (int i = 0; i < files.length; i++)
//            {
//                Log.d("Files", "FileName:" + files[i].getName());
//            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/Created");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {

                File file = new File(Environment.getExternalStorageDirectory() + "/Created", "abc.jpg");
                if (file.exists()) {
                    file.delete();
                }


                if (!file.exists()) {
                    try {
                        file.createNewFile();
                        FileChannel src = new FileInputStream(picturePath).getChannel();
                        FileChannel dst = new FileOutputStream(file).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Do something on success
            } else {
                // Do something else on failure
            }


        }
    }

//    public static final int STORAGE_PERMISSION_REQUEST_CODE= 1;
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case STORAGE_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    // check whether storage permission granted or not.
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(this, "Granted Read Permission", Toast.LENGTH_LONG).show();  //do what you want
//                        //do what you want;
//                    }
//                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
//                        Toast.makeText(this, "Not Granted Read Permission", Toast.LENGTH_LONG).show();  //do what you want
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }

    private void prepareTessData(){
        System.out.println("mCurrentPhotoPath : "+DATA_PATH +TESS_DATA);
        try{

//            System.out.println(getExternalFilesDir(DATA_PATH+TESS_DATA));
//            System.out.println("mCurrentPhotoPath : "+getExternalFilesDir(DATA_PATH+TESS_DATA));
//            File dir = getExternalFilesDir(DATA_PATH+TESS_DATA);
//            File dir = new File(DATA_PATH+TESS_DATA);
            File folder = new File(Environment.getExternalStorageDirectory() + "/Tess"+TESS_DATA);
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
                AssetManager assetManager = getAssets();
                System.out.println("Asset Manager: " +assetManager.toString());
                String fileList[] = assetManager.list("");
                System.out.println("Assets File : " + fileList.length);
                for (int i = 0; i < fileList.length; i++)
                {
                    Log.d("Files", "FileName:" + fileList[i]);
                }
                for (String fileName : fileList) {
                    String pathToDataFile = Environment.getExternalStorageDirectory() + "/Tess" +TESS_DATA+ "/" + fileName;
                    System.out.println("Data File : " + pathToDataFile);

                    if (!(new File(pathToDataFile)).exists()) {

                        InputStream in = getAssets().open(fileName);
                        OutputStream out = new FileOutputStream(pathToDataFile);
                        byte[] buff = new byte[1024];
                        int len;
                        while ((len = in.read(buff)) > 0) {
                            out.write(buff, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
            }
            else{

            }
        } catch (Exception e) {

            Log.e(TAG+" Msg", e.getMessage());
        }
    }

    private void startOCR(String mCurrentPhotoPath){
        System.out.println("Picture Path : "+mCurrentPhotoPath);
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            FileInputStream fis = new FileInputStream(mCurrentPhotoPath);
//            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            String result = this.getText(bitmap);
            System.out.println("Result : "+result);
            TextView textView = (TextView) findViewById(R.id.textView);
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
        tessBaseAPI.init(DATA_PATH+"/", "eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text().replaceAll("[^A-Za-z0-9,.# ]+", "");

        }catch (Exception e){
            Log.e(TAG + " getText ", e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    private void OpenCVTest(String picturepath){
//        String inputFileName="simm_01";
//        String inputExtension = "jpg";
//        String inputDir = getCacheDir().getAbsolutePath();  // use the cache directory for i/o
//        String outputDir = getCacheDir().getAbsolutePath();
//        String outputExtension = "png";
//        String inputFilePath = inputDir + File.separator + inputFileName + "." + inputExtension;
//
//
//        Log.d (this.getClass().getSimpleName(), "loading " + inputFilePath + "...");
        Mat image = Imgcodecs.imread(picturepath);
//        Log.d (this.getClass().getSimpleName(), "width of " + inputFileName + ": " + image.width());
// if width is 0 then it did not read your image.


// for the canny edge detection algorithm, play with these to see different results
        int threshold1 = 70;
        int threshold2 = 100;

        Mat im_canny = new Mat();  // you have to initialize output image before giving it to the Canny method
        Imgproc.Canny(image, im_canny, threshold1, threshold2);
//        String cannyFilename = outputDir + File.separator + inputFileName + "_canny-" + threshold1 + "-" + threshold2 + "." + outputExtension;
//        Log.d (this.getClass().getSimpleName(), "Writing " + cannyFilename);
//        Imgcodecs.imwrite(cannyFilename, im_canny);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        imageView.setImageMatrix(im_canny);
//        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        Bitmap bm = Bitmap.createBitmap(im_canny.cols(), im_canny.rows(),Bitmap.Config.ARGB_8888);
        // convert Mat image to bitmap::
        Utils.matToBitmap(im_canny, bm);
        //setting the image
        imageView.setImageBitmap(bm);
    }




//    public boolean isStoragePermissionGranted(final Activity activity) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                return true;
//            }
//            else{
//                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    return false;
//                }
//            }
//        else {
//            return true;
//
//        }
//    }
}
