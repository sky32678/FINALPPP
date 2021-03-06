package com.example.sky32.finalppp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SharedMemory;
import android.provider.Settings;
import android.renderscript.ScriptC;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sky32.finalppp.R;
import com.example.sky32.finalppp.ShowCamera;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Camera camera;
    CameraSource cameraSource;
    ShowCamera showCamera;
   // FrameLayout frameLayout;
    SurfaceView surfaceView;
    Button takepic, stop_timer, choosePic;
    TextView text1, textview, textUri;
    final int RequestCameraPermission = 1001;

    private static final String FORMAT = "%02d:%02d:%02d";
    /////input section
    String input;
    EditText editTextinput;

    /////////////////////////////////Picture stuffs




    //Callback
    //Callback
    Camera.PictureCallback PictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = savePhoto();

            if (pictureFile == null) {
                return;
            } else {
                try {
                    FileOutputStream f = new FileOutputStream(pictureFile);
                    f.write(data);
                    f.close();
                    camera.startPreview();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /////save pics
    private File savePhoto() {
        String s = Environment.getExternalStorageState();
        if (!s.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "HI");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File outputFile = new File(folder, "hi.jpg");
            return outputFile;
        }
    }


    boolean isWorking = true;
    public void startTakingPhoto(View v) {
        if (camera != null) {
            camera.takePicture(null, null, PictureCallback);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takepic = (Button) findViewById(R.id.takepicture);
        takepic.setOnClickListener(onClickListener_takepic);

        editTextinput = findViewById(R.id.inputText) ;



        ///////cameera stuff
        surfaceView = findViewById(R.id.surfaceView);
        textview = findViewById(R.id.textView);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {

        } else {
            cameraSource = new CameraSource.Builder
                    (getApplicationContext(), textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1200, 1200).setAutoFocusEnabled(true).setRequestedFps(3.0f)
                    .build();
            showCamera = new ShowCamera(this,camera);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try { if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, RequestCameraPermission);
                             return;
                                               }
                        cameraSource.start(surfaceView.getHolder());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                           }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    camera.stopPreview();
                    camera.release();
                }
            });
        }
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }
            View v;

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0 && !isWorking) {
                    textview.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i <items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                ///////if words on camera = input, it will be shown on the screen
                                if (item.getValue().equals(input) && !isWorking)
                                {

                                      stringBuilder.append(item.getValue());
                                     stringBuilder.append("\n");

                                    ////stop searching
                                      isWorking = true;
                                }

                            }
                            ////show on the screen
                            textview.setText(stringBuilder.toString());
                        }
                    });
                }

            }
        });



        ////set ID
        //takepic = findViewById(R.id.tackpic);


        choosePic = findViewById(R.id.gallay);


    }

    /////button
    View.OnClickListener onClickListener_takepic = new View.OnClickListener()
    {

        @Override

        public void onClick(View v)

        {   //search again
            isWorking = false;
            //input
        input = editTextinput.getText().toString();


        }

    };

}










