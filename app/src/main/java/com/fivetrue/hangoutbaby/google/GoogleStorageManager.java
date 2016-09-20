package com.fivetrue.hangoutbaby.google;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.fivetrue.hangoutbaby.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Created by kwonojin on 16. 9. 20..
 */
public class GoogleStorageManager {

    private static final String TAG = "GoogleStorageManager";

    public interface OnUploadResultListener{
        void onUploadSuccess(Uri url);
        void onUploadFailed(Exception e);
    }

    private static final String IMAGE_ROOT = "images/";
    private static final String IMAGE_PROFILE = IMAGE_ROOT + "profile/";

    private Context mContext = null;

    private static GoogleStorageManager sInstance = null;

    private FirebaseStorage mFirebaseStorage = null;
    private StorageReference mStorageRef = null;

    public static GoogleStorageManager getInstnace(Context context){
        if(sInstance == null){
            sInstance = new GoogleStorageManager(context);
        }
        return sInstance;
    }

    private GoogleStorageManager(Context context){
        mContext = context;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
    }

    public void uploadProfileImage(String childName, Bitmap bitmap, final OnUploadResultListener ll){
        if(childName != null && bitmap != null && !bitmap.isRecycled() && ll != null){
            if(bitmap.getHeight() <= 200 && bitmap.getWidth() <= 200){
                StorageReference profileImageRef = mStorageRef.child(IMAGE_PROFILE + childName);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = profileImageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        ll.onUploadFailed(exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        ll.onUploadSuccess(taskSnapshot.getDownloadUrl());
                    }
                });
            }else{
                ll.onUploadFailed(new Exception("Bitmap size over 200px"));
            }
        }
    }
}
