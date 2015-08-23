package com.tiangongkaiwu.kuaizu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adview.AdViewLayout;
import com.mobclick.android.MobclickAgent;
import com.tiangongkaiwu.db.Helper;

public class ShoucangActivity extends Activity {
	private static final String TAG = "ShoucangActivity";
    SCAdapter mAdapter;
    ArrayList<ZufangInfo> info;
    public static final String STARTED_FROM_MAIN_PAGE = "started_from_main_page";
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.zufanglist);
			
			if(getIntent().getBooleanExtra(STARTED_FROM_MAIN_PAGE, false)){
	        	LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);
	            AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20112125290803uufpszc6vhdrngj");
	            RelativeLayout.LayoutParams adViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	            layout.addView(adViewLayout, adViewLayoutParams);
	            layout.invalidate();
	        }
			
		    info = new ArrayList<ZufangInfo>();
	        
	        Helper.getHelper(this).openDatabase();
	        Helper.getHelper(this).getAllData(info);
	        Helper.getHelper(this).close();
	        if(info.size() == 0){
	        	findViewById(R.id.no_info).setVisibility(View.VISIBLE);
	        }else{
	        	findViewById(R.id.no_info).setVisibility(View.GONE);
	        }
	        mAdapter = new SCAdapter(this, info);
	        ListView lv = ((ListView)this.findViewById(R.id.zufangList));
	        lv.setAdapter(mAdapter);
	        lv.setCacheColorHint(0);
	        lv.setDividerHeight(5);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		
		/** 刷新收藏列表*/
		info.clear();
		Helper.getHelper(this).openDatabase();
        Helper.getHelper(this).getAllData(info);
        Helper.getHelper(this).close();
        
		mAdapter.notifyDataSetInvalidated();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	
	@Override
    protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this); 
    }
	
	
}

class SCAdapter extends BaseAdapter {
    
    Context mContext;
    ArrayList<ZufangInfo> info;
    
    public SCAdapter(Context con, ArrayList<ZufangInfo> in) {
        mContext = con;
        info = in;
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int position) {
        return info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    class ViewHolder {
        TextView longDescView;
        TextView shortDescView;
        TextView priceView;
        ImageView openDetail;
        TextView pastTimeView;
        ImageView cancelShoucangBtn;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem, null);

            viewHolder = new ViewHolder();
            viewHolder.longDescView = (TextView) convertView
                    .findViewById(R.id.longDesc);
            viewHolder.shortDescView = (TextView) convertView
                    .findViewById(R.id.shortDesc);
            viewHolder.priceView = (TextView) convertView
                    .findViewById(R.id.price);
            viewHolder.openDetail = (ImageView) convertView
                    .findViewById(R.id.open_detail);
            viewHolder.pastTimeView = (TextView) convertView
                    .findViewById(R.id.pastTime);

            viewHolder.cancelShoucangBtn = (ImageView) convertView
                    .findViewById(R.id.add_favorite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.longDescView
                .setText(info.get(position).getLongDes());
        viewHolder.shortDescView.setText(info.get(position)
                .getShortDes());
        viewHolder.priceView.setText(info.get(position).getPrice());

        viewHolder.pastTimeView.setText("发布时间："+info
                .get(position).getPastTime());
        viewHolder.openDetail.setTag(
                info.get(position).getDetailLink());

        // 点击查看详细，直接定位到原始页面
        viewHolder.openDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent openLink = new Intent();
                openLink.setAction(Intent.ACTION_VIEW);
                openLink.setData(Uri.parse(arg0.getTag().toString()));
                openLink.addCategory(Intent.CATEGORY_BROWSABLE);
                mContext.startActivity(openLink);
            }

        });
        
        viewHolder.cancelShoucangBtn.setImageResource(R.drawable.delete);
        viewHolder.cancelShoucangBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	
            	// prompt the user to delete this
				// checklist.
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("确认删除？");
				builder.setPositiveButton(
						"删除",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int whichButton) {
								/**
				            	 * 删除指定的收藏。
				            	 */
				            	Helper.getHelper(mContext).openDatabase();
				    	        Helper.getHelper(mContext).delShouCang(new String[]{info.get(position).getID()});
				    	        Helper.getHelper(mContext).close();
				    	        
				    	        /**
				    	         * 刷新收藏列表。
				    	         */
				    	        info.clear();
				    			Helper.getHelper(mContext).openDatabase();
				    	        Helper.getHelper(mContext).getAllData(info);
				    	        Helper.getHelper(mContext).close();
				    	        SCAdapter.this.notifyDataSetChanged();
							}
						});

				builder.setNegativeButton(
						"取消",
						null);
				builder.create();
				builder.show();
            	
            }
        });
        
        return convertView;
    }
}
