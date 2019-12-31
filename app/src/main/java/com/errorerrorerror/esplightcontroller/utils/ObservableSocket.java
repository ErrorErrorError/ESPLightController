package com.errorerrorerror.esplightcontroller.utils;

import android.util.Log;

import androidx.annotation.IntDef;

import com.errorerrorerror.esplightcontroller.data.device.Device;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ObservableSocket {
    public static final int CONNECTION_RESET = -1;
    public static final int CONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int DISCONNECTED = 3;
    public static final int COULD_NOT_CONNECT = 4;
    public static final int ERROR_OCCURRED = 5;
    public static final int CONNECTION_TIMEOUT = 6;
    public static final int ERROR_CLOSING_SOCKET = 7;
    public static final int ERROR_SENDING_DATA = 8;
    @ConnectionState
    private int connectionState = CONNECTING;

    private static final String TAG = ObservableSocket.class.getSimpleName();

    private static final int heartbeatInterval = 5000;
    private static final int initialDelay = 0;
    private static final int timeout = 3000;

    private Socket socket;
    private Device device;

    private PublishSubject<Integer> observableConnection = PublishSubject.create();
    private PublishSubject<Device> observableDevice = PublishSubject.create();
    private Disposable socketDisposable;
    private Disposable observableConnectionDisposable;

    private boolean wasConnectedBefore;

    public ObservableSocket(Device device) {
        this.device = device;
    }

    public void startObservingConnection() {
        if (socketDisposable != null) {
            stop();
        }

        socketDisposable = Observable.interval(initialDelay, heartbeatInterval, TimeUnit.MILLISECONDS, Schedulers.io())
                .map(tick -> deviceConnection())
                .subscribeOn(Schedulers.io())
                .subscribe(connection -> {

                    dispatchConnectivityObservable();
                    Log.d(TAG, "startObservingConnection: " + device.getName() + " on: " + device.isOn() + " connection: " + connection);
                });
    }

    @ConnectionState
    private int deviceConnection() {
        if (connectionState == CONNECTING) {
            dispatchConnectivityObservable();
        }

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(device.getIp(), device.getPort()), timeout);
            connectionState = CONNECTED;
            connectionState = sendData(socket, device, connectionState);
        } catch (SocketTimeoutException e) {
            connectionState = CONNECTION_TIMEOUT;
        } catch (IOException e) {
            connectionState = COULD_NOT_CONNECT;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                connectionState = ERROR_CLOSING_SOCKET;
            }
        }

        return connectionState;
    }

    public long getId() {
        return device.getId();
    }

    private static int sendData(final Socket socket, final Device device, @ConnectionState int connectionState) {
        byte[] data = getDeviceAsBinary(device);

        try {
            if (socket.isClosed() || !socket.isConnected()) {
                return ObservableSocket.ERROR_SENDING_DATA;
            }
            socket.getOutputStream().write(data, 0, data.length);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            connectionState = ERROR_SENDING_DATA;
            Log.e(TAG, "sendData: ", e);
        }

        return connectionState;
    }

    public void updateDevice(Device device) {
        if (!this.device.equals(device)) {
            this.device = device;
            startObservingConnection();
        }
    }

    private void stop() {
        socketDisposable.dispose();
        socketDisposable = null;
    }

    public void removeSelf() {
        stop();
        device = null;
        if (observableConnectionDisposable != null) {
            observableConnectionDisposable.dispose();
            observableConnectionDisposable = null;
        }
    }

    @ConnectionState
    public int getConnectionState() {
        return connectionState;
    }

    private void dispatchConnectivityObservable() {
        observableConnection.onNext(connectionState);
        observableDevice.onNext(device);
    }

    public Observable<ObservableDevice> getObservableConnection() {
        return Observable.zip(observableDevice, observableConnection, ObservableDevice::new).doOnSubscribe(disposable -> observableConnectionDisposable = disposable);
    }

    private static byte[] getDeviceAsBinary(Device device) {
        byte[] deviceData = {(byte) (device.isOn() ? 1 : 0), (byte) map(device.getBrightness(), 0 ,100, 0, 255)};
        byte[] modeData = device.getMode().toDataByte();

        int dLength = deviceData.length;
        int mLength = modeData.length;
        byte[] data = new byte[dLength + mLength];
        System.arraycopy(deviceData, 0, data, 0, dLength);
        System.arraycopy(modeData, 0, data, dLength, mLength);

        return data;
    }

    /// From Arduino IDE https://www.arduino.cc/reference/en/language/functions/math/map/
    private static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    @IntDef({CONNECTION_RESET, CONNECTED, CONNECTING, DISCONNECTED, COULD_NOT_CONNECT, ERROR_OCCURRED, CONNECTION_TIMEOUT, ERROR_CLOSING_SOCKET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionState {
    }

    public class ObservableDevice {
        private final Device device;
        private final int connection;

        ObservableDevice(Device device, int connection) {
            this.device = device;
            this.connection = connection;
        }

        public Device getDevice() {
            return device;
        }

        @ConnectionState
        public int getConnection() {
            return connection;
        }
    }

    public static String connectionErrorAsString(@ConnectionState int connection, boolean simplified) {
        String connect = "";
        switch (connection) {
            case ObservableSocket.CONNECTED:
                connect = "Connected";
                break;
            case ObservableSocket.CONNECTING:
                connect = "Connecting";
                break;
            case ObservableSocket.CONNECTION_RESET:
                connect = "Connection Reset";
                break;
            case ObservableSocket.COULD_NOT_CONNECT:
                connect = "Could Not Connect";
                break;
            case ObservableSocket.CONNECTION_TIMEOUT:
                connect = simplified ? "Not Connected" : "Connection Timeout";
                break;
            case ObservableSocket.DISCONNECTED:
                connect = "Disconnected";
                break;
            case ObservableSocket.ERROR_CLOSING_SOCKET:
                connect = "Error Closing Socket";
                break;
            case ObservableSocket.ERROR_OCCURRED:
                connect = "Error Occurred";
                break;
        }

        return connect;
    }
}