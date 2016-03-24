package com.game.magicard.activity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

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
import com.game.magicard.ai.game1AI;
import com.yalantis.guillotine.sample.animation.GuillotineAnimation;
import com.game.magicard.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Game1 extends AppCompatActivity {
	private static final long RIPPLE_DURATION = 250;
	private GuillotineAnimation gagb;
	private long exitTime = 0;
	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	@InjectView(R.id.root)
	FrameLayout root;
	@InjectView(R.id.content_hamburger)
	View contentHamburger;
	private Button write,help,choose;
	private AlertDialog.Builder builder;
	private Dialog writeDialog, readDialog;
	private NfcAdapter adapter;
	private IntentFilter[] mWriteTagFilters,mReadFilters;
	private PendingIntent pendingIntent;
    private String[][] mTechLists;
	Queue<String> queue;
	String num1,num2;
	int readCount = 0;
	boolean hasWrite = false;
	boolean nowRead = false;
	boolean AIMode = false;
	game1AI ai;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		initUI();
		if(StaticRes.nfc_status && StaticRes.nfc_enable) {
			adapter = NfcAdapter.getDefaultAdapter(Game1.this);
		/*
		mInitQueue = new InitQueue();
		mInitQueue.start();
		*/
			queue = new LinkedList<String>();
			int[] num = {5,3,7,4,5,8,2,6,9,1};
			for(int i=0;i<10;i++) {
				queue.add(num[i] + "");
			}
			pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Game1.this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			// д���ǩȨ��
			IntentFilter writeFilter = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED);
			mWriteTagFilters = new IntentFilter[] { writeFilter };
			mTechLists = new String[][] {
					new String[] { MifareClassic.class.getName() },
					new String[] { NfcA.class.getName() } };// ����ɨ��ı�ǩ����

			IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
			ndef.addCategory("*/*");
			mReadFilters = new IntentFilter[] { ndef };// ������
		}
		switchy();
	}
	private void switchy() {
		LinearLayout g2 = (LinearLayout) findViewById(R.id.g2_group);
		g2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Game1.this, Game2.class);
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
						Intent intent = new Intent(Game1.this, Game3.class);
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
						Intent intent = new Intent(Game1.this, StgsActivity.class);
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
	public void onResume() {
		super.onResume();
		if(StaticRes.nfc_status && StaticRes.nfc_enable) {
			if(!hasWrite) {
				adapter.enableForegroundDispatch(this, pendingIntent,mWriteTagFilters, mTechLists);
			} else {
				adapter.enableForegroundDispatch(this, pendingIntent, mReadFilters,
						mTechLists);
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
	protected void onDestroy() {
		//if(StaticRes.mp != null)
			//StaticRes.mp.release();
		super.onDestroy();
	}
	@Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            dealIntent(intent);
        }
    }
	private void dealIntent(Intent intent) {
		if(hasWrite == false) {   	
        	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = getNoteAsNdef();
            if (ndefMessage != null) {
                writeCard(ndefMessage, tag);
            } else {
                showToast("已经完成写入");
            }
        } else if(nowRead == true) {
        	readTag(intent);
        }
	}
	class InitQueue extends Thread {
		public Handler mHandler;
		
		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				
				@Override
				public void handleMessage(Message msg) {
					if(msg.what == Data.initQueue) {
						int[] num = {5,3,7,4,5,8,2,6,9,1};
						int numCount = 10;
						for(int i=0; i<numCount; i++) {
							queue.add(num[i] + "");
						}
					}
				}
			};
			Looper.loop();
		}
	}
	private void initQueue() {
		int[] num = {5,3,7,4,5,8,2,6,9,1};
		int numCount = 10;
		for(int i=0; i<numCount; i++) {
			queue.add(num[i] + "");
		}
	}
	private void initUI() {
		setContentView(R.layout.activity_g1);
		ButterKnife.inject(this);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setTitle(null);
		}

		View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_g1, null);
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
				builder = new AlertDialog.Builder(Game1.this);
				builder.setTitle("帮助");
				builder.setMessage(getResources().getString(R.string.help_1));
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
				if(StaticRes.nfc_status && StaticRes.nfc_enable) {
					builder = new AlertDialog.Builder(Game1.this);
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
		choose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(StaticRes.nfc_status && StaticRes.nfc_enable) {
					if(!hasWrite) {
						builder = new AlertDialog.Builder(Game1.this);
						builder.setTitle("提示：");
						builder.setMessage("请完成卡片制作");
						builder.setNegativeButton("已经有了卡牌", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								nowRead = true;
								hasWrite = true;
								dialog.dismiss();
								builder = new AlertDialog.Builder(Game1.this);
								builder.setTitle("请选择游戏模式");
								builder.setNegativeButton("人机对战", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});

								builder.setNeutralButton("与人对战", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});

								builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
								builder.create().show();

							}
						});
						builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						builder.create().show();
					} else {
						nowRead = true;
						builder.setTitle("请选择游戏模式");
						builder.setNegativeButton("人机对战", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});

						builder.setNeutralButton("与人对战", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});

						builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
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
	// �����ı�����һ��NdefRecord
    private NdefMessage getNoteAsNdef() {
        String text = queue.peek();
        //showToast("Queue size now = "+queue.size());
        String appendix = Data.game1CardNumber - queue.size() + "";
        if (text.equals("")) {
            return null;
        } else {
        	text = appendix + ":" + text;
            byte[] textBytes = text.getBytes();
            // image/jpeg text/plain
            NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                    "image/jpeg".getBytes(), new byte[] {}, textBytes);
            return new NdefMessage(new NdefRecord[] { textRecord });
        }
    }
	// д��tag
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
                queue.remove();
                showToast("写入数据成功");
                if(queue.size() == 0) {
                	showToast("全部数据写入成功");
					writeDialog.dismiss();
                	hasWrite = true;
                	initQueue();
                }
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
    //read tag and return the content of the tag
  	private String processIntent(Intent intent) {
          Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
          NdefMessage msg = (NdefMessage) rawMsgs[0];
          NdefRecord[] records = msg.getRecords();
          String resultStr = new String(records[0].getPayload());
          //showToast(resultStr);
          return resultStr;
      }
    private void readTag(Intent intent) {
    	switch (readCount) {
			case 0: {
				num1 = processIntent(intent);
				String[] str = num1.split(":");
				//String id = str[0];
				num1 = str[1];
				//showToast(num1);
				showToast("请贴近下一张卡片");
				readCount++;
				break;
			}
			case 1: {
				num2 = processIntent(intent);
				String[] str = num2.split(":");
				//String id2 = str[0];
				num2 = str[1];
				//showToast(num2);
				int sum = 0;
		   		try {
		   			sum = Integer.parseInt(num1) + Integer.parseInt(num2);
		   		} catch (Exception e) {
			   		showToast(e.getMessage());
			   		showToast("不能转化");
			   	}
			   	showToast("和为： " + sum);
			   	if(sum == 10) {
			   		showToast("这两张卡牌是一对");
			   	} else {
			   		showToast("这两张卡牌不是一对");
			   	}
				if(AIMode) {
					String result = ai.choose().recordToString();
					showToast("AI要求读入：" + result);
				}
			   	readCount = 0;
			   	if(readDialog != null) {
			   		readDialog.dismiss();		   		}
		   		break;
		   	} 		
		}
    }
    private void showToast(String text) {
    	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
