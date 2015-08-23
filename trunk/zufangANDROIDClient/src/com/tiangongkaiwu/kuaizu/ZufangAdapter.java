package com.tiangongkaiwu.kuaizu;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiangongkaiwu.db.Helper;

class ZufangAdapter extends ArrayAdapter<ZufangInfo> {
	private List<ZufangInfo> infoList;
	private LayoutInflater inflater;
	private Context context;

	public ZufangAdapter(Context context, int textViewResourceId,
			List<ZufangInfo> objects) {
		super(context, textViewResourceId, objects);
		infoList = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = (LinearLayout) inflater.inflate(R.layout.listitem,
					null);

			viewHolder = new ViewHolder();
			viewHolder.longDescView = (TextView) convertView
					.findViewById(R.id.longDesc);
			viewHolder.shortDescView = (TextView) convertView
					.findViewById(R.id.shortDesc);
			viewHolder.priceView = (TextView) convertView
					.findViewById(R.id.price);
			viewHolder.checkDetail = (ImageView) convertView
					.findViewById(R.id.open_detail);
			viewHolder.pastTimeView = (TextView) convertView
					.findViewById(R.id.pastTime);

			viewHolder.addFavorite = (ImageView) convertView
					.findViewById(R.id.add_favorite);

			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.longDescView.setText(infoList.get(position).getLongDes());
		viewHolder.shortDescView.setText(infoList.get(position).getShortDes());
		viewHolder.priceView.setText(infoList.get(position).getPrice());

		viewHolder.pastTimeView.setText("����ʱ�䣺"
				+ infoList.get(position).getPastTime());
		viewHolder.checkDetail.setTag(infoList.get(position).getDetailLink());

		// ����鿴��ϸ��ֱ�Ӷ�λ��ԭʼҳ��
		viewHolder.checkDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent openLink = new Intent();
				openLink.setAction(Intent.ACTION_VIEW);
				openLink.setData(Uri.parse(arg0.getTag().toString()));
				openLink.addCategory(Intent.CATEGORY_BROWSABLE);
				context.startActivity(openLink);
			}

		});

		/**
		 * �ղ�״̬���ơ�
		 */
		if (infoList.get(position).getShoucangStatus()) {
			viewHolder.addFavorite.setImageResource(R.drawable.star_on);
		} else {
			viewHolder.addFavorite.setImageResource(R.drawable.star_off);
		}

		// �ղش�����Ϣ
		viewHolder.addFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Helper.getHelper(context).openDatabase();
				Helper.getHelper(context).addData(infoList.get(position));
				Helper.getHelper(context).close();

				/**
				 * �ı�״̬�����ղأ������ﲻ��ȡ���ղأ��赽�ղع�����ȡ���ղء�
				 */
				infoList.get(position).setShoucangStatus(true);
				((ImageView) arg0).setImageResource(R.drawable.star_on);
			}

		});

		return convertView;
	}

}

class ViewHolder {
	TextView longDescView;
	TextView shortDescView;
	TextView priceView;
	ImageView checkDetail;
	TextView pastTimeView;
	ImageView addFavorite;
}
