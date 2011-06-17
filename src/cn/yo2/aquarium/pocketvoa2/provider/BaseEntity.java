package cn.yo2.aquarium.pocketvoa2.provider;

public class BaseEntity {
	protected static final String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";
	protected static final String TYPE_TEXT = "TEXT";
	protected static final String TYPE_TEXT_UNIQUE = "TEXT UNIQUE";
	protected static final String TYPE_DATETIME = "DATETIME";
	protected static final String TYPE_INT = "INTEGER";
	
	protected static final String AUTHORITY = "cn.yo2.aquarium.pocketvoa2.provider";
	protected static final String CONTENT_URI_PREFIX = "content://" + AUTHORITY;
	
	long _id;
}
