package com.tipusarowar.rapidball;

//import com.example.android.lunarlander.LunarView;
//import com.example.android.lunarlander.LunarView.LunarThread;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.tipusarowar.rapidball.RapidView.RapidThread;;

public class RapidBallActivity extends Activity {

	

    /** A handle to the thread that's actually running the animation. */
    private RapidThread rRapidThread;

    /** A handle to the View in which the game is running. */
    private RapidView rRapidView;

	private Button startGameButton;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startGameButton = (Button) findViewById(R.id.startGame);
      
        
        startGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(new RapidView(RapidBallActivity.this));
			}
		});
        /*
        rRapidView = new RapidView(RapidBallActivity.this);// =(RapidView) findViewById(R.id.rapid);
        rRapidThread = rRapidView.getThread();
    */
    }
}

