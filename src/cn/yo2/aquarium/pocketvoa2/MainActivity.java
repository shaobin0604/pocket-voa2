package cn.yo2.aquarium.pocketvoa2;


import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	
	private RadioGroup mTabs;
	private SlidingDrawer mPlayer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabs = (RadioGroup) findViewById(R.id.navi_bar);
        mTabs.setOnCheckedChangeListener(this);
        
        mPlayer = (SlidingDrawer) findViewById(R.id.player_drawer);
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_new:
			
			break;

		default:
			break;
		}
	}
    
    
}
