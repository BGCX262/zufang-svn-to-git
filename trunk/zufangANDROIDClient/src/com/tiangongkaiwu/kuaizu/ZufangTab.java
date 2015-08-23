package com.tiangongkaiwu.kuaizu;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;

import com.mobclick.android.MobclickAgent;

public class ZufangTab extends TabActivity {
	protected static final String TAG = "ZufangTab";
	TabHost host;
	//private ArrayList<ZufangInfo> mZhengzuList = new ArrayList<ZufangInfo>();

	@Override
	public void onCreate(Bundle saveInstance) {
		super.onCreate(saveInstance);
		setContentView(R.layout.zufang_tab);
		host = getTabHost();
		// 合租
		Intent hezu_intent = new Intent();
		hezu_intent.setClass(host.getContext(), ZufangListActivity.class);
		hezu_intent.putExtra(ZufangService.KEYWORD, getIntent().getStringExtra(ZufangService.KEYWORD).trim());
		hezu_intent.putExtra(ZufangService.CITY, getIntent().getIntExtra(ZufangService.CITY, -1));
		if(getIntent().getStringExtra(ZufangService.CATEGORY).equals("hezu")){
			hezu_intent.putParcelableArrayListExtra(ZufangService.ZUFANG_INFO, getIntent().getParcelableArrayListExtra(ZufangService.ZUFANG_INFO));
		}
		hezu_intent.putExtra(ZufangService.CATEGORY, "hezu");
		hezu_intent.putExtra(ZufangService.NEED_UPDATE, getIntent().getStringExtra(ZufangService.NEED_UPDATE));
        
		RelativeLayout hezuWidget = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_widget_item, null);
		hezuWidget.setBackgroundResource(R.drawable.tab_background);
//		hezuWidget.findViewById(R.id.icon).setBackgroundResource(R.drawable.hezu);
		((TextView) hezuWidget.findViewById(R.id.title)).setText("合租");
		TabHost.TabSpec hezuSpec = host.newTabSpec("hezu").setIndicator(hezuWidget).setContent(hezu_intent);
		host.addTab(hezuSpec);

		// 整租
		Intent zhengzu = new Intent();
		if(getIntent().getStringExtra(ZufangService.CATEGORY).equals("zhengzu")){
			zhengzu.putParcelableArrayListExtra(ZufangService.ZUFANG_INFO, getIntent().getParcelableArrayListExtra(ZufangService.ZUFANG_INFO));
		}
		zhengzu.putExtra(ZufangService.KEYWORD, getIntent().getStringExtra(ZufangService.KEYWORD).trim());
		zhengzu.putExtra(ZufangService.CITY, getIntent().getIntExtra(ZufangService.CITY, -1));
		zhengzu.putExtra(ZufangService.NEED_UPDATE, getIntent().getStringExtra(ZufangService.NEED_UPDATE));
		zhengzu.putExtra(ZufangService.CATEGORY, "zhengzu");
		zhengzu.setClass(host.getContext(), ZhengzuListActivity.class);
		
		RelativeLayout zhengzuWidget = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_widget_item, null);
		zhengzuWidget.setBackgroundResource(R.drawable.tab_background);
//		zhengzuWidget.findViewById(R.id.icon).setBackgroundResource(
//				R.drawable.zhengzu);
		((TextView) zhengzuWidget.findViewById(R.id.title)).setText("整租");
		TabHost.TabSpec zhengzuSpec = host.newTabSpec("zhengzu").setIndicator(zhengzuWidget).setContent(zhengzu);
		host.addTab(zhengzuSpec);


		// 收藏
		Intent shoucang = new Intent();
		shoucang.setClass(host.getContext(), ShoucangActivity.class);
		RelativeLayout shoucangWidget = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_widget_item, null);
		shoucangWidget.setBackgroundResource(R.drawable.tab_background);
//		shoucangWidget.findViewById(R.id.icon).setBackgroundResource(
//				R.drawable.star);
		((TextView) shoucangWidget.findViewById(R.id.title)).setText("收藏");
		TabHost.TabSpec shoucangSpec = host.newTabSpec("shoucang").setIndicator(shoucangWidget).setContent(shoucang);
		host.addTab(shoucangSpec);
		
		if(getIntent().getStringExtra(ZufangService.CATEGORY).equals("hezu")){
			host.setCurrentTab(0);
			host.getTabWidget().getChildAt(0).setPressed(true);
		}else{
			host.setCurrentTab(1);
			host.getTabWidget().getChildAt(1).setPressed(true);
		}
		
		host.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals("hezu")){
					if(host.getTabContentView()==null){
						
					}
				}
				else
				if(tabId.equals("zhengzu")){
					
				}
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
}
