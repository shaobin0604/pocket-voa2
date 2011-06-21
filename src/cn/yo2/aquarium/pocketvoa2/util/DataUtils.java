package cn.yo2.aquarium.pocketvoa2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.yo2.aquarium.pocketvoa2.provider.Feed;
import cn.yo2.aquarium.pocketvoa2.provider.FeedItem;

public class DataUtils {
	private DataUtils() {}
	
	public static Uri createFeed(Context context, String name, String url) {
		if (isFeedExist(context, url)) {
			Logger.e("Skip existing Feed, name = " + name + ", url = " + url);
			return null;
		}
		
		ContentResolver resolver = context.getContentResolver();
		
		ContentValues values = new ContentValues(2);
		values.put(Feed.Columns.NAME, name);
		values.put(Feed.Columns.URL, url);
		
		return resolver.insert(Feed.contentUri(), values);
	}
	
	public static boolean isFeedExist(Context context, String url) {
		Logger.d("url = " + url);
		
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(Feed.contentUri(), Feed.Columns.COLUMNS, Feed.Columns.URL + " = ?", new String[] {url}, null);
		
		if (cursor != null) {
			try {
				return cursor.getCount() > 0;
			} finally {
				cursor.close();
			}
		} else {
			return false;
		}
	}
	
	public static List<Feed> queryFeedList(Context context) {
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(Feed.contentUri(), Feed.Columns.COLUMNS, null, null, null);
		
		if (cursor != null) {
			try {
				List<Feed> list = new ArrayList<Feed>();
				while (cursor.moveToNext()) {
					list.add(new Feed(cursor));
				}
				
				return list;
			} finally {
				cursor.close();
			}
		}
		return Collections.emptyList();
	}
	
	private static boolean isFeedItemExist(Context context, String url) {
		Logger.d("url = " + url);
		
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(FeedItem.contentUri(), FeedItem.Columns.COLUMNS, FeedItem.Columns.URL + " = ?", new String[] {url}, null);
		
		if (cursor != null) {
			try {
				return cursor.getCount() > 0;
			} finally {
				cursor.close();
			}
		} else {
			return false;
		}
	}
	
	public static Uri createFeedItem(Context context, String title, String url, String description, String pubDate, long feedId) {
		if (isFeedItemExist(context, url)) {
			Logger.e("Skip existing FeedItem, title = " + title + "url = " + url);
			return null;
		}
		
		ContentResolver resolver = context.getContentResolver();
		
		ContentValues values = new ContentValues(5);
		values.put(FeedItem.Columns.TITLE, title);
		values.put(FeedItem.Columns.URL, url);
		values.put(FeedItem.Columns.DESCRIPTION, description);
		values.put(FeedItem.Columns.PUBDATE, pubDate);
		values.put(FeedItem.Columns.FEED_ID, feedId);
		
		return resolver.insert(FeedItem.contentUri(), values);
	}
}
