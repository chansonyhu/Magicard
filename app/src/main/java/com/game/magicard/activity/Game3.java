package com.game.magicard.activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.magicard.data.StaticRes;
import com.yalantis.guillotine.sample.animation.GuillotineAnimation;
import com.game.magicard.R;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * Created by 千山 on 2015/9/7.
 */
public class Game3 extends AppCompatActivity {
	private static final long RIPPLE_DURATION = 250;
	private GuillotineAnimation gagb;
	private long exitTime = 0;
	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	@InjectView(R.id.root)
	FrameLayout root;
	@InjectView(R.id.content_hamburger)
	View contentHamburger;
	private Button write,help,tel;
	private AlertDialog.Builder builder, tel_builder;
	boolean hasWrite = false;
	boolean nowRead = false;
	private Dialog writeDialog,readDialog;
	private String telnum;
	private EditText et_telnum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		//
		switchy();
	}
	private void initUI() {
		setContentView(R.layout.activity_g3);
		ButterKnife.inject(this);

		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setTitle(null);
		}

		View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_g3, null);
		root.addView(guillotineMenu);

		gagb = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
				.setStartDelay(RIPPLE_DURATION)
				.setActionBarViewForAnimation(toolbar)
				.setClosedOnStart(true)
				.build();
		help = (Button)this.findViewById(R.id.bt_help);
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder = new AlertDialog.Builder(Game3.this);
				builder.setTitle("帮助");
				builder.setMessage(getResources().getString(R.string.help_3));
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});

		write = (Button)this.findViewById(R.id.bt_write);
		write.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��Ҫ��д�µ�����
				hasWrite = false;
				if (StaticRes.nfc_status && StaticRes.nfc_enable) {
					tel_builder = new AlertDialog.Builder(Game3.this)
							.setTitle("请输入要写入的电话号码")
							.setIcon(R.drawable.ic_game3)
							.setNegativeButton("取消", null)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									nowRead = true;
									hasWrite = true;
									//telnum = et_telnum.toString();
									telnum = "10010";
									String temp = "已经将电话号码 " + telnum + " 写入卡片。";
									new AlertDialog.Builder(Game3.this)
											.setTitle("提示")
											.setMessage(temp)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// TODO Auto-generated method stub
													new AlertDialog.Builder(Game3.this)
															.setTitle("是否继续写入电话号码？")
															.setNegativeButton("否", null)
															.setPositiveButton("是", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	// TODO Auto-generated method stub
																	dialog.dismiss();
																}
															})
															.show();
												}
											}).show();

									//Toast.makeText(Game3.this, temp, Toast.LENGTH_LONG).show();
									//dialog.dismiss();
								}
							});
					et_telnum = new EditText(Game3.this);
					tel_builder.setView(et_telnum).show();
				} else if (!StaticRes.nfc_status) {
					showToast(getResources().getString(R.string.not_support));
				} else if (!StaticRes.nfc_enable) {
					showToast(getResources().getString(R.string.not_enable));
				}

			}
		});

		tel = (Button)this.findViewById(R.id.bt_tel);
		tel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (StaticRes.nfc_status && StaticRes.nfc_enable) {
					builder = new AlertDialog.Builder(Game3.this);
					//if(!hasWrite) {
					builder.setTitle("提示：")
							.setMessage("请靠近卡片来拨号")
							.setNegativeButton("取消", null)
							.setCancelable(false)
							.create().show();
					//}
				} else if (!StaticRes.nfc_status) {
					showToast(getResources().getString(R.string.not_support));
				} else if (!StaticRes.nfc_enable) {
					showToast(getResources().getString(R.string.not_enable));
				}
			}
		});



	}
	private void switchy() {
		LinearLayout g1 = (LinearLayout) findViewById(R.id.g1_group);

		g1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Game3.this, Game1.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});

		LinearLayout g2 = (LinearLayout) findViewById(R.id.g2_group);
		g2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Game3.this, Game2.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});

		LinearLayout stgs = (LinearLayout) findViewById(R.id.settings_group);
		stgs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Game3.this, StgsActivity.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});
	}

	//当卡片贴近的时候，处理Intent的方法
	private void dealIntent(Intent intent)
	{
		//如果还没有写入卡片则写入，否则读取卡片
		if(hasWrite == false)
		{
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage ndefMessage = getNoteAsNdef(telnum);
			if (ndefMessage != null)
			{
				writeCard(ndefMessage, tag);
			}
			else
			{
				showToast("已经完成写入");
			}
		}
		else if(nowRead == true)
		{
			readTag(intent);
		}
	}
	boolean writeCard(NdefMessage message, Tag tag) {

		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					showToast("tag不允许写入");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					showToast("文件大小超出容量");
					return false;
				}
				ndef.writeNdefMessage(message);
				showToast("写入数据成功");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						showToast("格式化tag并且写入message");
						return true;
					} catch (IOException e) {
						showToast("格式化tag失败");
						return false;
					}
				} else {
					showToast("Tag不支持NDEF");
					return false;
				}
			}
		} catch (Exception e) {
			showToast("写入数据失败");
		}

		return false;
	}
	private NdefMessage getNoteAsNdef(String text) {
		byte[] textBytes = text.getBytes();
		// image/jpeg text/plain
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"image/jpeg".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}
	private void readTag(Intent intent) {

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (gagb.isClosing && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			gagb.open();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		//if(StaticRes.mp != null)
			//StaticRes.mp.release();
		super.onDestroy();
	}
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
