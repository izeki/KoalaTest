package cc.nctu1210.api.koala6x;

import android.bluetooth.BluetoothDevice;

public class SensorEvent {
    public static final int TYPE_ACCELEROMETER = 1 << 0;
    public static final int TYPE_GYROSCOPE = 1 << 1;
    public static final int TYPE_MAGNETOMETER = 1 << 2;

    public final int type;
    public final BluetoothDevice device;
    public int seq;
    public final float values[];

    protected SensorEvent(final int type, BluetoothDevice device, int valueSize, int seq) {
        this.type = type;
        this.device = device;
        this.values = new float[valueSize];
        this.seq = seq;
    }

    protected void setValues(float values[]) {
        for (int i=0, size=values.length; i<size; i++) {
            this.values[i] = values[i];
        }
    }
}