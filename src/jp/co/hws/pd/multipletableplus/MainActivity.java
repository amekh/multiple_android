package jp.co.hws.pd.multipletableplus;

import jp.co.hws.pd.multipletableplus.view.MainSurfaceView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * メインゲーム処理
 * @author Kouki Higashikawa
 *
 */
public class MainActivity extends Activity {

	private MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		int gameDiff = intent.getIntExtra("gameDiff", 0);		
		
		// BGMの再生関心
		mp = MediaPlayer.create(this, R.raw.mainbgm);
		mp.setLooping(true);
        mp.start();
		
		MainSurfaceView mainSurfaceView = new MainSurfaceView(this, gameDiff);		
		FrameLayout mainView = (FrameLayout)findViewById(R.id.panelFieldP);
		
		TextView num1 = (TextView)findViewById(R.id.num1);
		TextView num2 = (TextView)findViewById(R.id.num2);
		TextView ope = (TextView)findViewById(R.id.ope);
        ope.setTypeface(Typeface.createFromAsset(getAssets(), 
				"print_bold_tt.ttf"));
        num2.setTypeface(Typeface.createFromAsset(getAssets(), 
        		"print_bold_tt.ttf"));
		num1.setTypeface(Typeface.createFromAsset(getAssets(), 
        		"print_bold_tt.ttf"));      	
		
		mainView.addView(mainSurfaceView, 0);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mp.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mp.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
