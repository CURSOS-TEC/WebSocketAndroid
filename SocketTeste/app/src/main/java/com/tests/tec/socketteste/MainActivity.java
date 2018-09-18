package com.tests.tec.socketteste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import com.github.nkzawa.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private EditText mInputText;
    private EditText mInputServer;
    private EditText mInputPort;
    private Button mSummitButton;
    private Button mConnectButton;
    private SeekBar mTemperatureIndicator;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://achex.ca");
        } catch (Exception e) {

        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mInputText = findViewById(R.id.main_activity_inputext);
        this.mInputServer = findViewById(R.id.main_activity_input_server_address);
        this.mInputPort = findViewById(R.id.main_activity_input_server_port);
        this.mSummitButton = findViewById(R.id.main_activity_summit_button);
        this.mConnectButton = findViewById(R.id.main_activity_summit_button_connect);
        this.mTemperatureIndicator = findViewById(R.id.main_activity_temperature_indicator);

        mTemperatureIndicator.setClickable(false);
        mTemperatureIndicator.setMax(1000);

        this.mSummitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        this.mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconnect();
            }
        });

        mSocket.connect();
        mSocket.on("temperature", onNewMessage);

    }




    /**
     * This method takes the text inside the input text and send it to the server using the message
     * tag
     */
    public void sendMessage() {
        if(this.mSocket.connected()){
            String message = mInputText.getText().toString().trim();
            mInputText.setText("");
            mSocket.emit("message", message);
        }else{
           Toast.makeText(getApplicationContext(),"Connect first",Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * This method reacts when a message of type "message" is sent by the server
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                double temperature = data.getDouble("temperature");
                Log.i("Socket Value" ,String.valueOf(temperature));
                mTemperatureIndicator.setProgress((int)temperature);
            } catch (JSONException e) {
                Log.i("Socket Error" , e.getMessage());
            }
        }
    };

    /**
     *
     */
    public  void reconnect(){
        String server = this.mInputServer.getText().toString();
        String port = this.mInputPort.getText().toString();
        String url = String.format("http://%s:%s",server,port);
        Log.i("Socket Url" ,url);
        try {
            mSocket.disconnect();
            mSocket = IO.socket(url);
            if( mSocket.connect().connected()){
                Toast.makeText(getApplicationContext(),"Connect: "+url,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("Socket", e.getMessage());
        }
        mSocket.on("temperature", onNewMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }

}
