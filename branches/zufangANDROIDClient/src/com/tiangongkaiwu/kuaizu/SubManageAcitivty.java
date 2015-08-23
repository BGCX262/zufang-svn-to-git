package com.tiangongkaiwu.kuaizu;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mobclick.android.MobclickAgent;
import com.tiangongkaiwu.utility.City;
import com.tiangongkaiwu.utility.ZufangConfig;
// 订阅管理页面
/**
 * @author wangjxin
 * 
 */
public class SubManageAcitivty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
		String keyword = ZufangConfig.getZufangConfig(this).getKeyword();
		String category = ZufangConfig.getZufangConfig(this).getCategory();
		try {
	        keyword = URLDecoder.decode(keyword, "gb2312");
        } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
        }
		int city = ZufangConfig.getZufangConfig(this).getCity();

		if (keyword == null || keyword.equals("")) {
			findViewById(R.id.del_sub).setVisibility(View.GONE);
			((TextView) findViewById(R.id.sub_info)).setText(R.string.no_sub_info);
		} else {
			((TextView) findViewById(R.id.sub_info)).setText(City.CITY[city] + "\u0020" + keyword+"\u0020"+category);
		}
		findViewById(R.id.del_sub).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = ZufangConfig.getZufangConfig(v.getContext()).getKeyword();
				if (keyword != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle(R.string.sub_del_confirm_title);
					builder.setMessage(R.string.sub_del_confirm_detail);
					builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent = new Intent(SubManageAcitivty.this, ZufangService.class);
							PendingIntent pendingIntent = PendingIntent
							        .getService(SubManageAcitivty.this, 0, intent, 0);
							AlarmManager alarmManager = (AlarmManager) SubManageAcitivty.this
							        .getSystemService(Context.ALARM_SERVICE);
							alarmManager.cancel(pendingIntent);

							// 更新系统变量，表示系统退出自动更新模式。 
							ZufangConfig.AUTO_UPDATE = false;

							// 删除搜所关键字
							ZufangConfig.getZufangConfig(SubManageAcitivty.this).saveKeyword("");
							// 返回前一个界面
							SubManageAcitivty.this.finish();

						}
					});
					builder.setNegativeButton(getString(R.string.cancel), null);
					builder.show();

				}
			}
		});

		// 用来设置自动查询时间间隔
		Spinner update_internal_spinner = (Spinner) findViewById(R.id.update_internal_spinner);
		String[] time_list = {"5分钟", "10分钟", "30分钟", "1小时" ,"2小时","3小时","4小时","5小时","6小时"};
		// android.R.layout.simple_list_item_1是系统的textview
		SpinnerAdapter sa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, time_list);
		update_internal_spinner.setAdapter(sa);

		int position = 0;
		for (int i = 0; i < time_list.length; i++) {
			if (time_list[i].equals(ZufangConfig.getZufangConfig(this).getUpdateInternal())) {
				position = i;
			}
		}
		update_internal_spinner.setSelection(position);

		// 保存用户选中的频率值
		update_internal_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				ZufangConfig.getZufangConfig(view.getContext()).setUpdateInternal((String) ((TextView) view).getText());

				// 系统只有处在自动更新模式下才生效。 
				if (ZufangConfig.AUTO_UPDATE == true) {
					changeUpdateInternal((int) changeToMillis((String) ((TextView) view).getText()));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		}); 

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

	/************************************************************
	 * 只支持单个单位，比如1分钟，1小时，并不支持2小时3分钟这样的时间格式。
	 ************************************************************/
	private long changeToMillis(String time) {
		long millis = 0;
		if (time.contains("分钟")) {
			String value = time.substring(0, time.indexOf("分钟"));
			millis += Long.parseLong(value) * 60 * 1000;
		}

		if (time.contains("小时")) {
			String value = time.substring(0, time.indexOf("小时"));
			millis += Long.parseLong(value) * 60 * 60 * 1000;
		}

		return millis;
	}

	/************************************************************
	 * 更改系统时钟提醒的时间间隔。
	 ************************************************************/
	public void changeUpdateInternal(int updateperiod) {
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ZufangService.class);
		intent.putExtra(ZufangService.IS_AUTO_UPDATE, true);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + updateperiod, updateperiod,
		        pendingIntent);
	}
}
