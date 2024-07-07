package com.example.videoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VideoActivity extends AppCompatActivity {

    static final int peticion_acceso_camara = 101;
    static final int peticion_captura_video = 103;

    String currentVideoPath;
    Button btnCaptura, btnGuardar;
    VideoView videoView;
    Uri videoURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCaptura=findViewById(R.id.btnCaptura);
        btnGuardar=findViewById(R.id.btnGuardar);
        videoView = findViewById(R.id.videoView);

        btnCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, peticion_acceso_camara);
        }else{
            recordVideo();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == peticion_acceso_camara){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recordVideo();
            }else{
                Toast.makeText(getApplicationContext(), "Acceso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void recordVideo() {
        Intent record = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (record.resolveActivity(getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
            }
            if (videoFile != null) {
                videoURI = FileProvider.getUriForFile(this,
                        "com.example.pm0120242p.fileprovider",
                        videoFile);
                record.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                Log.i("Video path", currentVideoPath);
                startActivityForResult(record, peticion_captura_video);
            }
    }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == peticion_captura_video && resultCode == RESULT_OK){
            if(data != null){
            }
        }
    }

    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        currentVideoPath = video.getAbsolutePath();
        return video;
    }

    private void save() {
                Toast.makeText(this, "Video guardado en: " + currentVideoPath, Toast.LENGTH_LONG).show();
            }
    }


