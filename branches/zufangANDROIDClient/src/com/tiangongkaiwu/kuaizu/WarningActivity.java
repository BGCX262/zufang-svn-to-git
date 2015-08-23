package com.tiangongkaiwu.kuaizu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobclick.android.MobclickAgent;

public class WarningActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout rl = new RelativeLayout(this);
		setContentView(rl);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		
		rl.setGravity(Gravity.CENTER);
		
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.cry);
		rl.setBackgroundResource(android.R.color.white);
		rl.addView(iv, lp);
	}
	@Override
    protected void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this); 
    }


	@Override
    protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this); 
    }
}
