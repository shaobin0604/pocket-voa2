package cn.yo2.aquarium.pocketvoa2.provider;

import java.util.Date;

import android.net.Uri;
import android.provider.BaseColumns;

public class FeedItem extends BaseEntity {
	protected static final String PATH = "/items/";
	
	String url;
	String title;
	String description;
	Date pubDate;
	long feedId;
	
	public static final class Columns implements BaseColumns {
		public static final String TITLE = "title";
		public static final String URL = "url";
		public static final String DESCRIPTION = "description";
		public static final String PUBDATE = "pubdate";
		public static final String FEED_ID = "feed_id";
		
		public static final String[] COLUMNS = {_ID, TITLE, URL, DESCRIPTION, PUBDATE, FEED_ID};
		public static final String[] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT, TYPE_TEXT_UNIQUE, TYPE_TEXT, TYPE_DATETIME, TYPE_INT};
		
		public static final String TABLE_NAME = "feed_items";
		
		public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
			+ _ID 			+ " " + TYPE_PRIMARY_KEY 	+ ","
			+ TITLE 		+ " " + TYPE_TEXT 			+ ","
			+ URL 			+ " " + TYPE_TEXT_UNIQUE 	+ ","
			+ DESCRIPTION 	+ " " + TYPE_TEXT 			+ ","
			+ PUBDATE 		+ " " + TYPE_DATETIME 		+ ","
			+ FEED_ID		+ " " + TYPE_INT			+ ");";
		
		public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";"; 
	}
	
	public static final Uri contentUri(long feedId) {
		return Uri.parse(new StringBuilder(CONTENT_URI_PREFIX).append("/feeds/").append(feedId).append("/items/").toString());
	}
	
	public static final Uri contentUri(long feedId, String itemId) {
		return Uri.parse(new StringBuilder(CONTENT_URI_PREFIX).append("/feeds/").append(feedId).append("/items/").append(itemId).toString());
	}
}
