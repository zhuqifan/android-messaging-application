package com.example.mbassjsp.task4;

// Created by A Leeming
// Modified JSP
// Date 17-1-2018
// See https://developer.android.com ,for android classes, methods, etc
// Code snippets from http://examples.javacodegeeks.com/android/core/socket-core/android-socket-example

// import classes
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkConnectionAndReceiver extends Thread{
    //Declare class variables
    private Socket socket = null;
    private static final int SERVERPORT = 9999; // This is the port that we are connecting to
    // Channel simulator is 9998
    private static final String SERVERIP = "10.0.2.2";  // This is the host's loopback address
    private static final String LOGTAG = "Network and receiver"; // Identify logcat messages

    private boolean terminated = false; // When FALSE keep thread alive and polling

    private PrintWriter streamOut = null; // Transmitter stream
    private BufferedReader streamIn = null; // Receiver stream
    private AppCompatActivity parentRef; // Reference to main user interface(UI)
    private TextView receiverDisplay; // Receiver display
    private String message = null; //Received message

    //class constructor
    public NetworkConnectionAndReceiver(AppCompatActivity parentRef)
    {
        this.parentRef=parentRef; // Get reference to UI
    }
    // Start new thread method
    public void run()
    {
        Log.i(LOGTAG,"Running in new thread");

        //Create socket and input output streams
        try {   //Create socket
            InetAddress svrAddr = InetAddress.getByName(SERVERIP);
            socket = new Socket(svrAddr, SERVERPORT);

            //Setup i/o streams
            streamOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException uhe) {
            Log.e(LOGTAG, "Unknownhost\n" + uhe.getStackTrace().toString());
        }
        catch (Exception e) {
            Log.e(LOGTAG, "Socket failed\n" + e.getMessage());
            e.printStackTrace();
        }

        //receiver
        while(!terminated) // Keep thread running
        {
            try {
                message = streamIn.readLine(); // Read a line of text from the input stream
                // If the message has text then display it
                if (message != null && message != "") {
                    Log.i(LOGTAG, "MSG recv : " + message);
                    //Get the receiving text area as defined in the Res dir xml code
                    receiverDisplay = parentRef.findViewById(R.id.txtServerResponse);
                    //Run code in run() method on UI thread
                    parentRef.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display message, and old text in the receiving text area
                            receiverDisplay.setText(message + "\n" + receiverDisplay.getText());
                        }
                    });
                }
            }
            catch (Exception e) {
                Log.e(LOGTAG, "Receiver failed\n" + e.getMessage());
                e.printStackTrace();
            }

        }
        //Call disconnect method to close i/o streams and socket
        disconnect();
        Log.i(LOGTAG,"Thread now closing");
    }


    //  Method for closing socket and i/o streams
    public void disconnect()
    {
        Log.i(LOGTAG, "Closing socket and io streams");
        try {
            streamIn.close();
            streamOut.close();
        }
        catch(Exception e)
        {/*do nothing*/}

        try {
            socket.close();
        }
        catch(Exception e)
        {/*do nothing*/}
    }
    // Getter method for returning the output stream for the transmitter to use
    public PrintWriter getStreamOut() {return this.streamOut;}
    // Setter method for terminating this thread
    // Set value to true to close thread
    public void closeThread(boolean value) {this.terminated = value;}


}
