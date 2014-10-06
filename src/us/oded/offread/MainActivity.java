package us.oded.offread;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Utils.initiateDemoData(getApplicationContext());
		

		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if(Utils.getAppFolder()==null)return 1;
			if(Utils.getAppFolder().listFiles()==null)return 1;
			if(Utils.getAppFolder().listFiles().length<1)return 1;
			
			return Utils.getAppFolder().listFiles().length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "error please report";
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int imageFileOrdinal = getArguments().getInt(ARG_SECTION_NUMBER)-1;
			
			File[] images = Utils.getArticlesImageFiles();

			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			
			TextView articleContent = (TextView) rootView.findViewById(R.id.section_content);
			TextView articleTitle = (TextView) rootView.findViewById(R.id.section_label);
			ImageView imageView = (ImageView) rootView.findViewById(R.id.top_image);
			Button sourceButton = (Button) rootView.findViewById(R.id.source_button);
			Button shareButton = (Button) rootView.findViewById(R.id.share_button);
			
			int displayHeight = Utils.getDisplayHeight(getActivity());
			View imageHolder = rootView.findViewById(R.id.image_holder);
			imageHolder.getLayoutParams().height = displayHeight*4/9;
			View footer = rootView.findViewById(R.id.footer);
			footer.getLayoutParams().height = displayHeight;

			
			if(images==null || images.length<1){
				articleTitle.setText("Wait for it, I'll fetch some stories in no time ;-)\n\nWhen I'm finished, you'll be able to swipe left to read'em.");
				return rootView;
			}
			
			File articleImageFile = images[imageFileOrdinal];
			String articlePhotoId = articleImageFile.getName();
			final Article a = Article.getArticle(getActivity().getApplicationContext(), articlePhotoId);
			
			if(a==null){
				articleTitle.setText("Can't load this article");
				return null;
			}
			

			articleContent.setText("Can't load content");
			articleTitle.setText("Can't load title");

			if(a.getText()!=null){
				Spanned articleText = Html.fromHtml(a.getText(),null,null);
				Utils.removeImageSpanObjects(articleText);
				articleContent.setText(articleText);
				articleContent.setMovementMethod(LinkMovementMethod.getInstance());

			}
			if(a.getTitle()!=null){
				articleTitle.setText(a.getTitle());
			}
			


			Bitmap articleBitmap = null;
			try {
				articleBitmap = Utils.decodeFileBitmapEfficientlyForDisplay(articleImageFile, getActivity());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(articleBitmap!=null){
				imageView.setImageBitmap(articleBitmap);
			}
			
			final String shareText =
					"Source: "+a.getLink()+"\n"+"\n"+
					a.getTitle()+"\n"+"\n"+
					"Read with: " + Utils.getInstallLink()+"\n";
			
			shareButton.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(
							Intent.EXTRA_TEXT,shareText);
							
					sendIntent.putExtra(Intent.EXTRA_TITLE, a.getTitle());
					//sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
					//sendIntent.setType("image/*");
					sendIntent.setType("text/plain");
					startActivity(sendIntent);
				}
			});			
			
			final Uri linkUri = Uri.parse(a.getLink());
			sourceButton.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {	
					Toast.makeText(getActivity(), "calling browser", Toast.LENGTH_SHORT).show();
					Intent browseIntent = new Intent(Intent.ACTION_VIEW, linkUri);
					startActivity(browseIntent);
				}
			});
			
			sourceButton.setText(a.getLink());			

			return rootView;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		GetFreshContentTask t = new GetFreshContentTask(this);
		t.execute("");
	}

}
