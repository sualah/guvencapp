package com.tr.guvencmakina.guvencapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtil {


    public static  Bitmap getImageBitmapFromBase64(String base64){
        Bitmap decoded_bitmapp = null;
        if( base64!= null){
            try {
                byte[] decoded_img_byte = Base64.decode(base64, Base64.DEFAULT);
                decoded_bitmapp = BitmapFactory.decodeByteArray(decoded_img_byte, 0, decoded_img_byte.length);
            }catch (Exception e){
                decoded_bitmapp = null;
                System.out.println("getImageBitmapFromBase64 exception " + e.getMessage());
            }

           }

       return decoded_bitmapp;
    }

    public static  String getBase64FromImageBitmap(Bitmap bitmap){
        String imageString = null;
        if(bitmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return  imageString;
    }
}
