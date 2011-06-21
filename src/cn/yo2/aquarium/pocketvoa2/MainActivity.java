package cn.yo2.aquarium.pocketvoa2;


import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.yo2.aquarium.pocketvoa2.provider.Feed;
import cn.yo2.aquarium.pocketvoa2.provider.FeedItem;
import cn.yo2.aquarium.pocketvoa2.util.DataUtils;
import cn.yo2.aquarium.pocketvoa2.util.FeedUtils;
import cn.yo2.aquarium.pocketvoa2.util.Logger;
import cn.yo2.aquarium.pocketvoa2.util.FeedUtils.FeedException;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	
	private static final int TAB_RSS = 0;
	private static final int TAB_BROWSE = 1;
	private static final int TAB_DOWNLOAD = 2;
	
	private static final int DLG_FEED_LIST = 1;
	
	private RadioGroup mTabs;
	private ViewFlipper mTabContentHolder;
	
	// +++ widget in TAB_RSS +++
	private Button mBtnFeedAdd;
	private Button mBtnFeedUpdate;
	private ExpandableListView mFeedListView;
	// --- widget in TAB_RSS ---
	
	private String[] mFeedListNames;
	private String[] mFeedListURLs;
	
	private SlidingDrawer mPlayer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabs = (RadioGroup) findViewById(R.id.navi_bar);
        mTabs.setOnCheckedChangeListener(this);
        
        mTabContentHolder = (ViewFlipper) findViewById(R.id.tab_content_holder);
        
        
        
        mPlayer = (SlidingDrawer) findViewById(R.id.player_drawer);
        
        mBtnFeedAdd = (Button) findViewById(R.id.btn_feed_add);
        mBtnFeedUpdate = (Button) findViewById(R.id.btn_feed_update);
        
        mBtnFeedAdd.setOnClickListener(this);
        mBtnFeedUpdate.setOnClickListener(this);
        
        mFeedListView = (ExpandableListView) findViewById(R.id.feed_list); 
        
        Resources resources = getResources();
        
        mFeedListNames = resources.getStringArray(R.array.feed_list_names);
        mFeedListURLs = resources.getStringArray(R.array.feed_list_values);
    }
    
    @Override
    public void onClick(View v) {
    	if (v == mBtnFeedAdd) {
    		showFeedListDialog();
    	} else if (v == mBtnFeedUpdate) {
    		updateFeedList();
    	}
    }
    
    private UpdateFeedListTask mUpdateFeedListTask;
    
    private void updateFeedList() {
    	List<Feed> feedList = DataUtils.queryFeedList(getApplicationContext());
    	
    	mUpdateFeedListTask = new UpdateFeedListTask();
    	mUpdateFeedListTask.execute(feedList);
    }
    
    private class UpdateFeedListTask extends AsyncTask<List<Feed>, String, Void> {
    	private static final int STATE_SUCCESS = 0;
    	private static final int STATE_PARTIAL_SUCCESS = 1;
    	private static final int STATE_FAIL = 2; 
    	
    	private ProgressDialog mProgressDialog;
    	
		@Override
		protected Void doInBackground(List<Feed>... params) {
			List<Feed> feedList = params[0];
			
			int feedCount = feedList.size();
			
			for (Feed feed : feedList) {
				
				try {
					publishProgress(feed.name);
					
					List<FeedItem> items = FeedUtils.getFeedItems(feed.url);
					
					for (FeedItem item : items) {
						DataUtils.createFeedItem(getApplicationContext(), item.title, item.url, item.description, item.pubDate, feed._id);
					}
					
				} catch (FeedException e) {
					Logger.e("Error when get feed " + feed.url, e);
				}
			}
			
			return (Void)null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.show();
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			mProgressDialog.setMessage(values[0]);
		}
    	
    }

	private void showFeedListDialog() {
    	showDialog(DLG_FEED_LIST);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
		case DLG_FEED_LIST: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose Feed");
			builder.setItems(R.array.feed_list_names, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataUtils.createFeed(getApplication(), mFeedListNames[which], mFeedListURLs[which]);
				}
			});
			
			return builder.create();
		}
		default:
			break;
		}
    	return super.onCreateDialog(id);
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
