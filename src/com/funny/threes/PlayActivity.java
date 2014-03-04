package com.funny.threes;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class PlayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
//		Number number = new Number(this);
//		number.initNumber(50, 50, 100, 100);
//		setContentView(number);
//		
//		AnimationSet animationSet = new AnimationSet(true);
//		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
//				Animation.RELATIVE_TO_SELF, 1f,
//				Animation.RELATIVE_TO_SELF, 1f);
//		animationSet.addAnimation(scaleAnimation);
//		animationSet.setDuration(2000);
//		number.startAnimation(animationSet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

}
