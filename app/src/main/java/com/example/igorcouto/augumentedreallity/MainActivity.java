package com.example.igorcouto.augumentedreallity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
echo "# android-opengles2-ar" >> README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/igor-couto/android-opengles2-ar.git
git push -u origin master
 */

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView myGLSurfaceView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        setContentView(R.layout.activity_main);


        // Cria a view do OpenGL
        myGLSurfaceView = new CamGLSurfaceView(this);
        // Diz a versão do OpenGl a ser utilizada
        // myGLSurfaceView.setEGLContextClientVersion(2);
        // Configuração dos atributos do OpenGL ( Tamanho dos canais de cor, depth buffer e stencil )
        //myGLSurfaceView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0);

        setContentView(myGLSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

}