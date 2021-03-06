package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import cc.nctu1210.childcare.R;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.VolleyRequestManager;



/**
 * A custom adapter for our listview
 * <p/>
 * If you check http://developer.android.com/reference/android/widget/Adapter.html you'll notice
 * there are several types. BaseAdapter is a good generic adapter that should suit all your needs.
 * Just implement all what's abstract and add your collection of data
 * <p/>
 * Created by hanscappelle on 7/10/14.
 * https://github.com/hanscappelle/so-2250770
 */
public class ChildrenListAdapter extends BaseAdapter {
    private final static String TAG = ChildrenListAdapter.class.getSimpleName();
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<ChildItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;
    private int isMaster=0;
    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public ChildrenListAdapter(final Context context, final List<ChildItem> mData, int isMaster) {
        this.mData = mData;
        this.mContext = context;
        this.isMaster = isMaster;
    }

    public List<ChildItem> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mData != null ? mData.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        // just returning position as id here, could be the id of your model object instead
        return i;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // this is where we'll be creating our view, anything that needs to update according to
            // your model object will need a view to visualize the state of that propery
            View view = convertView;


            // the viewholder pattern for performance
            ViewHolder viewHolder = new ViewHolder();
            if (view == null) {

                // inflate the layout, see how we can use this context reference?
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                view = inflater.inflate(R.layout.list_child_item, parent, false);
                Log.d(TAG, String.format("Get view %d", position));
                // we'll set up the ViewHolder
                viewHolder.photo = (ImageView) view.findViewById(R.id.image_child_user);
                viewHolder.name     = (TextView) view.findViewById(R.id.text_child_user);
                viewHolder.status      = (ImageView) view.findViewById(R.id.image_child_status);
                viewHolder.position_status      = (TextView) view.findViewById(R.id.text_child_status);
                // store the holder with the view.
                view.setTag(viewHolder);

            } else {
                // we've just avoided calling findViewById() on resource every time
                // just use the viewHolder instead
                viewHolder = (ViewHolder) view.getTag();
            }

            // object item based on the position
            ChildItem obj = mData.get(position);

            // assign values if the object is not null
            if (mData != null) {
                // get the TextView from the ViewHolder and then set the text (item name) and other values
                final String photoName = obj.photoName;
                //Bitmap photo = ApplicationContext.getBitmapByFileName(photoName);
                //Log.i(TAG, "position: "+position+" photoName:"+photoName);
                Bitmap photo = ApplicationContext.getBitmapFromMemCache(photoName);

                if (photo == null) {
                    Log.i(TAG, "get image  from volley!");
                    //viewHolder.photo.setBackground(ApplicationContext.controlBitMap(mContext, R.drawable.default_user));
                    String photoURL = ApplicationContext.CHILD_PHOTO_FILE_URL + photoName;
                    final ImageView photoImage = viewHolder.photo;
                    //ImageLoader mImageLoader = VolleyRequestManager.getInstance(mContext).getImageLoader();
                    //mImageLoader.get(photoURL, ImageLoader.getImageListener(viewHolder.photo, R.drawable.default_user, R.drawable.default_user));
                    ImageRequest request = new ImageRequest(photoURL,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    //File photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);\
                                    Bitmap out = null;
                                    //Log.i(TAG, "Before compressed: " + photoName + ":size: " + response.getByteCount() + "bytes");
                                    out = ApplicationContext.scaleBitmap(response, 100, 100);
                                    //Log.i(TAG, "After compressed: "+photoName+":size: "+out.getByteCount()+"bytes");
                                    photoImage.setImageBitmap(out);
                                    //ApplicationContext.saveBitmap(photoFile, response);
                                    ApplicationContext.addBitmapToMemoryCache(photoName, out);
                                }
                            }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.w(TAG, "image access fail!!!");
                                    photoImage.setImageResource(R.drawable.default_user);
                                }
                            });
                    VolleyRequestManager.getInstance(mContext).addToRequestQueue(request);
                } else {
                    Log.i(TAG, "image set from cache!");
                    viewHolder.photo.setImageBitmap(photo);
                }

                viewHolder.name.setText(obj.name);

                if(obj.flag.equals("1"))
                    viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_miss, null));
                else {
                    if (obj.status.equals("near")) {
                        viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_green, null));
                    } else if (obj.status.equals("medium")) {
                        viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_green, null));
                    } else if (obj.status.equals("far")) {
                        viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_green, null));
                    }
                }
                if(isMaster == 1)

                {
                    viewHolder.position_status.setText(obj.rssi);
                }
                    else
                    viewHolder.position_status.setText(obj.place);
            }
            return view;
    }

    private static class ViewHolder {
        public ImageView photo;
        public TextView name;
        public ImageView status;
        public TextView position_status;
    }
}