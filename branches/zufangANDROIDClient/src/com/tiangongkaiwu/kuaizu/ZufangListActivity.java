package com.tiangongkaiwu.kuaizu;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.tiangongkaiwu.utility.City;
import com.tiangongkaiwu.utility.ZufangConfig;

public class ZufangListActivity extends Activity {

    public static final String TAG = "ZufangListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zufanglist);
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        
        if (keyword == null || keyword.length() == 0) {
            this.setTitle(City.CITY[intent.getIntExtra("city", -1)] + "���ⷿ��Ϣ");
        } else {
            this.setTitle(City.CITY[intent.getIntExtra("city", -1)] + "���ⷿ��Ϣ"
                    + "-" + keyword);
        }
        ArrayList<ZufangInfo> list = intent.getParcelableArrayListExtra(ZufangService.ZUFANG_INFO);
        
        if(list != null && list.size() != 0){
        	updateListView(list);
        }else{
        	
			int city = intent.getIntExtra(ZufangService.CITY, -1);
			String category = intent.getStringExtra(ZufangService.CATEGORY);

			if (keyword == null || keyword.length() == 0) {
				this.setTitle(City.CITY[intent.getIntExtra("city", -1)]
						+ "���ⷿ��Ϣ");
			} else {
				this.setTitle(City.CITY[intent.getIntExtra("city", -1)]
						+ "���ⷿ��Ϣ" + "-" + keyword);
			}
        	final ProgressDialog dialog;
			dialog = new ProgressDialog(this);
			dialog.setTitle("�ⷿ��Ϣ��ѯ");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);

			GetZufangInfoAsyncTask.Callback new_request_callback = new GetZufangInfoAsyncTask.Callback() {

				@Override
				public void callback(ArrayList<ZufangInfo> list) {
					dialog.dismiss();
					if (list == null) {
						Toast
								.makeText(
										ZufangListActivity.this
												.getApplicationContext(),
										"Encounter a network probleam, please check your network configure!",
										Toast.LENGTH_LONG).show();
						return;
					};

					updateListView(list);
				}
			};

			if (intent.getStringExtra(ZufangService.KEYWORD) != null
					&& !intent.getStringExtra(ZufangService.KEYWORD).equals("")) {
				// start a progress
				final GetZufangInfoAsyncTask task = new GetZufangInfoAsyncTask(
						this, new_request_callback);
				task.execute(ZufangConfig.SERVER_URL + "?keyword=" + keyword
						+ "&city=" + city + "&category=" + category);
				dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						task.cancel(false);
					}
				});
				dialog.show();
			} else {
				final GetZufangInfoAsyncTask task = new GetZufangInfoAsyncTask(
						this, new_request_callback);
				// TODO: Local server test
				task.execute(ZufangConfig.SERVER_URL + "?city=" + city
						+ "&category=" + category);
				dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						task.cancel(false);
					}

				});
				dialog.show();
			}
        }
        
    }
    
    private void updateListView(List<ZufangInfo> list) {
		ZufangAdapter adapter = new ZufangAdapter(this, 0, list);
		ListView lv = (ListView) this.findViewById(R.id.zufangList);
		lv.setDividerHeight(5);
		lv.setAdapter(adapter);
		lv.setCacheColorHint(0);
	}
    
    @Override
    protected void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this); 
    }


	@Override
    protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this); 
    }

    public String changeCharset(String str, String oldCharset, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            // �þɵ��ַ���������ַ�����������ܻ�����쳣��
            byte[] bs = str.getBytes(oldCharset);
            // ���µ��ַ����������ַ���
            return new String(bs, newCharset);
        }
        return null;
    }
}
