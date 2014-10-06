package us.oded.offread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

public class Utils {


	public static Bitmap decodeFileBitmapEfficientlyForDisplay(File f, Activity act) throws IOException{
		
		//get screen properties		
		Display display = act.getWindowManager().getDefaultDisplay();
		//Point size = new Point();
		//display.getSize(size);
		//int displayWidth = size.x;
		//int displayHeight = size.y;
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		int displayWidth = outMetrics.widthPixels;
		int d = outMetrics.densityDpi;
		
		//get image size
		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		FileInputStream tfis = new FileInputStream(f);
		BitmapFactory.decodeStream(tfis, null, o);
		int imageHeight = o.outHeight;
		int imageWidth = o.outWidth;
		tfis.close();
		
		int imageToDisplayRatio = imageWidth*10000/displayWidth;
		
		
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		int targetDensity = d*imageToDisplayRatio/10000;
		o2.inTargetDensity = targetDensity;
		//o2.inSampleSize = 2;
		
		FileInputStream fis = new FileInputStream(f);

		Bitmap b = BitmapFactory.decodeStream(fis, null, o2);

		return b;
	}



	//decodes image and scales it to reduce memory consumption
	public static Bitmap oldDecodeFileBitmapEfficientlyForDisplay(File f, Activity act) throws IOException{
		
		//get screen size
		int displayWidth = Utils.getDisplayWidth(act);
		int displayHeight = Utils.getDisplayHeight(act);

		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		FileInputStream fis = new FileInputStream(f);
		Bitmap tmp = BitmapFactory.decodeStream(fis, null, o);
		fis.close();
		
		int targetHeight = displayWidth;
		int targetWidth = displayWidth;


		//int samplingFactor = getSamplingFactorByLog(targetWidth, targetHeight, o.outWidth,o.outHeight);

		if (tmp!= null) {
			tmp.recycle();
		}
		//Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		//o2.inSampleSize = samplingFactor;

		//even better
		o2.inPurgeable = true;
		o2.inInputShareable = true;
		o2.inTempStorage = new byte[128*1024];
		o2.inPreferredConfig = Bitmap.Config.RGB_565;
		o2.outWidth = 20;

		fis = new FileInputStream(f);
		Bitmap b = BitmapFactory.decodeStream(fis, null, o2);

		fis.close();
		if (b == null) return null;
		Log.d("bitmaps","Created bitmap:" + b.hashCode());
		System.gc();
		return b;
	}


	public static int getCameraPhotoOrientation(String imagePath){
		int rotate = 0;
		try {

			ExifInterface exif = new ExifInterface(imagePath);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}



	private static int getSamplingFactorByLog(int targetWidth,
			int targetHeight, int width, int height) {
		int samplingFactor = 1;
		if (width > targetWidth || height > targetHeight) {
			final double logBase = Math.log(0.5);
			double ratio = Math.max(targetHeight, targetWidth) / (double) Math.max(height, width);
			//closest power of 2
			samplingFactor = (int)Math.pow(2, (int) Math.round(Math.log(ratio) / logBase));
		}
		return samplingFactor;
	}



	public static String getString (Context ctx, String sharedPreferenceKey){
		SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(ctx);
		return appSettings.getString(sharedPreferenceKey, null);
	}

	public static void putString (Context ctx, String sharedPreferenceKey, String value){
		SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(ctx);
		appSettings.edit().putString(sharedPreferenceKey, value).commit();
	}	
	public static void remove(Context ctx, String sharedPreferenceKey) {
		SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(ctx);
		appSettings.edit().remove(sharedPreferenceKey).commit();
	}


	public static void initiateDemoData(Context ctx){
		Article article1 = new Article("iTunespiece_Advertising_2003_sillouhette2_1020.jpg", "iTunes is 10 years old", "Can you believe that iTunes is 10 years old god damn it??!?!?!", "http://itunes.com");
		Article article2 = new Article("THE-MACHINE_Dir-Caradog-James_Fight_Photo_Courtesy_Red-and-Black-hero.jpg", "the machine is coming for us!", "And it will get us all, really, no kidding.", "http://google.com");
		Article article3 = new Article("shutterstock_123846706_large.jpg", "How can bitCoin change the world", "By democratizing money and making it inflation free. Nintendo has begun selling refurbished 3DS and DSi XL consoles at aggressive price points, offering buyers the same one-year warranty that typically accompanies new hardware. The units are guaranteed to be in working order, though the manufacturer admits they may contain 'minor cosmetic blemishes.' That said, if you're willing to put up with some wear and tear, there are good savings to be found here. Available options range in price between $99.99 and $169.99 depending on the specific bundle you're looking at, but you'd be hard-pressed to find better rates elsewhere. Nintendo says its latest store additions are 'the only refurbished products in the market that have been cleaned, tested, and inspected to meet Nintendo&rsquo;s high...", "http://bitcoin.com");
		Article.addArticle(ctx, article1);
		Article.addArticle(ctx, article2);
		Article.addArticle(ctx, article3);

	}


	public static void getFreshContent(Activity act) throws Exception {
		String[] urlStrings = getSubscriptions();

		//loop subscriptions
		for (int sub = 0; sub < urlStrings.length; sub++) {


			//URL url  = new URL("http://feeds.feedburner.com/TechCrunch/");
			String urlString = urlStrings[sub];
			URL url  = new URL(urlString);		

			XmlReader reader = null;

			try {

				reader = new XmlReader(url);
				SyndFeed feed = new SyndFeedInput().build(reader);
				//System.out.println("Feed Title: "+ feed.getAuthor());

				for (Iterator<?> i = feed.getEntries().iterator(); i.hasNext();) {

					SyndEntry entry = (SyndEntry) i.next();
					String uriMd5GeneratedPhotoId = md5(entry.getUri())+".jpg";

					if(Article.getArticle(act, 
							uriMd5GeneratedPhotoId)!=null){
						continue;
					}

					String articleText = null;

					if(entry.getContents()!=null && entry.getContents().get(0)!=null){
						articleText = ((SyndContent)entry.getContents().get(0)).getValue();	
					}else if(entry.getDescription()!=null){
						articleText = entry.getDescription().getValue();
					}

					int imageIndex = articleText.indexOf("src=");
					int spaceAfterImageIndex = articleText.indexOf(" ", imageIndex);
					String img = articleText.substring(imageIndex+4, spaceAfterImageIndex);
					img = img.replaceAll("\"", "");
					img = img.replaceAll("'", "");
					if(img.contains("?")){
						img = img.substring(img.indexOf("http://"),img.indexOf("?"));
					}				


					downloadFileAndSaveToFolder(img, getAppFolder().getAbsolutePath(), uriMd5GeneratedPhotoId);

					Article a = new Article(uriMd5GeneratedPhotoId, entry.getTitle(), articleText, entry.getUri());
					Article.addArticle(act, a);
				}
			} finally {
				if (reader != null)
					reader.close();
			}
		}

	}


	private static String[] getSubscriptions() {
		String[] urlStrings = {
				"http://www.theverge.com/rss/index.xml",
				"http://feeds.feedburner.com/TechCrunch/",
				"http://www.engadget.com/rss.xml",
		};
		return urlStrings;
	}


	public static void downloadFileAndSaveToFolder(
			String fileUrl, String targetFolder, String fileName) {

		try{

			URL url = new URL(fileUrl);//works with: http://oded.biz/images/5/5a/Profile.jpg

			//create the new connection
			URLConnection urlConnection = url.openConnection();
			//HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			//set up some things on the connection
			//urlConnection.setRequestMethod("GET");
			//urlConnection.setDoOutput(true);

			//and connect!
			urlConnection.connect();

			//this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			//set the path where we want to save the file
			//in this case, going to save it on the root directory of the
			//sd card.

			//create a new file, specifying the path, and the filename
			//which we want to save the file as.
			File file = new File(targetFolder,fileName+".tmp");

			//this will be used to write the downloaded data into the file we created
			FileOutputStream fileOutput = new FileOutputStream(file);


			//this is the total size of the file
			//int totalSize = urlConnection.getContentLength();
			//variable to store total downloaded bytes
			//int downloadedSize = 0;

			//create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; //used to store a temporary size of the buffer

			//now, read through the input buffer and write the contents to the file
			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
				//add the data in the buffer to the file in the file output stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				//add up the size so we know how much is downloaded

				//this is where you would do something to report the prgress, like this maybe
				//downloadedSize += bufferLength;
				//updateProgress(downloadedSize, totalSize);

			}
			//close the output stream when done
			fileOutput.close();

			//remove .tmp suffix
			String newFileName = file.getAbsolutePath().replace(".tmp", "");
			File newFile = new File(newFileName);
			if(file.exists()){
				file.renameTo(newFile);
			}

		}catch (IOException e){
			e.printStackTrace();
		}
	}


	public static File getAppFolder() {
		File appFolder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/"
				+ "offread");

		if(!appFolder.exists())appFolder.mkdirs();

		return appFolder;

	}

	public static String md5(String s) 
	{
		MessageDigest digest;
		try 
		{
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(),0,s.length());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		return "";
	}


	public static File[] getArticlesImageFiles() {
		File appFolder = Utils.getAppFolder();
		File[] images = appFolder.listFiles();

		if(images == null || images.length < 1) {
			return null;
		}
		Arrays.sort(images, new Comparator<File>(){
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			} 
		});	
		return images;
	}

	public static Point getDisplaySize(Activity act){
		Display display = act.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static int getDisplayWidth(Activity act){
		return getDisplaySize(act).x;
	}	
	public static int getDisplayHeight(Activity act){
		return getDisplaySize(act).y;
	}

	public static Spanned removeImageSpanObjects(Spanned articleText) {
		SpannableStringBuilder spannedStr = (SpannableStringBuilder) articleText;
		Object[] spannedObjects = spannedStr.getSpans(0, spannedStr.length(),
				Object.class);
		for (int i = 0; i < spannedObjects.length; i++) {
			if (spannedObjects[i] instanceof ImageSpan) {
				ImageSpan imageSpan = (ImageSpan) spannedObjects[i];
				spannedStr.replace(spannedStr.getSpanStart(imageSpan),spannedStr.getSpanEnd(imageSpan), "");
			}
		}
		//spannedStr.getChars(start, end, dest, destoff)
		//spannedStr.replace(start, end, tb, tbstart, tbend)
		return spannedStr;
	}
	
	public static String getInstallLink(){
		return "http://play.google.com/offread";
	}

}



