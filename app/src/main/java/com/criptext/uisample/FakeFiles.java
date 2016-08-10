package com.criptext.uisample;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gesuwall on 8/10/16.
 */
public class FakeFiles {
    Context ctx;

    public FakeFiles(Context ctx){
        this.ctx = ctx;
    }
    public static final String defaultAudioFilepath(Context ctx){
        return ctx.getCacheDir() + "/barney.aac";
    }

    public static final String defaultImageFilepath(Context ctx) {
        return ctx.getCacheDir() + "/criptext.png";
    }

    public static final String defaultImageBlurFilepath(Context ctx) {
        return ctx.getCacheDir() + "/criptext_blur.png";
    }

    /**
     * if photo files do not exist, create them
     */
    void createImageFile(){
        File file = new File(defaultImageFilepath(ctx));
        if(!file.exists()){
            try {
                InputStream ins = ctx.getResources().openRawResource(R.raw.criptext_logo);
                FileOutputStream outputStream = new FileOutputStream(file.getPath());

                byte buf[] = new byte[1024];
                int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file = new File(defaultImageBlurFilepath(ctx));
        if(!file.exists()){
            try {
                InputStream ins = ctx.getResources().openRawResource(R.raw.criptext_logo_blur);
                FileOutputStream outputStream = new FileOutputStream(file.getPath());

                byte buf[] = new byte[1024];
                int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * If audio files don't exist, create them.
     */
    void createAudioFile(){
        File file = new File(defaultAudioFilepath(ctx));
        if(!file.exists()){
            try {
            InputStream ins = ctx.getResources().openRawResource(R.raw.barney);
            FileOutputStream outputStream = new FileOutputStream(file.getPath());

            byte buf[] = new byte[1024];
            int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
