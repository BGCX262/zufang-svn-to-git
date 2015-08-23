package com.tiangongkaiwu.kuaizu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.IBinder;
import android.widget.Toast;

import com.tiangongkaiwu.utility.MyLog;
import com.tiangongkaiwu.utility.ZufangConfig;
import com.tiangongkaiwu.utility.ZufangConfig.TimeConfig;

public class ZufangService extends Service {

	public static final String TAG = "ZufangService";
	public static final String IS_AUTO_UPDATE = "is_auto_update";
	public static final String ZUFANG_INFO = "zufang_info";
	public static final String NEED_UPDATE = "need_update";
	public static final String KEYWORD = "keyword";
	public static final String CATEGORY = "category";
	public static final String CITY = "city";
	private String keyword = "";
	private String category = "hezu";
	private int city = -1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	// notify user that there is new zufangInfo updated.
	private void notifyUserUpdateInfo(ArrayList<ZufangInfo> list) {
		NotificationManager notifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		// initiate the notification.
		Notification notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.tickerText = String.format(getString(R.string.ticker_text), list.size());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		long vibrate[] = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;

		// define the notification's expanded message and intent
		Intent intent = new Intent(this, ZufangTab.class);
		keyword = ZufangConfig.getZufangConfig(this).getKeyword();
		city = ZufangConfig.getZufangConfig(this).getCity();
		category = ZufangConfig.getZufangConfig(this).getCategory();
		
		intent.putExtra(CITY, city);
		intent.putExtra(KEYWORD, keyword);
		intent.putExtra(CATEGORY, category);
		// the zufanglistactivity will different the new request or update
		// request use this flag.

		intent.putParcelableArrayListExtra(ZUFANG_INFO, list);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, getString(R.string.app_name), list.get(list.size()-1).getLongDes(),pendingIntent);
		notifyManager.notify(1, notification);
	}

	/***********************************************************
	 * 开始定时更新
	 ***********************************************************/
	public void startIntermittentUpdate(int updateperiod) {
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ZufangService.class);
		intent.putExtra(IS_AUTO_UPDATE, true);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System
				.currentTimeMillis()
				+ updateperiod, updateperiod, pendingIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doServe(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void doServe(Intent intent) {
		// 请求来自用户操作
		if (!intent.getBooleanExtra(IS_AUTO_UPDATE, false)) {
			// whether need to be notified when updated.
			if (intent.getBooleanExtra(NEED_UPDATE, false)) {
				startIntermittentUpdate((int) changeToMillis(ZufangConfig
						.getZufangConfig(this).getUpdateInternal()));

				/** 更新系统变量，表示系统进入自动更新模式。 */
				ZufangConfig.AUTO_UPDATE = true;
			}

			try {
				keyword = intent.getStringExtra(KEYWORD);
				keyword = URLEncoder.encode(keyword, "gb2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 如果用户选择了提醒功能
			if (intent.getBooleanExtra(NEED_UPDATE, false)) {
				if (intent.getStringExtra(KEYWORD) != null) {
					ZufangConfig.getZufangConfig(this).saveKeyword(keyword);
				}
			}
			city = intent.getIntExtra(CITY, -1);
			ZufangConfig.getZufangConfig(this).saveCity(city);

			category = intent.getStringExtra(CATEGORY) == null ? "hezu"
					: intent.getStringExtra(CATEGORY);
			ZufangConfig.getZufangConfig(this).saveCategory(category);

			final ProgressDialog dialog;
			dialog = new ProgressDialog(ZufangStandards.self);
			dialog.setTitle("租房信息查询");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);

			GetZufangInfoAsyncTask.Callback new_request_callback = new GetZufangInfoAsyncTask.Callback() {

				@Override
				public void callback(ArrayList<ZufangInfo> list) {
					dialog.dismiss();
					if (list == null) {
						MyLog.d(TAG, "THE LIST IS NULL!!!");
						Toast
								.makeText(
										ZufangService.this
												.getApplicationContext(),
										"Encounter a network probleam, please check your network configure!",
										Toast.LENGTH_LONG).show();
						return;
					}
					;
					// 保存最新的发布的租房信息的时间
					for (ZufangInfo zuInfo : list) {
						if (list.get(0).getPastTime() != null
								&& !list.get(0).getPastTime().equals("")) {
							ZufangConfig.getZufangConfig(ZufangService.this)
									.saveLatestUpdateTime(zuInfo.getPastTime(),
											System.currentTimeMillis());
							break;
						}
					}

					Intent intent = new Intent(ZufangService.this,
							ZufangTab.class);
					intent.putParcelableArrayListExtra(ZUFANG_INFO, list);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(CITY, city);
					intent.putExtra(KEYWORD, keyword);
					intent.putExtra(CATEGORY, category);
					ZufangService.this.startActivity(intent);
				}

			};

			if (intent.getStringExtra(KEYWORD) != null
					&& !intent.getStringExtra(KEYWORD).equals("")) {
				// start a progress
				final GetZufangInfoAsyncTask task = new GetZufangInfoAsyncTask(
						ZufangStandards.self, new_request_callback);
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
						ZufangStandards.self, new_request_callback);
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
		} else {// 请求来自自动更新

			// 查询租房信息是否有更新
			GetZufangInfoAsyncTask.Callback update_request_callback = new GetZufangInfoAsyncTask.Callback() {

				@Override
				public void callback(ArrayList<ZufangInfo> list) {
					// TODO check if there is update
					if (list != null && list.size() > 0) {
						// there is update
						notifyUserUpdateInfo(list);
						// update the laste_update_time
						for (ZufangInfo zuInfo : list) {
							if (list.get(0).getPastTime() != null && !list.get(0).getPastTime().equals("")) {
								MyLog.d(TAG, "zuInfo.getPastTime():"+ zuInfo.getPastTime());
								ZufangConfig.getZufangConfig(ZufangService.this).saveLatestUpdateTime(zuInfo.getPastTime(),System.currentTimeMillis());
								break;
							}
						}

						MyLog.d(TAG,"after update:" + ZufangConfig.getZufangConfig(ZufangService.this).getLatestUpdateTime().old_min_past_time);
					}
				}

			};

			keyword = ZufangConfig.getZufangConfig(this).getKeyword();
			city = ZufangConfig.getZufangConfig(this).getCity();
			category = ZufangConfig.getZufangConfig(this).getCategory();
			
			TimeConfig tc = ZufangConfig.getZufangConfig(this).getLatestUpdateTime();
			(new GetZufangInfoAsyncTask(ZufangStandards.self,
					update_request_callback)).execute(ZufangConfig.SERVER_URL
					+ "?keyword=" + keyword + "&city=" + city
					+ "&request_type=update_request&old_min_past_time="
					+ tc.old_min_past_time + "&saved_time=" + tc.savedTime
					+ "&category=" + category);

		}
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
}
