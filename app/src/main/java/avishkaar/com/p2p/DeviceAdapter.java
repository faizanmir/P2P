package avishkaar.com.p2p;

import android.bluetooth.BluetoothClass;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.deviceViewHolder> {
    ArrayList<WifiDevice>deviceArrayList;
    WifiP2pDevice wifiP2pDevice;

    public DeviceAdapter(ArrayList<WifiDevice> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }



    interface deviceCallback{
        void deviceCallBack(WifiP2pDevice wifiP2pDevice);
    }
    deviceCallback ref;
    void referenceListener(deviceCallback ref)
    {
        this.ref = ref;
    }

    @NonNull
    @Override
    public deviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new deviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull deviceViewHolder holder, final int position) {
            holder.deviceName.setText(deviceArrayList.get(position).getDeviceName());
            holder.deviceAddress.setText(deviceArrayList.get(position).getDeviceAddress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifiP2pDevice = deviceArrayList.get(position).getDevice();
                    ref.deviceCallBack(wifiP2pDevice);
                }
            });
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    class deviceViewHolder extends RecyclerView.ViewHolder{
        TextView deviceName;
        TextView deviceAddress;
        public deviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceAddress = itemView.findViewById(R.id.deviceAddress);
        }
    }
}
