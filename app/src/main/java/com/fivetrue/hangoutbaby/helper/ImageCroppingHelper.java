package com.fivetrue.hangoutbaby.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by kwonojin on 16. 9. 20..
 */
public class ImageCroppingHelper {

    private static final String TAG = "ImageCroppingHelper";

    public interface OnCroppingListener{
        void onCropImage(Uri originalUri, Uri croppedUri, Bitmap image);
        void onFailed(Uri originalUri, Uri croppedUri, Exception e);
    }

    private static final int DEFAULT_CROPPED_IMAGE_WIDTH = 200;
    private static final int DEFAULT_CRRPPED_IMAGE_HEIGHT = 200;
    private static final int DEFAULT_ASPECT_X = 4;
    private static final int DEFAULT_ASPECT_Y = 3;

    private static final int REQUEST_IMAGE_PICK = 0x11;

    private Activity mActivity = null;

    private int mCroppedImageWidth = DEFAULT_CROPPED_IMAGE_WIDTH;
    private int mCroppedImageHeight = DEFAULT_CRRPPED_IMAGE_HEIGHT;
    private int mAspectX = DEFAULT_ASPECT_X;
    private int mAspectY = DEFAULT_ASPECT_Y;

    private Uri mOriginalImageUri = null;
    private Uri mCroppedImageUri = null;

    private OnCroppingListener mOnCroppingListener = null;

    public ImageCroppingHelper(Activity activity){
        mActivity = activity;
    }

    public void startChoosingAndCrop(int width, int height, int aspectX, int aspectY, OnCroppingListener ll){
        mCroppedImageWidth = width;
        mCroppedImageHeight = height;
        mAspectX = aspectX;
        mAspectY = aspectY;
        mOnCroppingListener = ll;
        chooseImage();
    }

    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    public void cropImage(Uri uri){
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(mAspectX, mAspectY)
                .setRequestedSize(mCroppedImageWidth, mCroppedImageHeight)
                .start(mActivity);
    }

    private void onCroppedImage(Intent intent){
        Log.d(TAG, "onCroppedImage() called with: " + "intent = [" + intent + "]");
        if(intent != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            mCroppedImageUri = result.getUri();
            try {
                Bitmap bm = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(mCroppedImageUri));
                mOnCroppingListener.onCropImage(mOriginalImageUri, mCroppedImageUri, bm);
            }catch (Exception e){
                e.printStackTrace();
                mOnCroppingListener.onFailed(mOriginalImageUri, mCroppedImageUri, e);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if(requestCode == REQUEST_IMAGE_PICK){
            if(resultCode == Activity.RESULT_OK){
                mOriginalImageUri = data.getData();
                cropImage(mOriginalImageUri);
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                onCroppedImage(data);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                mOnCroppingListener.onFailed(mOriginalImageUri, mCroppedImageUri, error);
                Log.d(TAG, "onActivityResult() crop error : " + error);
            }
        }
    }

}
