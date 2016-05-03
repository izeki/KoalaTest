package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cc.nctu1210.childcare.R;

/**
 * Created by User on 2016/4/30.
 */
public class GatewayListAdapterForMaster extends BaseAdapter {
    private final static String TAG = "GatewayListAdapterForMaster";
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<NewGatewayItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;

    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public  GatewayListAdapterForMaster(final Context context, final List<NewGatewayItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<NewGatewayItem> getData() {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        // this is where we'll be creating our view, anything that needs to update according to
        // your model object will need a view to visualize the state of that propery
        View view = convertView;


        // the viewholder pattern for performance
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder= new ViewHolder();
            // inflate the layout, see how we can use this context reference?
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.list_gateway_item, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.gatewayPlace = (TextView) view.findViewById(R.id.gateway_place);

            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }

        // object item based on the position
        final NewGatewayItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            String title = mContext.getString(R.string.gateway) + String.valueOf(position+1);
            obj.setTitle(title);
            viewHolder.gatewayPlace.setText(obj.getPlace());
        }
        return view;
    }

    private static class ViewHolder {
        public TextView gatewayPlace;
    }
}
