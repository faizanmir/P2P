package avishkaar.com.p2p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView deviceRecyclerView;
    ArrayList<WifiDevice> deviceArrayList;
    Button send,scan,connect;
    TextView status;
    EditText messageSend;
    RecyclerView.LayoutManager layoutManager;
    DeviceAdapter adapter;
    WifiP2pManager manager;
    BroadcastReceiver receiver;
    private List<WifiP2pDevice> peersArraylist = new ArrayList<WifiP2pDevice>();
    WifiP2pManager.PeerListListener peerListListener;
    private static final String TAG = "MainActivity";
    WifiP2pDevice wifiP2pDevice;
    WifiP2pConfig config;
    String host;

    WifiP2pManager.Channel channel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        deviceRecyclerView.setLayoutManager(layoutManager);
        deviceRecyclerView.setAdapter(adapter);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess: " +  "Connection Successful" );
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "onFailure: " + "Connection Failed with code : "+reason );
                    }
                });}catch (NullPointerException e)
               {
                   e.printStackTrace();
                   Toast.makeText(MainActivity.this,"Please select a deivice from the list to connect to ",Toast.LENGTH_SHORT).show();
               }
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess: " +"Discovery Successful" );
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "onFailure: " +" Discovery Failed with Error Code : "+ reason );
                    }
                });
            }
        });







        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Collection<WifiP2pDevice> refreshedPeers =  peers.getDeviceList();
                if (!refreshedPeers.equals(peers)) {
                    peersArraylist.clear();
                    peersArraylist.addAll(refreshedPeers);
                }
                for (WifiP2pDevice wifiP2pDevice:peersArraylist)
                {
                    deviceArrayList.add(new WifiDevice(wifiP2pDevice,wifiP2pDevice.deviceName,wifiP2pDevice.deviceAddress));
                    adapter.notifyDataSetChanged();
                }

            }

        };

        adapter.referenceListener(new DeviceAdapter.deviceCallback() {
            @Override
            public void deviceCallBack(WifiP2pDevice wifiP2pDevice) {
                MainActivity.this.wifiP2pDevice = wifiP2pDevice;
                config.deviceAddress = wifiP2pDevice.deviceAddress;
            }
        });






    }

    void requestPeers()
    {
        manager.requestPeers(channel,peerListListener);
    }


    void init()
    {   deviceArrayList = new ArrayList<>();
        send = findViewById(R.id.send);
        connect = findViewById(R.id.connect);
        scan = findViewById(R.id.scan);
        status = findViewById(R.id.status);
        deviceRecyclerView = findViewById(R.id.deviceRecyclerView);
        messageSend = findViewById(R.id.sendText);
        layoutManager= new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        adapter = new DeviceAdapter(deviceArrayList);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel  =  manager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.e(TAG, "onChannelDisconnected: " + "Disconnected Channel...");
            }
        });
        receiver = new WifiBroadCastReceiver(this,manager,channel);
        peersArraylist = new ArrayList<>();
        config= new WifiP2pConfig();
        config.wps.setup= WpsInfo.PBC;

    }



    IntentFilter makeWifiIntentFilter()
    {
        IntentFilter intentFilter = new IntentFilter();
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }


    void setConnect()
    {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                InetAddress groupOwnerAddress = info.groupOwnerAddress;
                host = groupOwnerAddress.getHostAddress();
                Log.e(TAG, "onConnectionInfoAvailable:Host Address " +  host );
//                Log.e(TAG, "onConnectionInfoAvailable: Client Address"  + info.groupOwnerAddress.getCanonicalHostName() );
                // After the group negotiation, we can determine the group owner
                // (server).
                if (info.groupFormed && info.isGroupOwner) {
                    status.setText("Is Host");

                    // Do whatever tasks are specific to the group owner.
                    // One common case is creating a group owner thread and accepting
                    // incoming connections.
                } else if (info.groupFormed) {
                    status.setText("is Client");
                    // The other device acts as the peer (client). In this case,
                    // you'll want to create a peer thread that connects
                    // to the group owner.
                }
            }
        });
    }



    void makeServer()
    {

    }


    void makeClient(String hostAddress)
    {

    }






    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,makeWifiIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}
