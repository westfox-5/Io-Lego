package main_prog;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;
import main_prog.main_p.Colors;


class BluetoothConnector {
    protected DataInputStream inputStream;
    private BTConnection connection;

    BluetoothConnector() {
        BTConnector set=new BTConnector();
        this.connection=set.waitForConnection(10,NXTConnection.RAW);
        
        InputStream in = connection.openInputStream();
        inputStream = new DataInputStream(in);
    }

    public boolean sendColor(Colors c){

    }

    public boolean sendBatteryInfo(){
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

    }
}