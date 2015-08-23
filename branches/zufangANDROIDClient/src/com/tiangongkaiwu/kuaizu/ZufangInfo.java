package com.tiangongkaiwu.kuaizu;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class ZufangInfo implements Parcelable{
	
	private Bundle mBundle = new Bundle();
	
	/**
	 *******************************************************************************
	 * 租房信息在数据库里的唯一ID，用来管理收藏信息。
	 *******************************************************************************
	 */
	public String getID() {
		return mBundle.getString("ID");
	}
	public void setID(String ID) {
		mBundle.putString("ID", ID);
	}
	
	/**
	 *******************************************************************************
	 * 本租房信息时候已经被收藏。
	 *******************************************************************************
	 */
	public boolean getShoucangStatus() {
		return mBundle.getBoolean("shoucang");
	}
	public void setShoucangStatus(boolean status) {
		mBundle.putBoolean("shoucang", status);
	}
	
	public String getPastTime() {
		return mBundle.getString("pastTime");
	}
	public void setPastTime(String pastTime) {
		mBundle.putString("pastTime", pastTime);
	}
	public String getLongDes() {
		return mBundle.getString("longDes");
	}
	public void setLongDes(String longDes) {
		mBundle.putString("longDes", longDes);
	}
	public String getPrice() {
		return mBundle.getString("price");
	}
	public void setPrice(String price) {
		mBundle.putString("price", price);
	}
	public String getShortDes() {
		return mBundle.getString("shortDes");
	}
	public void setShortDes(String shortDes) {
		mBundle.putString("shortDes", shortDes);
	}
	public String getDetailLink() {
		return mBundle.getString("detailLink");
	}
	public void setDetailLink(String detailLink) {
		mBundle.putString("detailLink", detailLink);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeBundle(this.mBundle);
	}
	
	public static final Parcelable.Creator<ZufangInfo> CREATOR = new Parcelable.Creator<ZufangInfo>(){

		@Override
		public ZufangInfo createFromParcel(Parcel source) {
			ZufangInfo info = new ZufangInfo();
			info.mBundle = source.readBundle();
			return info;
		}

		@Override
		public ZufangInfo[] newArray(int size) {
			return new ZufangInfo[size];
		}
		
	};
}
