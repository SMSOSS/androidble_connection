package com.example.ble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.IntentFilter;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String deviceaddress=("2C:AB:33:54:98:A5");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private byte[] sendValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the BluetoothAdapter
        final BluetoothManager mbluetoothManager=(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter=mbluetoothManager.getAdapter();
        connect();
    }

    public void connect(){
        //connect to given device addr
        BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(deviceaddress);
        mBluetoothGatt=device.connectGatt(this, false, mGattCallback);
    }

    //get callbacks when something changes
    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (BluetoothGatt.GATT_SUCCESS == status) {
                Log.e("GATT", "Connected");
                gatt.discoverServices();
            } else {
                Log.e("GATT", "Status :" + status);
                gatt.close();
                connect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("GATT", "msg :" + status);
            sendmsg(gatt , "10");
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                Log.d("GATT", "enter success");
            }
        }

        //func to send msg to lego spike
        public void sendmsg(BluetoothGatt gatt, String msg){
            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(RX_SERVICE_UUID).getCharacteristic(RX_CHAR_UUID);
            byte[] byteArrray = msg.getBytes();
            characteristic.setValue(byteArrray);
            boolean stat = gatt.writeCharacteristic(characteristic);
            Log.e("GATT", "Send :" + stat);
        }



    };
}