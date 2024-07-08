package com.example.videoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import Repository.Trans;
import Repository.ConexionBD;


public class VideoActivity extends AppCompatActivity {

    static final int peticion_acceso_camara = 101;
    static final int peticion_captura_video = 103;

    String currentVideoPath;
    Button btnCaptura, btnGuardar;
    VideoView videoView;
    Repository.ConexionBD conexion;
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

        conexion = new ConexionBD(this,Trans.db_name, null, Trans.VERSION);
        btnCaptura=findViewById(R.id.btnCaptura);
        btnGuardar=findViewById(R.id.btnGuardar);

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
            startActivityForResult(record, peticion_captura_video);
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == peticion_captura_video && resultCode == RESULT_OK) {
            if (data != null) {
                Uri videoUri = data.getData();
                currentVideoPath = videoUri.getPath();
                Toast.makeText(getApplicationContext(), "¡El video ha sido grabado! ", Toast.LENGTH_LONG).show();
                Log.i("Video path", currentVideoPath);


                videoView = findViewById(R.id.videoView);
                videoView.setVideoURI(videoUri);
                MediaController media=new MediaController(this);
                media.setAnchorView(videoView);
                media.setPadding(0,0,0,520);
                videoView.setMediaController(media);
                videoView.start();

            }
        }
    }



    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(videoFileName, ".mp4", storageDir);
        currentVideoPath = video.getAbsolutePath();
        return video;
    }



    private void save() {
        try {
            File videoFile = createVideoFile();

            Uri videoURI = FileProvider.getUriForFile(this, "com.example.videoapp.fileprovider", videoFile);
            Log.i("Save Video", "Video guardado en: " + videoURI.getPath());
            Toast.makeText(getApplicationContext(), "Video guardado en: " + videoURI.getPath(), Toast.LENGTH_LONG).show();

            String videoBase64 = convertVideoToBase64(videoFile);
            Log.i("Video Base64", "Video en Base64: " + videoBase64);
            saveVideoToDatabase(videoBase64);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error al guardar el video", Toast.LENGTH_LONG).show();
        }
    }

    private String convertVideoToBase64(File videoFile) throws IOException {
        FileInputStream videoStream = new FileInputStream(videoFile);
        ByteArrayOutputStream videoByte = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = videoStream.read(buffer)) != -1) {
            videoByte.write(buffer, 0, bytesRead);
        }
        videoStream.close();
        byte[] videoBytes = videoByte.toByteArray();
        String b64Video = Base64.encodeToString(videoBytes, Base64.DEFAULT);
        Log.d("VideoConversion", "Conversion completa. Longitud de Base64: " + b64Video.length());

        return b64Video;
    }


    private void saveVideoToDatabase(String videoBase64) {
        ConexionBD db = new ConexionBD(this, Trans.db_name, null, Trans.VERSION);
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Trans.VIDEO, videoBase64);
        long newRowId = database.insert(Trans.TBL_VIDEO, null, values);
        database.close();
    }



}





