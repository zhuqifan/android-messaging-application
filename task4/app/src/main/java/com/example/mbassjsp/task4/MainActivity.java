package com.example.mbassjsp.task4;

// Created by A Leeming
// Modified JSP
// Date 17-1-2018
// See https://developer.android.com ,for android classes, methods, etc


// Import classes
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        // Default android imports
        import android.app.Activity; import android.app.ActionBar;
        import android.app.Fragment; import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater; import android.view.Menu; import android.view.MenuItem;
        import android.view.View; import android.view.ViewGroup;
        import android.os.Build;
//Added imports for this app
        import android.widget.Button; import android.widget.ImageView;
        import java.io.File; import java.io.FileNotFoundException;
        import java.io.FileOutputStream; import java.io.IOException;
        import java.io.InputStream; import java.io.OutputStream;
        import android.graphics.Bitmap; import android.graphics.BitmapFactory;
        import android.app.Activity; import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;
        import android.view.Menu; import android.view.MenuItem; import android.view.View;
        import android.widget.Button; import android.widget.EditText;
        import android.widget.TextView;
        // Added for playing sound
        import android.media.AudioManager; import android.media.MediaPlayer;
        import android.media.AudioTrack; import android.media.AudioFormat;
        import java.io.File;
        import java.io.InputStream; import java.io.BufferedInputStream;
        import java.io.DataInputStream; import java.io.*;
        import android.content.res.* ;

        import java.util.Arrays;
        import java.util.Random;

// Android apps must have a MainActivity class that extends Activity or AppCompatActivity class
public class MainActivity extends AppCompatActivity {
    //Declare class variables
    private static final String LOGTAG = "Main UI"; //Logcat messages from UI are identified
    private NetworkConnectionAndReceiver networkConnectionAndReceiver = null;
    private EditText transmitterText; //Transmitter data variable
    private MediaPlayer mySound ;
    // Class methods
    public void playSound(View view) {
        //mySound.start();
        msSpeech();
        SmoothBR();
        repalceLostPacket();
        arrayplay();
    }
    public void stopSound(View view) {
        mySound.release();
    }


    private int Fs = 8000; // Samping rate (Hz)
    private int length = Fs*10; // length of array for 10 seconds
    private short[ ] data = new short[length]; // Array of 16-bit samples
    private int i;
    // Method for filling data array with random noise:
    void fillRandom()
    { Random rand = new Random();
        for ( i = 0 ; i < length ; i++ ) { data[i] = (short) rand.nextInt();} //Fill data array
    } // end of method fillRandom
     // Method for playing sound from an array:

     void arrayplay()
     {
         int CONF = AudioFormat.CHANNEL_OUT_MONO;
         int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
         int MDE = AudioTrack.MODE_STATIC; //Need static mode.
         int STRMTYP = AudioManager.STREAM_ALARM;
         AudioTrack track = new AudioTrack(STRMTYP, Fs, CONF, FORMAT, length*2, MDE);
         //fillRandom(); // Fill data array with 16-bit samples of random noise
         track.write(data, 0, length); track.play();
         //Following statements needed if you want to go on to play something else
         while(track.getPlaybackHeadPosition() != length) {}; //Wait before playing more
         track.stop(); track.setPlaybackHeadPosition(0);
         while(track.getPlaybackHeadPosition() != 0) {}; // wait for head position
     } // end of arrayplay method


     void msSpeech()
     { // Method for reading 16-bit samples from a wav file into array data
        int i; int b1 = 0; int b2 = 0;
        try
        { // Make speechis an input stream object for accessing a wav file placed in R.raw
            InputStream speechis=getResources().openRawResource(R.raw.damagedspeech);
            // Read & discard the 44 byte header of the wav file:
            for ( i = 0 ; i < 44 ; i++ ) { b1 = speechis.read(); }
            // Read rest of 16-bit samples from wav file byte-by-byte:
            for ( i = 0 ; i < length ; i++ )
            { b1 = speechis.read(); // Get first byte of 16-bit sample in least sig 8 bits of b1
                if (b1 == -1) {b1 = 0;} // b1 becomes -1 if we try to read past End of File
                b2 = speechis.read(); // Get second byte of sample value in b2
                if (b2 == -1) {b2 = 0;} // trying to read past EOF
                b2 = b2<<8 ; // shift b2 left by 8 binary places
                data[i] = (short) (b1 | b2); //Concat 2 bytes to make 16-bit sample value
            } // end of for loop
            speechis.close();
        } catch (FileNotFoundException e) {Log.e("tag", "wav file not found", e);}
        catch (IOException e) { Log.e("tag", "Failed to close input stream", e);}
     } // end of msSpeech method

    void SmoothBR(){
        for( i = 0 ; i < length; i ++){
            if (data[i] >= 1500)
                data[i] = (short)((data[i-1] + data[i+1])/2);
        }
    }

    public static boolean check(short[] array, short number){
        for ( int i = 0; i < array.length; i++){
            if (array[i] != number)
                return false;
        }
        return true;
    }
    void repalceLostPacket(){
        short[] previous = new short[200];
        short[] current = new short[200];
        for (i = 0; i < length/200; i++){
            previous = Arrays.copyOfRange(data, i * 200, (i+1)*200);
            current = Arrays.copyOfRange(data, (i+1) * 200, (i+2)*200);
            if (check(current,(short)0)){
                current = previous;
            }
        }
    }

    void msImage(String filename)
    { // Reads from an image file into an array for image processing in Java.
        try { int res_id = getResources().getIdentifier(filename, "raw", getPackageName() );
            InputStream image_is = getResources().openRawResource(res_id);
            int filesize = image_is.available(); //Get image file size in bytes
            byte[ ] image_array = new byte[filesize]; //Create array to hold image
            image_is.read(image_array); //Load image into array
            image_is.close(); // Close in-out file stream
            // Add your code here to process image_array & save processed version to a file.
            File newImageFile = new File(getFilesDir(), filename);
            OutputStream image_os = new FileOutputStream(newImageFile);
            image_os.write(image_array, 0, filesize);
            image_os.flush();
            image_os.close();
            //Display the processed imaged file
            Bitmap newBitmap = BitmapFactory.decodeFile(newImageFile.getAbsolutePath());
            // Create ImageView object
            ImageView image = (ImageView) findViewById(R.id.imageView1);
            image.setImageBitmap(newBitmap);
        } // end of try
        catch (FileNotFoundException e) { Log.e("tag", "File not found ", e);}
        catch (IOException e) { Log.e("tag", "Failed for stream", e); }
    } //end of msImage method
    @Override
    //Extend the onCreate method, called whenever an activity is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Extend the onCreate method
        // Set up the view using xml description res>layout>activity_main.xml
        setContentView(R.layout.activity_main);
        mySound = MediaPlayer.create(this, R.raw.damagedspeech);
        setContentView(R.layout.activity_main);
        //Get image display area as set in the xml file//
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        //Display peppers.bmp from res/raw directory//
        image.setImageResource(R.drawable.peppers);
        //Call msImage method to process the image:
        //msImage("peppers");
        Log.i(LOGTAG, "Starting task4 app"); // Report to Logcat

        // Instantiate the network connection and receiver object
        networkConnectionAndReceiver = new NetworkConnectionAndReceiver(this);
        networkConnectionAndReceiver.start();    // Start socket-receiver thread


        // Get the receiving text area as defined in the Res dir xml code
        TextView receiverTextArea = findViewById(R.id.txtServerResponse);
        // Make the receiving text area scrollable
        receiverTextArea.setMovementMethod(new ScrollingMovementMethod());

        final Button exitButton = findViewById(R.id.btnExit);
        final Button cancelButton = findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the killButton object
            public void onClick(View v) {
                // OnClick actions here
                exitButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);

            }
        });
        Button killButton = findViewById(R.id.btnKill);
        // Make the kill button receptive to being clicked
        // Button click handler
        killButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the killButton object
            public void onClick(View v) {
                // OnClick actions here
                exitButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            }
        });

        // Get the kill button as defined in the Res dir xml code

        // Make the kill button receptive to being clicked
        // Button click handler
        exitButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the killButton object
            public void onClick(View v) {
                // OnClick actions here
                // Exit app
                System.exit(0);

            }
        });


        // Get the text area for commands to be transmitted as defined in the Res dir xml code
//        transmitterText = (EditText) findViewById(R.id.cmdInput);
//        // Get the send button as defined in the Res dir xml code
//        Button sendButton =  findViewById(R.id.btnSendCmd);
//        // Make the kill button receptive to being clicked
//        // Button click handler
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            // onClick method implementation for the sendButton object
//            public void onClick(View v) {
//                // OnClick actions here
//                // Instantiate the transmitter passing the output stream and text to it
//                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
//                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText.getText().toString());
//                    transmitter.start();        // Run on its own thread
//                    transmitterText.setText(""); // Clear transmitter text field
//                }
//            }
//        });

        //who
// Get the text area for commands to be transmitted as defined in the Res dir xml code
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button whoButton =  findViewById(R.id.btnWho);
        whoButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"WHO");
                transmitter.start();        // Run on its own thread
                Log.i(LOGTAG, "show who is online!"); // Report to Logcat
            }
        });

//REGISTER
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button RegisterButton =  findViewById(R.id.btnRegister);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"REGISTER "+transmitterText.getText().toString());
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "REGISTER SUCCESS!"); // Report to Logcat
            }
        });

//INVITE
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button InviteButton =  findViewById(R.id.btnInvite);
        InviteButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"INVITE "+transmitterText.getText().toString());
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "Invite!"); // Report to Logcat
            }
        });

        //Accpet
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button AcceptButton =  findViewById(R.id.btnAccept);
        AcceptButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"ACCEPT "+transmitterText.getText().toString());
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "Accept!"); // Report to Logcat
            }
        });
//MSG
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button MSGButton =  findViewById(R.id.btnMSG);
        MSGButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"0MSG "+transmitterText.getText().toString());
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "send to message"); // Report to Logcat
            }
        });

        //MSG
        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button CMDButton =  findViewById(R.id.btnSendCmd);
        CMDButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                String s = transmitterText.getText().toString();
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),GetType(s) + " " + GetName(s) + " " + StringtoBinary(s));
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "send"); // Report to Logcat
            }
        });

        transmitterText = (EditText) findViewById(R.id.cmdInput);
// Get the send button as defined in the Res dir xml code
        Button encryptButton =  findViewById(R.id.btnENCRYPT);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                String s = transmitterText.getText().toString();
                Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(),"0MSG BarryBot ENCRYPT");
                transmitter.start();        // Run on its own thread
                transmitterText.setText(""); // Clear transmitter text field
                Log.i(LOGTAG, "send"); // Report to Logcat
            }
        });

    } //End of app onCreate method

    public String GetName(String s){
        String[] name = s.split(" ",3);
        Log.d(LOGTAG,name[1]);
        return name[1];
    }

    public String GetType(String s){
        String[] Parts = s.split(" ",3);
        Log.d(LOGTAG,Parts[0]);
        return Parts[0];
    }

    public StringBuilder StringtoBinary(String s){
        String[] text = s.split(" ", 3);
        if (text[2].equals("ENCRYPT"))
        {
            StringBuilder binary = new StringBuilder();
            binary.append("ENCRYPT");
            Log.d(LOGTAG,"ENCRYPTmsg");
            return binary;
        }
        else
        {
            byte[] bytes = text[2].getBytes();
            StringBuilder binary = new StringBuilder();
            for (byte b : bytes)
            {
                int val = b;
                for (int i = 0; i < 8; i++)
                {
                    binary.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
                binary.append(' ');
            }
            Log.d(LOGTAG,"nonaaaENCRYPTmsg");
            return binary;
        }
    }

/*  // Following code used when using basic activity
    @Override
    //Create an options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Uses res>menu>main.xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } //End of app onCreateOptionsMenu

    @Override
    //Called when an item is selected from the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //End of app onOptionsItemSelected method
*/

}//End of app MainActivity class