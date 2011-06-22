package cn.yo2.aquarium.pocketvoa2.provider;

public class Entry extends BaseEntity {
	
	public String url;
	public String title;
	public String content;
	public String pubDate;
	public String localPath;
	
	public boolean hasTrans;
	public String transUrl;
	public String trans;
	public String localTransPath;
	
	public boolean hasMp3;
	public String mp3Url;
	public String localMp3Path;
	
	public boolean hasLrc;
	public String lrcUrl;
	public String localLrcPath;
}
