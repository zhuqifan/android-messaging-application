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

// Android apps must have a MainActivity class that extends Activity or AppCompatActivity class
public class MainActivity extends AppCompatActivity {
    //Declare class variables
    private static final String LOGTAG = "Main UI"; //Logcat messages from UI are identified
    private NetworkConnectionAndReceiver networkConnectionAndReceiver = null;
    private EditText transmitterText; //Transmitter data variable

    // Class methods
    @Override
    //Extend the onCreate method, called whenever an activity is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Extend the onCreate method
        // Set up the view using xml description res>layout>activity_main.xml
        setContentView(R.layout.activity_main);

        Log.i(LOGTAG, "Starting task4 app"); // Report to Logcat

        // Instantiate the network connection and receiver object
        networkConnectionAndReceiver = new NetworkConnectionAndReceiver(this);
        networkConnectionAndReceiver.start();    // Start socket-receiver thread


        // Get the receiving text area as defined in the Res dir xml code
        TextView receiverTextArea = findViewById(R.id.txtServerResponse);
        // Make the receiving text area scrollable
        receiverTextArea.setMovementMethod(new ScrollingMovementMethod());


        // Get the kill button as defined in the Res dir xml code
        Button killButton = findViewById(R.id.btnKill);
        // Make the kill button receptive to being clicked
        // Button click handler
        killButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the killButton object
            public void onClick(View v) {
                // OnClick actions here
                // Exit app
                System.exit(0);

            }
        });

        // Get the text area for commands to be transmitted as defined in the Res dir xml code
        transmitterText = (EditText) findViewById(R.id.cmdInput);
        // Get the send button as defined in the Res dir xml code
        Button sendButton =  findViewById(R.id.btnSendCmd);
        // Make the kill button receptive to being clicked
        // Button click handler
        sendButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText.getText().toString());
                    transmitter.start();        // Run on its own thread
                    transmitterText.setText(""); // Clear transmitter text field
                }
            }
        });


    } //End of app onCreate method

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