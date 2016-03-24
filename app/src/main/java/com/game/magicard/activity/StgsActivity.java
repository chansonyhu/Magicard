package com.game.magicard.activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.yalantis.guillotine.sample.animation.GuillotineAnimation;
import com.game.magicard.R;
import com.game.magicard.data.StaticRes;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * Created by 千山 on 2015/9/7.
 */
public class StgsActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    private GuillotineAnimation gagb;
    private long exitTime = 0;
    private RadioGroup rg_music, rg_sound;
    private RadioButton rb_music0, rb_music1, rb_sound0, rb_sound1;
    private SeekBar sb;
    private int maxVolume, currentVolume;
    private AudioManager am;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.root)
    FrameLayout root;
    @InjectView(R.id.content_hamburger)
    View contentHamburger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        set_music();
        aj_vol();
        switchy();
    }
    private void initUI() {
        setContentView(R.layout.activity_stgs);
        ButterKnife.inject(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_stgs, null);
        root.addView(guillotineMenu);
        gagb = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
        rg_music = (RadioGroup)findViewById(R.id.rg1);
        rg_sound = (RadioGroup)findViewById(R.id.rg2);
        rb_music0 = (RadioButton)findViewById(R.id.on);
        rb_music1 = (RadioButton)findViewById(R.id.off);
        rb_sound0 = (RadioButton)findViewById(R.id.on1);
        rb_sound1 = (RadioButton)findViewById(R.id.off1);
        sb = (SeekBar)findViewById(R.id.seekBar);
        am = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(maxVolume);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        sb.setProgress(currentVolume);
        if(currentVolume > 0) {
            rb_music0.setChecked(true);
            rb_music1.setChecked(false);
            rb_sound0.setChecked(true);
            rb_sound1.setChecked(false);
        } else {
            rb_music1.setChecked(true);
            rb_music0.setChecked(false);
            rb_sound1.setChecked(true);
            rb_sound0.setChecked(false);
        }
    }
    private void set_music() {
        rg_music.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_music1.getId()) {
                    if (StaticRes.mp.isPlaying()) {
                        StaticRes.mp.pause();
                    }
                    sb.setProgress(0);
                } else {
                    if (!StaticRes.mp.isPlaying()) {
                        StaticRes.mp.start();
                        //am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                        sb.setProgress(currentVolume);
                    }
                }
            }
        });

    }

    private void aj_vol() {
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currentVolume == 0) {
                    rb_music1.setChecked(true);
                    rb_music0.setChecked(false);
                    if (StaticRes.mp.isPlaying()) {
                        StaticRes.mp.pause();
                    }
                } else {
                    rb_music1.setChecked(false);
                    rb_music0.setChecked(true);
                    if (!StaticRes.mp.isPlaying()) {
                        StaticRes.mp.start();
                    }
                }
                sb.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                        Intent intent = new Intent(StgsActivity.this, Game1.class);
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
                        Intent intent = new Intent(StgsActivity.this, Game2.class);
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
                    Intent intent = new Intent(StgsActivity.this, Game3.class);
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

