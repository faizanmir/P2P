package avishkaar.com.p2p;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiDevice {
    public WifiP2pDevice device;
    public String deviceName;
    public String deviceAddress;

    public WifiDevice(WifiP2pDevice device, String deviceName, String deviceAddress) {
        this.device = device;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
