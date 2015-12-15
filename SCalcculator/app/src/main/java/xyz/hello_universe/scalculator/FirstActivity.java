package xyz.hello_universe.scalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FirstActivity extends AppCompatActivity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private Button button;
    private boolean tapped=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firstscreen);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        if (!tapped) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Start();
                }
            }, AUTO_HIDE_DELAY_MILLIS);
        }
    }


    public void QuickStart(View view) {
        button = (Button)view;
        tapped = true;
        Start();
    }

    public void Start() {
        final Intent mainIntent = new Intent(FirstActivity.this, MainActivity.class);
        FirstActivity.this.startActivity(mainIntent);
        FirstActivity.this.finish();
    }
}
