package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.VolleyRequestManager;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class ChildProfleEditActivity extends Activity implements View.OnClickListener {
    private static final String TAG = ChildProfleEditActivity.class.getSimpleName();
    private EditText mEditTextName;
    private ImageView mImageViewPhoto;
    private TextView mTextViewDeviceAddress;
    private Button mButtonOk, mButtonRemove;
    private String name;
    private String deviceAddress;
    private int viewPosition;
    private String photoName;
    //private File photoFile;
    private File tmpFile = ApplicationContext.createImageFile(ApplicationContext.CHILD_PHOTO_FILE_PATH, "tmp.jpg");
    Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.child_profie_edit);
        init();
    }

    void init() {
        mEditTextName = (EditText) findViewById(R.id.editText_edit_name);
        mImageViewPhoto = (ImageView) findViewById(R.id.image_edit_user);
        mTextViewDeviceAddress = (TextView) findViewById(R.id.text_edit_mac);
        mButtonOk = (Button) findViewById(R.id.button_edit_ok);
        mButtonRemove = (Button) findViewById(R.id.button_edit_remove);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(ApplicationContext.CHILD_NAME);
        deviceAddress = bundle.getString(ApplicationContext.DEVICE_ADDRESS);
        viewPosition = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
        photoName = bundle.getString(ApplicationContext.PHOTO_NAME);
        mTextViewDeviceAddress.setText(deviceAddress);
        mEditTextName.setText(name);

        mButtonOk.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);

        //photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
        //photo = ApplicationContext.getBitmapByFile(photoFile);
        photo = ApplicationContext.getBitmapFromMemCache(photoName);
        if (photo == null) {
            //mImageViewPhoto.setBackground(ApplicationContext.controlBitMap(this, R.drawable.default_user));
            String photoURL = ApplicationContext.CHILD_PHOTO_FILE_URL + photoName;
            //ImageLoader mImageLoader = VolleyRequestManager.getInstance(getApplicationContext()).getImageLoader();
            //mImageLoader.get(photoURL, ImageLoader.getImageListener(mImageViewPhoto, R.drawable.default_user, R.drawable.default_user));
            ImageRequest request = new ImageRequest(photoURL,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            mImageViewPhoto.setImageBitmap(response);
                            ApplicationContext.addBitmapToMemoryCache(photoName, response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mImageViewPhoto.setImageResource(R.drawable.default_user);
                        }
                    });
            VolleyRequestManager.getInstance(getBaseContext()).addToRequestQueue(request);
        } else {
            mImageViewPhoto.setImageBitmap(photo);
        }
        mImageViewPhoto.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(Uri.fromFile(tmpFile));
                }
                break;
            case ApplicationContext.REQUEST_CODE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                }
                break;
            case ApplicationContext.REQUEST_CODE_CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        setHeadPicture(data);
                    }
                } else if (resultCode == ApplicationContext.RESULT_CODE_CROP_ERROR) {
                    Log.e(TAG, "Crop error!!");
                }
                break;
        }

    }

    private void startPhotoZoom(Uri uri) {
        /*
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_CROP_PHOTO);
        */
        Uri dest = Uri.fromFile(new File(getCacheDir(),"cropped"));
        Crop.of(uri, dest).asSquare().start(this);
    }

    private void setHeadPicture(Intent picData) {
        try {
            /*
            Bundle extras = picData.getExtras();
            if (extras != null) {
                photo = extras.getParcelable("data");
                mImageViewPhoto.setImageBitmap(photo);
                ApplicationContext.saveBitmap(photoFile, photo);
                ApplicationContext.uploadPhoto(photoName, photo);
            }*/
            Uri photoUri = Crop.getOutput(picData);
            Bitmap tmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            int rotation = picData.getIntExtra(ApplicationContext.EXTRA_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            photo = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, true);
            mImageViewPhoto.setImageBitmap(photo);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit_user:
                String[] items = { getString(R.string.take_photo), getString(R.string.choose_photo) };
                new AlertDialog.Builder(this).setTitle(getString(R.string.photo))
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                                                Uri.fromFile(tmpFile));
                                        startActivityForResult(intentTakePhoto, ApplicationContext.REQUEST_CODE_TAKE_PHOTO);
                                        break;
                                    case 1:
                                        Intent intentChoosePhoto = new Intent("android.intent.action.PICK", null);
                                        intentChoosePhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(intentChoosePhoto, ApplicationContext.REQUEST_CODE_CHOOSE_PHOTO);
                                    default:
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.button_edit_ok:
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                final String name = mEditTextName.getText().toString();
                int position = viewPosition;
                bundle.putString(ApplicationContext.CHILD_NAME, name);
                bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                bundle.putString(ApplicationContext.PHOTO_NAME, photoName);
                intent.putExtras(bundle);
                if (photo != null) {
                    //final File photoFile = ApplicationContext.createImageFile(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
                    //ApplicationContext.saveBitmap(photoFile, photo);
                    ApplicationContext.addBitmapToMemoryCache(photoName, photo);
                    ApplicationContext.uploadPhoto(photoName, photo);
                    ApplicationContext.imageFileDelete(tmpFile);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.button_edit_remove:
                intent = getIntent();
                position = viewPosition;
                intent.putExtra(ApplicationContext.LIST_VIEW_POSITION, position);
                setResult(ApplicationContext.RESULT_CODE_REMOVE, intent);
                final String addr = deviceAddress;
                ApplicationContext.removeAChild(addr);
                finish();
                break;
        }
    }
}
