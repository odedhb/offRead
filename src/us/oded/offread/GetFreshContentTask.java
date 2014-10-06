package us.oded.offread;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetFreshContentTask extends AsyncTask<String, Integer, Boolean> {
	
	Activity act;
	
	public GetFreshContentTask(Activity act) {
		this.act = act;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Toast.makeText(act, "Getting new content", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			Utils.getFreshContent(act);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

@Override
protected void onPostExecute(Boolean result) {
	super.onPostExecute(result);
	Toast.makeText(act, "Finished getting new content", Toast.LENGTH_SHORT).show();

}

}
