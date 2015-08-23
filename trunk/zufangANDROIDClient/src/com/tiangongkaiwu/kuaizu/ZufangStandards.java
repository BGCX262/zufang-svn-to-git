
package com.tiangongkaiwu.kuaizu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mobclick.android.MobclickAgent;
import com.tiangongkaiwu.utility.City;

public class ZufangStandards extends Activity {

    public static ZufangStandards self;
    EditText keywordEdit;
    int city = -1;
    private String mCategory;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);
        MobclickAgent.update(this);
        setContentView(R.layout.main);
        this.setTitle(getString(R.string.input_zufanginfo_hint));
        keywordEdit = (EditText) findViewById(R.id.et_keyword);
        // ���ûس���ȷ��
        keywordEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    submit();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        //ѡ�ڳ���
        final RadioGroup rg = (RadioGroup) findViewById(R.id.group);
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                if (rb != null) {
                    ((TextView) findViewById(R.id.curr_city)).setText(getString(R.string.selected_city) + rb.getText());
                    if (rb.getText().toString().contains("����")) {
                        city = 2;
                    } else if (rb.getText().toString().contains("�ɶ�")) {
                        city = 0;
                    }
                    if (rb.getText().toString().contains("����")) {
                        city = 1;
                    } else if (rb.getText().toString().contains("�Ϻ�")) {
                        city = 3;
                    }
                }
            }
        });
        
        Button moreBtn = (Button) findViewById(R.id.more);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            int whichSelected;
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
                b.setTitle(getString(R.string.select_city));
                b.setSingleChoiceItems(City.CITY, -1,
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(
                                                   DialogInterface dialog,
                                                   int which) {
                                               whichSelected = which;
                                           }
                                       });
                b.setNegativeButton(getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                        }
                                    });
                b.setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            rg.clearCheck();
                                            ((TextView) findViewById(R.id.curr_city))
                                                    .setText(getString(R.string.selected_city)
                                                            + City.CITY[whichSelected]);
                                            city = whichSelected;
                                        }
                                    });
                b.create().show();
            }
        });
        rg.clearCheck();
        
        //ѡ���ⷿ����
        RadioGroup category_group = (RadioGroup) this.findViewById(R.id.category_group);
        category_group.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId){
					case R.id.hezu:
						mCategory = "hezu";
						break;
					case R.id.zhengzu:
						mCategory = "zhengzu";
						break;
					default:
						mCategory = "hezu";
				}
			}
        });

        //�����ѹ���ҳ��
        findViewById(R.id.manage_reminder).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ZufangStandards.this,SubManageAcitivty.class);
				ZufangStandards.this.startActivity(intent);
			}
        	
        });
        
        //���ղ�ҳ��
        findViewById(R.id.view_stored_info).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),ShoucangActivity.class);
				intent.putExtra(ShoucangActivity.STARTED_FROM_MAIN_PAGE, true);
				v.getContext().startActivity(intent);
			}
        });
        
        //�򿪷���ҳ��
        findViewById(R.id.feedback).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MobclickAgent.openFeedbackActivity(ZufangStandards.this);
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

    protected void submit() {
        if (((TextView) findViewById(R.id.curr_city)).getText().toString().contains("δ")) {
            Toast.makeText(this, getString(R.string.select_city_tips), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, ZufangService.class);
        String keyword = keywordEdit.getEditableText().toString();
        intent.putExtra(ZufangService.KEYWORD, keyword.trim());
        intent.putExtra(ZufangService.CATEGORY, mCategory);
        intent.putExtra(ZufangService.CITY, city);
        if (((CheckBox) findViewById(R.id.update_notify_checkbox)).isChecked()) {
            intent.putExtra(ZufangService.NEED_UPDATE, true);
            //���͸����ˣ�ͳ���Զ����µ�ʹ���ʡ�
            MobclickAgent.onEvent(this, "check_automatic_update");
        }
        this.startService(intent);
       
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.setClass(this, ZufangService.class);
        this.stopService(intent);
        super.onDestroy();
    }
}
