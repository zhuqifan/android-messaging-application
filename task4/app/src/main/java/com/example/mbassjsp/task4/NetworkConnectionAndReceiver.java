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
    private String[] collection = new String[30];
    private String key = null;
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
                    /*String textString = receiverDisplay.getText().toString();
                    int[] textBinary = new int[textString.length()];
                    for (int i = 0; i < textString.length(); i++) {
                        textBinary[i] = Character.digit(textString.charAt(i), 10);
                    }
                    int textDeci = bin2deci( textBinary, textString.length());*/
                    //Run code in run() method on UI thread
                    parentRef.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(messageType(message) == 1){
                                collect( message);
                                if(isRepeated( message)){
                                    key = getKey( message);
                                    receiverDisplay.setText(decrypt(message, key) + "\n" + receiverDisplay.getText());
                                }
                                else if (key != null){
                                    receiverDisplay.setText(decrypt(message, key) + "\n" + receiverDisplay.getText());
                                }
                                else{
                                    receiverDisplay.setText("Not enough message to decrypt, please try again" + "\n" + receiverDisplay.getText());
                                }
                            }
                            // Display message, and old text in the receiving text area
                            else if(messageType(message) == 2){
                                receiverDisplay.setText(binaryToText(message) + "\n" + receiverDisplay.getText());
                            }
                            else{
                                receiverDisplay.setText(message + "\n" + receiverDisplay.getText());
                            }
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
    public String getKey( String message){
        //XOR message and signature
        String signature = StringtoBinary("Sent by BarryBot, School of Computer Science, The University of Manchester").toString();
        String key = "";
        StringBuilder sb = new StringBuilder();
        Log.d(LOGTAG,"signature");
        Log.d(LOGTAG,signature);
        Log.d(LOGTAG,"CHECK!!!");
        Log.d(LOGTAG,encryptToText(signature));
        Log.d(LOGTAG,"message");
        Log.d(LOGTAG,message);
        for(int i = 0; i < message.length(); i++)
            sb.append((signature.charAt(i) ^ message.charAt(i)));
        Log.d(LOGTAG,"getKey");
        Log.d(LOGTAG,sb.toString());
        return sb.toString();
    }

    public String decrypt( String message, String key){
        StringBuilder decryptMSG = new StringBuilder();
        for(int i = 0; i < message.length(); i++)
            decryptMSG.append((message.charAt(i) ^ key.charAt(i)));

        Log.d(LOGTAG,"Key is");
        Log.d(LOGTAG,key);
        Log.d(LOGTAG,"Message now is");
        Log.d(LOGTAG,message);
        Log.d(LOGTAG,"Answer is");
        Log.d(LOGTAG,decryptMSG.toString());
        Log.d(LOGTAG,encryptToText(decryptMSG.toString()));
        return encryptToText(decryptMSG.toString());
    }
    public StringBuilder StringtoBinary(String s){

        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            //binary.append(' ');
        }
        Log.d(LOGTAG,s);
        return binary;
    }

    public boolean isRepeated( String message){
        int index = 0;
        for (int i = 0; i < collection.length; i++){
            if (collection[i] != null)
                index++;
        }
        for(int i = 0; i < index - 1 ; i++){
            if(collection[i].equals(message)) {
                Log.d(LOGTAG,"isRepeated");
                return true;
            }
        }
        Log.d(LOGTAG,"isNOTRepeated");
        return false;
    }

    public void collect( String message){
        for(int i = 0; i < collection.length; i++){
            if(collection[i] == null) {
                collection[i] = message;
                break;
                }
            }
        Log.d(LOGTAG,"collect !!");
    }

    public String binaryToText(String s){
        String[] string = s.split(" ");
        String str = "";
        for(int i = 3; i < string.length; i++){
            int charCode = Integer.parseInt(string[i], 2);
            str = str + new Character((char)charCode).toString();
        }
        return str;
    }

    public String encryptToText(String s){
        String string = "";
        for(int i = 0; i < s.length() ; i+=8){
            String str = s.substring(i, i+8);
            int charCode = Integer.parseInt(str, 2);
            string = string + new Character((char)charCode).toString();
        }

        return string;
    }

    public int messageType(String s){
        String[] Parts = s.split(" ");
        if(Parts.length == 1)
            return 1;
        else if(Parts[1].equals("(via")){

            return 2;
        }
        else
            return 3;
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