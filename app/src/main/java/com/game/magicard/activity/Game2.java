package com.game.magicard.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.magicard.data.*;
import com.yalantis.guillotine.sample.animation.GuillotineAnimation;
import com.game.magicard.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import butterknife.ButterKnife;
import butterknife.InjectView;
public class Game2 extends AppCompatActivity {
	private static final long RIPPLE_DURATION = 250;
	private GuillotineAnimation gagb;
	private long exitTime = 0;
	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	@InjectView(R.id.root)
	FrameLayout root;
	@InjectView(R.id.content_hamburger)
	View contentHamburger;

	private Button write;
	private Button help;
	private Button choose;
	private AlertDialog.Builder builder;
	private Dialog writeDialog;
	private Dialog readDialog;

	InitQueue mInitQueue;

	//可以根据需要改进为使用intent传递，先这么写
	private NfcAdapter adapter;

	private IntentFilter[] mWriteTagFilters;
	private IntentFilter[] mReadFilters;
	private PendingIntent pendingIntent;
	private String[][] mTechLists;

	Queue<String> queue;

	//private PendingIntent pendingIntent;
	//private IntentFilter[] mFilters;
	//private String[][] mTechLists;

	String num;
	//int [] num_queue = new int[10];
	int first_num = 0;
	int second_num = 0;
	int readCount = 0;
	boolean hasWrite = false;
	boolean nowRead = false;
	boolean isQueueInit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		initUI();
		if(StaticRes.nfc_status && StaticRes.nfc_enable) {
			adapter = NfcAdapter.getDefaultAdapter(Game2.this);
			queue = new LinkedList<String>();

			int[] num = {10,4,2,1,5,7,3,8,9,6};
			for(int i=0;i<10;i++)
			{
				queue.add(num[i] + "");
			}

			pendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(Game2.this,getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			// 写入标签权限
			IntentFilter writeFilter = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED);
			mWriteTagFilters = new IntentFilter[] { writeFilter };
			mTechLists = new String[][]
					{
							new String[] { MifareClassic.class.getName() },
							new String[] { NfcA.class.getName() }
					};// 允许扫描的标签类型

			IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
			ndef.addCategory("*/*");
			mReadFilters = new IntentFilter[] { ndef };// 过滤器
		}
		switchy();
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
						Intent intent = new Intent(Game2.this, Game1.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});

		LinearLayout g3 = (LinearLayout) findViewById(R.id.g3_group);
		g3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Game2.this, Game3.class);
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
						Intent intent = new Intent(Game2.this, StgsActivity.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.abc_fade_in,
								R.anim.abc_fade_out);
					}
				}, 0);
			}
		});
	}
	@Override
	public void onResume() 
	{
		super.onResume();
		if(StaticRes.nfc_status && StaticRes.nfc_enable) {
			if(!hasWrite){
				adapter.enableForegroundDispatch(this, pendingIntent,mWriteTagFilters, mTechLists);
			}
			else{
				adapter.enableForegroundDispatch(this, pendingIntent, mReadFilters, mTechLists);
			}
		}
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
	public void onDestroy()
	{
		super.onDestroy();
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))
		{
			dealIntent(intent);
		}
	}
	//当卡片贴近的时候，处理Intent的方法
	private void dealIntent(Intent intent)
	{
		//如果还没有写入卡片则写入，否则读取卡片
		if(hasWrite == false)
		{
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage ndefMessage = getNoteAsNdef();
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

	class InitQueue extends Thread
	{
		public Handler mHandler;

		@Override
		public void run()
		{
			Looper.prepare();
			mHandler = new Handler()
			{

				@Override
				public void handleMessage(Message msg)
				{
					if(msg.what == Data.initQueue)
					{
						int[] num = {10,4,2,1,5,7,3,8,9,6};
						for(int i=0;i<10;i++)
						{
							queue.add(num[i] + "");
						}
					}
				}
			};
			Looper.loop();
		}
	}

	private void initQueue()
	{
		Message msg = new Message();
		msg.what = Data.initQueue;
		mInitQueue.mHandler.sendMessage(msg);
	}
	private void initUI() 
	{
		setContentView(R.layout.activity_g2);

		ButterKnife.inject(this);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setTitle(null);
		}

		View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_g2, null);
		root.addView(guillotineMenu);
		gagb = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
				.setStartDelay(RIPPLE_DURATION)
				.setActionBarViewForAnimation(toolbar)
				.setClosedOnStart(true)
				.build();
		help = (Button)this.findViewById(R.id.bt_help);
		help.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				builder = new AlertDialog.Builder(Game2.this);
				builder.setTitle("帮助");
				builder.setMessage(getResources().getString(R.string.help_2));
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		});

		write = (Button)this.findViewById(R.id.bt_write);
		write.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				//需要加写新的内容
				hasWrite = false;
				if(StaticRes.nfc_status && StaticRes.nfc_enable) {
					builder = new AlertDialog.Builder(Game2.this);
					builder.setTitle("请将卡片靠近完成写入");
					builder.setCancelable(false);
					builder.setNegativeButton("取消", null);
					writeDialog = builder.create();
					writeDialog.show();
				} else if(!StaticRes.nfc_status) {
					showToast(getResources().getString(R.string.not_support));
				} else if(!StaticRes.nfc_enable){
					showToast(getResources().getString(R.string.not_enable));
				}

			}
		});

		choose = (Button)this.findViewById(R.id.bt_mode);
		choose.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(StaticRes.nfc_status && StaticRes.nfc_enable) {
					builder = new AlertDialog.Builder(Game2.this);
					if(!hasWrite)
					{
						builder.setTitle("提示：");
						builder.setMessage("请完成卡片制作");
						builder.setNegativeButton("已经有了卡牌",
								new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// TODO Auto-generated method stub
										nowRead = true;
										hasWrite = true;
										dialog.dismiss();
										builder = new AlertDialog.Builder(Game2.this);
										builder.setTitle("请选择游戏模式");
										builder.setNegativeButton("人机对战",
												new DialogInterface.OnClickListener()
												{

													@Override
													public void onClick(DialogInterface dialog, int which)
													{
														// TODO Auto-generated method stub
														dialog.dismiss();
													}
												});

										builder.setNeutralButton("与人对战",
												new DialogInterface.OnClickListener()
												{

													@Override
													public void onClick(DialogInterface dialog, int which)
													{
														// TODO Auto-generated method stub
														dialog.dismiss();
													}
												});

										builder.setPositiveButton("取消",
												new DialogInterface.OnClickListener()
												{

													@Override
													public void onClick(DialogInterface dialog, int which)
													{
														// TODO Auto-generated method stub
														dialog.dismiss();
													}
												});
										builder.create().show();

									}
								});
						builder.setPositiveButton("取消",
								new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
						builder.create().show();
					}
					else
					{
						nowRead = true;
						builder.setTitle("请选择游戏模式");
						builder.setNegativeButton("人机对战",
								new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});

						builder.setNeutralButton("与人对战",
								new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});

						builder.setPositiveButton("取消",
								new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
						builder.create().show();
					}
				} else if(!StaticRes.nfc_status) {
					showToast(getResources().getString(R.string.not_support));
				} else if(!StaticRes.nfc_enable){
					showToast(getResources().getString(R.string.not_enable));
				}

			}
		});
	}


	// 根据文本生成一个NdefRecord
	private NdefMessage getNoteAsNdef()
	{
		String text = queue.peek();
		//showToast("Queue size now = "+queue.size());
		String appendix = Data.game1CardNumber - queue.size() + "";
		if (text.equals(""))
		{
			return null;
		}
		else
		{
			text = appendix + ":" + text;
			byte[] textBytes = text.getBytes();
			// image/jpeg text/plain
			NdefRecord textRecord = new NdefRecord(
					NdefRecord.TNF_MIME_MEDIA,
					"image/jpeg".getBytes(), new byte[] {}, textBytes);
			return new NdefMessage(new NdefRecord[] { textRecord });
		}
	}

	// 写入tag的函数
	boolean writeCard(NdefMessage message, Tag tag)
	{

		int size = message.toByteArray().length;

		try
		{
			Ndef ndef = Ndef.get(tag);
			if (ndef != null)
			{
				ndef.connect();

				if (!ndef.isWritable())
				{
					showToast("tag不允许写入");
					return false;
				}
				if (ndef.getMaxSize() < size)
				{
					showToast("文件大小超出容量");
					return false;
				}

				ndef.writeNdefMessage(message);
				queue.remove();
				showToast("写入数据成功");
				if(queue.size() == 0)
				{
					showToast("全部数据写入成功");
					hasWrite = true;
					initQueue();
					writeDialog.dismiss();
					//需要加入
				}
				return true;
			}
			else
			{
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null)
				{
					try
					{
						format.connect();
						format.format(message);
						showToast("格式化tag并且写入message");
						return true;
					}
					catch (IOException e)
					{
						showToast("格式化tag失败");
						return false;
					}
				}
				else
				{
					showToast("Tag不支持NDEF");
					return false;
				}
			}
		}
		catch (Exception e)
		{
			showToast("写入数据失败");
		}

		return false;
	}

	//读取卡片并且返回卡片读取的内容
	private String processIntent(Intent intent)
	{
		Parcelable[] rawMsgs = intent.
				getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		NdefRecord[] records = msg.getRecords();
		String resultStr = new String(records[0].getPayload());
		//showToast(resultStr);
		return resultStr;
	}

	//整个函数用于读取卡片的内容
	private void readTag(Intent intent)
	{

		switch (readCount)
		{
			//读取前面9张卡片的情形
			case 0 :
			{
				num = processIntent(intent);
				//str[0]为id,str[1]为我们所需要的数值
				String[] str = num.split(":");
				num = str[1];
				int temp = 0;
				try
				{
					temp = Integer.parseInt(num);
					second_num = temp;
				}
				catch(Exception e)
				{
					showToast(e.getMessage());
					showToast("卡片错误，请重新制作");
				}
				this.compareAndShow();
				showToast("请贴近下一张卡片");
				readCount++;
				break;
			}

			case 9 :
			{
				num = processIntent(intent);
				//str[0]为id,str[1]为我们所需要的数值
				String[] str = num.split(":");
				num = str[1];
				int temp = 0;
				try
				{
					temp = Integer.parseInt(num);
					first_num = second_num;
					second_num = temp;
				}
				catch(Exception e)
				{
					showToast(e.getMessage());
					showToast("卡片错误，请重新制作");
				}
				this.compareAndShow();
				showToast("游戏结束");
				readCount = 0;
				break;
			}
			//读取最后一张卡片的情形
			default :
			{

				num = processIntent(intent);
				//str[0]为id,str[1]为我们所需要的数值
				String[] str = num.split(":");
				num = str[1];
				int temp = 0;
				try
				{
					temp = Integer.parseInt(num);
					first_num = second_num;
					second_num = temp;
				}
				catch(Exception e)
				{
					showToast(e.getMessage());
					showToast("卡片错误，请重新制作");
				}

				readCount = 0;
				if(readDialog != null)
				{
					readDialog.dismiss();
				}
				compareAndShow();
				break;
			}
		}
	}


	private void compareAndShow()
	{
		if(first_num < second_num)
		{
			showToast("↑");
		}
		else if(first_num > second_num)
		{
			showToast("↓");
		}
		else
		{
			showToast("→");
		}
	}

	//为了使得代码简洁，引入了showToast函数
	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT)
				.show();
	}
}