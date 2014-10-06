package us.oded.offread;

import android.content.Context;

public class Article {
	
	static String TITLE_PREFIX = "TITLE_";
	static String TEXT_PREFIX = "TEXT_";
	static String LINK_PREFIX = "LINK_";
	
String photoId;
	public String getPhotoId() {
	return photoId;
}
public void setPhotoId(String photoId) {
	this.photoId = photoId;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}
public String getLink() {
	return link;
}
public void setLink(String link) {
	this.link = link;
}
	String title;
String text;
String link;


public static Article getArticle(Context ctx, String articlePhotoId){
	if(Utils.getString(ctx, LINK_PREFIX+articlePhotoId)!=null){
		return new Article(articlePhotoId,
				Utils.getString(ctx, TITLE_PREFIX+articlePhotoId),
				Utils.getString(ctx, TEXT_PREFIX+articlePhotoId),
				Utils.getString(ctx, LINK_PREFIX+articlePhotoId)
				);
	}
	return null;
}

public Article(String photoId, String title, String text, String link) {
	this.photoId = photoId;
	this.title = title;
	this.text = text;
	this.link = link;
}
public static void addArticle(Context ctx, Article article){
	String articlePhotoId = article.getPhotoId();
	Utils.putString(ctx, TITLE_PREFIX+articlePhotoId, article.getTitle());		
	Utils.putString(ctx, TEXT_PREFIX+articlePhotoId, article.getText());		
	Utils.putString(ctx, LINK_PREFIX+articlePhotoId, article.getLink());
}

}
