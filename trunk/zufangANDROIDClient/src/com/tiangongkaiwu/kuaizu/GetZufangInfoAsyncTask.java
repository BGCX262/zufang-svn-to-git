package com.tiangongkaiwu.kuaizu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetZufangInfoAsyncTask extends AsyncTask<String, Integer, ArrayList<ZufangInfo>> {

	public interface Callback {
		void callback(ArrayList<ZufangInfo> list);
	}

	private static final String TAG = "GetZufangInfoAsyncTask";

	private Callback callback;
	
	public GetZufangInfoAsyncTask(Context context, Callback callback) {
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	public HttpResponse requestZufangInfo(String url, HttpClient httpclient) {
		Log.d(TAG, url);
		HttpGet getMsg = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(getMsg);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return null;
		}

		return response;
	}

	public HttpClient initialHttpAgent() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		return httpclient;
	}

	public ArrayList<ZufangInfo> parseZufangInfoToArray(HttpResponse zufangInfo) {
		ArrayList<ZufangInfo> infoList = new ArrayList<ZufangInfo>();
		HttpEntity entity = zufangInfo.getEntity();

		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = entity.getContent();

			// must specify the encoding otherwise the Chinese in the
			// content will not be showed normally.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"));
			String temp;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}

			JSONArray zufangList = new JSONArray(sb.toString());
			for (int i = 0; i < zufangList.length(); i++) {
				JSONObject jsonInfo = (JSONObject) zufangList.get(i);
				ZufangInfo zuInfo = new ZufangInfo();

				zuInfo.setDetailLink(jsonInfo.getString("detailLink"));
				zuInfo.setLongDes(jsonInfo.getString("longDes"));
				zuInfo.setPrice("¼Û¸ñ£º" + jsonInfo.getString("price"));
				zuInfo.setPastTime(jsonInfo.getString("pastTime"));
				zuInfo.setShortDes(jsonInfo.getString("shortDes"));

				infoList.add(zuInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infoList;
	}

	@Override
	protected ArrayList<ZufangInfo> doInBackground(String... params) {

		HttpResponse zufangInfo = requestZufangInfo(params[0],initialHttpAgent());
		// the network has problem.
		if (zufangInfo == null) {
			return null;
		}
		return parseZufangInfoToArray(zufangInfo);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(ArrayList<ZufangInfo> result) {
		callback.callback(result);
		super.onPostExecute(result);

	}

}
