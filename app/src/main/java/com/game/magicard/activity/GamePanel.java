package com.game.magicard.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.magicard.R;
import com.game.magicard.data.StaticRes;

public class GamePanel extends Activity {

	private LinearLayout game1, game2, game3;
	private NfcAdapter adapter;
	private AudioManager aManger;
	private long exitTime = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel);
		checkNFC();
		if(!StaticRes.nfc_enable)
			startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
		initUI();
		initMedia();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		checkNFC();
		if(!StaticRes.nfc_enable)
			showToast(getResources().getString(R.string.not_enable));
	}
	@Override
	protected void onDestroy() {
		//if(StaticRes.mp != null)
		//StaticRes.mp.release();
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出游戏", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initUI() {
		game1 = (LinearLayout) this.findViewById(R.id.g1_group);
		game1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GamePanel.this, Game1.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.abc_fade_in,
						R.anim.abc_fade_out);
			}
		});

		game2 = (LinearLayout) this.findViewById(R.id.g2_group);
		game2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GamePanel.this, Game2.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.abc_fade_in,
						R.anim.abc_fade_out);
			}
		});

		game3 = (LinearLayout) this.findViewById(R.id.g3_group);
		game3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GamePanel.this, Game3.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.abc_fade_in,
						R.anim.abc_fade_out);
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
						Intent intent = new Intent(GamePanel.this, StgsActivity.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});

	}

	private void initMedia() {
		//aManger = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		//aManger.adjustStreamVolume(AudioManager.STREAM_MUSIC,
		//		AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		try {
			StaticRes.mp = MediaPlayer.create(GamePanel.this,R.raw.test);
			StaticRes.mp.setLooping(true);
			StaticRes.mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		StaticRes.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}
	private void checkNFC() {
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter == null) {
			showToast(getResources().getString(R.string.not_support));
			StaticRes.nfc_status = false;
			StaticRes.nfc_enable = false;
		} else if(!adapter.isEnabled()) {
			showToast(getResources().getString(R.string.not_enable));
			StaticRes.nfc_status = true;
			StaticRes.nfc_enable = true;
			if(adapter == null || !adapter.isEnabled()) {
				StaticRes.nfc_enable = false;
			}
		} else {
			StaticRes.nfc_status = true;
			StaticRes.nfc_enable = true;
		}
	}
	void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}


}
