package cn.yo2.aquarium.pocketvoa2.provider;

import java.util.Date;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Feed extends BaseEntity {
	String name;
	String url;
	Date pubDate;
	
	public static final class Columns implements BaseColumns {
		public static final String NAME = "name";
		public static final String URL = "url";
		public static final String PUBDATE = "pubdate";
		
		public static final String[] COLUMNS = {_ID, NAME, URL, PUBDATE};
		public static final String[] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT, TYPE_TEXT_UNIQUE, TYPE_DATETIME};
		
		public static final String TABLE_NAME = "feeds";
		
		public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
			+ _ID 		+ " " + TYPE_PRIMARY_KEY 	+ ","
			+ NAME 		+ " " + TYPE_TEXT 			+ ","
			+ URL 		+ " " + TYPE_TEXT_UNIQUE 	+ ","
			+ PUBDATE 	+ " " + TYPE_DATETIME 		+ ");";
		
		public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";"; 
	}
	
	
	
	public static final Uri contentUri() {
		return Uri.parse(new StringBuilder(CONTENT_URI_PREFIX).append("/feeds/").toString());
	}
	
	public static final Uri contentUri(long feedId) {
		return Uri.parse(new StringBuilder(CONTENT_URI_PREFIX).append("/feeds/").append(feedId).toString());
	}
}
