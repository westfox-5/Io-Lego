package main_prog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import lejos.hardware.lcd.LCD;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;

class BluetoothConnector {
    protected DataInputStream inputStream;
    private DataOutputStream outputStream;
    private BTConnection connection;

    BluetoothConnector() {
        BTConnector set=new BTConnector();
        this.connection=set.waitForConnection(10,NXTConnection.RAW);
        
        inputStream = connection.openDataInputStream();
        outputStream = connection.openDataOutputStream();
    }
    
    public void send(String str) throws IOException {
    	outputStream.write(str.getBytes(Charset.forName("UTF-8")));
		outputStream.flush();
    }
    
    public String read() throws IOException {
        InputStreamReader dataInputStream = new InputStreamReader(inputStream);
        int bytesRead;
        char[] buffer = new char[256];

        bytesRead = dataInputStream.read(buffer, 0, 4);
    	return new String(buffer, 0, bytesRead);
    }
}