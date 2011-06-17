package cn.yo2.aquarium.pocketvoa2;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	
	private static final int TAB_RSS = 0;
	private static final int TAB_BROWSE = 1;
	private static final int TAB_DOWNLOAD = 2;
	
	private RadioGroup mTabs;
	private ViewFlipper mTabContentHolder;
	
	// +++ widget in TAB_RSS
	private Button mBtnFeedAdd;
	private Button mBtnFeedUpdate;
	private ExpandableListView mRssListView;
	
	// --- widget in TAB_RSS
	
	
	private SlidingDrawer mPlayer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabs = (RadioGroup) findViewById(R.id.navi_bar);
        mTabs.setOnCheckedChangeListener(this);
        
        mTabContentHolder = (ViewFlipper) findViewById(R.id.tab_content_holder);
        
        mRssListView = (ExpandableListView) findViewById(R.id.feed_list); 
        
        mPlayer = (SlidingDrawer) findViewById(R.id.player_drawer);
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_new:
			mTabContentHolder.setDisplayedChild(TAB_RSS);
			break;
		case R.id.radio_browse:
			mTabContentHolder.setDisplayedChild(TAB_BROWSE);
			break;
		case R.id.radio_download:
			mTabContentHolder.setDisplayedChild(TAB_DOWNLOAD);
			break;
		default:
			break;
		}
	}
    
    
}
