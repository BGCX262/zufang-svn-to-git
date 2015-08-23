package com.tiangongkaiwu.db;

import java.util.ArrayList;

import com.tiangongkaiwu.kuaizu.ZufangInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 *
 */
public class Helper extends SQLiteOpenHelper {

	public static final String DB_NAME = "zufang.db";

	public static final String TALBE_NAME = "shoucang";

	public static final int VERSION = 1;
	/**
	 * sqliteϵͳĬ�ϵ�һ���������������������С�
	 */
	public static final String COLUMN_ID = "id";
	
	public static final String COLUMN_LONGDES = "longDes";

	public static final String COLUMN_SHORTDES = "shortDes";

	public static final String COLUMN_PASTTIME = "pastTime";

	public static final String COLUMN_PRICE = "price";

	public static final String COLUMN_DETAILLINK = "detailLink";

	public static final String COLUMN_TIME = "time";
	
	private SQLiteDatabase mDb;
	
	private static Helper sHelper = null;

	private Helper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	
	/**
	 *******************************************************************************
	 * ��֤helper���ǵ����ģ���֤���ݿ�ֻ������һ�Ρ�
	 *******************************************************************************
	 */
	public static Helper getHelper(Context context){
		if(sHelper == null){
			sHelper = new Helper(context);
		}
		return sHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TALBE_NAME + "( " + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_LONGDES
				+ " TEXT, " + COLUMN_SHORTDES + " TEXT, " + COLUMN_PASTTIME
				+ " TEXT, " + COLUMN_PRICE + " TEXT, " + COLUMN_DETAILLINK
				+ " TEXT, " + COLUMN_TIME + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TALBE_NAME);
		onCreate(db);
	}

	/**
	 *******************************************************************************
	 * ִ�д˲����Ƚ��������Բ�Ҫ��onCreate������ֱ�ӵ��� 
	 *******************************************************************************
	 */
	public void openDatabase(){
		mDb = this.getWritableDatabase();
	}
	
	/**
	 *******************************************************************************
	 * ����ղ���Ϣ�����ݿ�
	 *******************************************************************************
	 */
	public void addData(ZufangInfo info) {
    	String longDes = info.getLongDes();
    	String shortDes = info.getShortDes();
    	String pastTime = info.getPastTime();
    	String price = info.getPrice();
    	String detailLink = info.getDetailLink();
    	ContentValues values = new ContentValues();
    	values.put(Helper.COLUMN_DETAILLINK, detailLink);
    	values.put(Helper.COLUMN_LONGDES, longDes);
    	values.put(Helper.COLUMN_PASTTIME, pastTime);
    	values.put(Helper.COLUMN_PRICE, price);
    	values.put(Helper.COLUMN_SHORTDES, shortDes);
    	values.put(Helper.COLUMN_TIME, System.currentTimeMillis());
    	mDb.insert(Helper.TALBE_NAME, null, values);
    	
    }
	
	/**
	 *******************************************************************************
	 * ��ȡ���е��ղ���Ϣ
	 *******************************************************************************
	 */
	 public void getAllData(ArrayList<ZufangInfo> info) {
	    	Cursor cur;
	    	cur = mDb.query(Helper.TALBE_NAME, null, null, null, null, null, Helper.COLUMN_TIME + " asc");
	    	if (cur.moveToFirst()) {
	    		do {
	    			ZufangInfo z = new ZufangInfo();
	    			z.setID(String.valueOf(cur.getInt(cur.getColumnIndex(Helper.COLUMN_ID))));
	    			z.setDetailLink(cur.getString(cur.getColumnIndex(Helper.COLUMN_DETAILLINK)));
	    			z.setLongDes(cur.getString(cur.getColumnIndex(Helper.COLUMN_LONGDES)));
	    			z.setPastTime(cur.getString(cur.getColumnIndex(Helper.COLUMN_PASTTIME)));
	    			z.setPrice(cur.getString(cur.getColumnIndex(Helper.COLUMN_PRICE)));
	    			z.setShortDes(cur.getString(cur.getColumnIndex(Helper.COLUMN_SHORTDES)));
	    			info.add(z);
	    		} while (cur.moveToNext());
	    	}
	    	cur.close();
	  }
	 
	 /**
	  *******************************************************************************
	  * ɾ��һ�����߶����ղ���Ϣ
	  *******************************************************************************
	  */
	 public void delShouCang(String[] id){
		 String whereClause = COLUMN_ID+"=?";
		 mDb.delete(Helper.TALBE_NAME, whereClause, id);
	 }
	 
 	/**
	 *******************************************************************************
	 * �ر����ݿ�����
	 *******************************************************************************
	 */
	 public void close(){
		 if(mDb != null && mDb.isOpen()){
			 mDb.close();
		 }
	 }
}
