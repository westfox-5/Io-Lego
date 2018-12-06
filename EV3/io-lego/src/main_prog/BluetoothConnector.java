package main_prog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.hardware.Battery;
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

    public boolean sendColor(int x, int y, boolean correct){
		String s = String.format("%d%d%d", x,y,correct?1:0);
			
		try {
			outputStream.write(s.getBytes());
			outputStream.flush();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
    }

    public boolean sendBatteryInfo(){
    	try {
			outputStream.write( (int)(Battery.getVoltage()) );
			outputStream.flush();
			return true;
    	} catch(IOException e) {
			e.printStackTrace();
			return false;
    	}
    }

    public String[] parseInput() {
        	
		String str = "";
		char c;
		try {
			while ( ( c = (char) inputStream.readByte() ) != '\0' ) {
				str += c;
			}

			return str.split("&");

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

    public boolean sendCloseToken() {
    	return false;
    }
}