package com.ubcoin.utils

import android.content.Context
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.google.gson.internal.LinkedTreeMap;

import java.io.ByteArrayOutputStream;
import java.io.File
import java.io.FileOutputStream
import java.util.List;
import java.util.Locale;

class ImageUtils {
    companion object {
        fun fixImageOrientation(filepath: String): ByteArray {
            val exifInterface: ExifInterface
            var rotation : Float = 0F
            try {
                exifInterface = ExifInterface(filepath)

                val orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90F
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180F
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270F
                }
            } catch (ex: Exception) {
            }

            val bmp = BitmapFactory.decodeFile(filepath)
            val matrix = Matrix()
            matrix.postRotate(rotation)
            val bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun fixImage(context: Context, filepath: String): String{
            var f = File(context.getCacheDir(), "temp_ubcoin");
            f.createNewFile();

            var fos = FileOutputStream(f);
            fos.write(fixImageOrientation(filepath));
            fos.flush();
            fos.close()
            return f.absolutePath
        }
    }
}