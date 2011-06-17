package cn.yo2.aquarium.pocketvoa2.provider;

import java.io.File;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;

public class Provider extends ContentProvider {
	private static boolean sUseSD;
	
	private static final String APP_DIR = Environment.getExternalStorageDirectory() + "/pocketvoa2/";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "pocketvoa2.db";
	
	private static final int URI_FEEDS = 1;
	private static final int URI_FEED = 2;
	private static final int URI_FEED_ENTRIES = 3;
	private static final int URI_FEED_ENTRY = 4;
	private static final int URI_ENTRIES = 5;
	private static final int URI_ENTRY = 6;
	
	private static UriMatcher URI_MATCHER;
	
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "feeds", URI_FEEDS);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "feeds/#", URI_FEED);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "feeds/#/entries", URI_FEED_ENTRIES);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "feeds/#/entries/#", URI_FEED_ENTRY);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "entries", URI_ENTRIES);
		URI_MATCHER.addURI(BaseEntity.AUTHORITY, "entries/#", URI_ENTRY);
	}
	
	private static class DatabaseHelper {
		private SQLiteDatabase database;
		
		public DatabaseHelper(Context context, String name, int version) {
			File file = new File(APP_DIR);
			
			if ((file.exists() && file.isDirectory() || file.mkdir()) && file.canWrite()) {
				try {
					database = SQLiteDatabase.openDatabase(APP_DIR + name, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
					
					if (database.getVersion() == 0) {
						onCreate(database);
					} else {
						onUpgrade(database, database.getVersion(), DATABASE_VERSION);
					}
					database.setVersion(DATABASE_VERSION);
					sUseSD = true;
				} catch (SQLException sqlException) {
					database = new SQLiteOpenHelper(context, name, null, version) {
						@Override
						public void onCreate(SQLiteDatabase db) {
							DatabaseHelper.this.onCreate(db);
						}

						@Override
						public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
							DatabaseHelper.this.onUpgrade(db, oldVersion, newVersion);
						}
					}.getWritableDatabase();
					sUseSD = false;
				}
			} else {
				database = new SQLiteOpenHelper(context, name, null, version) {
					@Override
					public void onCreate(SQLiteDatabase db) {
						DatabaseHelper.this.onCreate(db);
					}

					@Override
					public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						DatabaseHelper.this.onUpgrade(db, oldVersion, newVersion);
					}
				}.getWritableDatabase();
				sUseSD = false;
			}
		}

		public void onCreate(SQLiteDatabase database) {
			database.execSQL(createTable(Feed.Columns.TABLE_NAME, Feed.Columns.COLUMNS, Feed.Columns.TYPES));
			database.execSQL(createTable(FeedItem.Columns.TABLE_NAME, FeedItem.Columns.COLUMNS, Feed.Columns.TYPES));
		}
		
		private String createTable(String tableName, String[] columns, String[] types) {
			if (tableName == null || columns == null || types == null || types.length != columns.length || types.length == 0) {
				throw new IllegalArgumentException("Invalid parameters for creating table "+tableName);
			} else {
				StringBuilder stringBuilder = new StringBuilder("CREATE TABLE ");
				
				stringBuilder.append(tableName);
				stringBuilder.append(" (");
				for (int n = 0, i = columns.length; n < i; n++) {
					if (n > 0) {
						stringBuilder.append(", ");
					}
					stringBuilder.append(columns[n]).append(' ').append(types[n]);
				}
				return stringBuilder.append(");").toString();
			}
		}

		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			
		}

		public SQLiteDatabase getWritableDatabase() {
			return database;
		}
	}
	
	private SQLiteDatabase mDatabase;
	
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long newId = -1;
		
		int option = URI_MATCHER.match(uri);
		
		switch (option) {
			case URI_FEEDS: {
				newId = mDatabase.insert(Feed.Columns.TABLE_NAME, null, values);
				break;
			}
			case URI_FEED_ENTRIES: {
				values.put(FeedItem.Columns.FEED_ID, uri.getPathSegments().get(1));
				newId = mDatabase.insert(FeedItem.Columns.TABLE_NAME, null, values);
				break;
			}
			case URI_ENTRIES: {
				newId = mDatabase.insert(FeedItem.Columns.TABLE_NAME, null, values);
				break;
			}
			default: 
				throw new IllegalArgumentException("Illegal insert " + uri);
		}
		if (newId > -1) {
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(uri, newId);
		} else {
			throw new SQLException("Could not insert row into " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mDatabase = new DatabaseHelper(getContext(), DATABASE_NAME, DATABASE_VERSION).getWritableDatabase();
		
		return mDatabase != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
