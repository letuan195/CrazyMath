package com.example.android.crazymath;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MyActivity extends Activity implements Runnable{

    private ProgressBar mProgressBar;
    private ImageButton btTrue;
    private ImageButton btFalse;
    private TextView tv1;
    private TextView tv2;
    private TextView tvResult;
    private ImageButton ibSound;
    private TextView tvScore;

    private Handler mHandler;
    private int mTime = 51;
    private Random mRandom = new Random();
    private boolean isMute=false;

    private boolean isClickTrue = false;
    private boolean isClickFalse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(50);
        btTrue = (ImageButton) findViewById(R.id.btTrue);
        btFalse = (ImageButton) findViewById(R.id.btFalse);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvScore = (TextView) findViewById(R.id.tvScore);
        ibSound = (ImageButton) findViewById(R.id.ibSound);

        randomNumber();

        btTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sync click
                isClickTrue = true;
                if(mTime >= 0 && isClickFalse == true){
                    return;
                }

                if(mTime == 51){
                    mHandler.postDelayed(MyActivity.this,20);
                }

                if (checkTrue()){
                    mTime = 50;
                    randomNumber();
                    tvScore.setText(""+(Integer.parseInt(tvScore.getText().toString()) +1));

                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.playInRaw(MyActivity.this,R.raw.coin).start();
                } else {
                    showDialog();
                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.playInRaw(MyActivity.this,R.raw.die).start();
                }
            }
        });
        btFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sync click
                isClickFalse = true;
                if(mTime >= 0 && isClickTrue == true){
                    return;
                }

                if(mTime == 51){
                    mHandler.postDelayed(MyActivity.this,20);
                }
                if (!checkTrue()){
                    mTime = 50;
                    randomNumber();
                    tvScore.setText(""+(Integer.parseInt(tvScore.getText().toString()) +1));

                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.playInRaw(MyActivity.this,R.raw.coin).start();

                } else {
                    showDialog();
                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.playInRaw(MyActivity.this,R.raw.die).start();
                }
            }
        });
        ibSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMute){
                    isMute = true;
                    ibSound.setImageResource(R.drawable.ic_action_volume_muted);
                    MyMediaPlayer.getInstance().setVolume(0.0f, 0.0f);
                } else {
                    isMute = false;
                    ibSound.setImageResource(R.drawable.ic_action_volume_on);
                    MyMediaPlayer.getInstance().setVolume(1.0f,1.0f);
                }
            }
        });
        mHandler = new Handler();
    }
    /*kiểm tra xem có đúng logic hay ko?*/
    private boolean checkTrue(){
        int x = Integer.parseInt(tv1.getText().toString());
        int y = Integer.parseInt(tv2.getText().toString());
        int z = Integer.parseInt(tvResult.getText().toString());

        return (x+y) == z;
    }
    /*Random number*/
    private void randomNumber(){
        int score = Integer.parseInt(tvScore.getText().toString());
        int x,y;
        if(score <= 15){
            x = mRandom.nextInt(10);
            y = mRandom.nextInt(10);
        } else if(15<score && score<= 30) {
            x = mRandom.nextInt(20);
            y = mRandom.nextInt(20);
        } else {
            x = mRandom.nextInt(50);
            y = mRandom.nextInt(50);
        }

        tv1.setText(""+x);
        tv2.setText(""+y);

        int z1 = x +y;
        int z2 = mRandom.nextInt(5) + x +y;

        int isTrue = mRandom.nextInt(3);
        if(isTrue ==0){
            tvResult.setText(""+z1);
        } else {
            tvResult.setText(""+z2);
        }

        isClickTrue = false;
        isClickFalse = false;
    }
    private void showDialog(){
        mHandler.removeCallbacks(this);
        mTime = 51;
        isClickTrue = false;
        isClickFalse = false;

        final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent);
        dialog.setContentView(R.layout.dialog_game_over);
        dialog.setCancelable(true);

        TextView tvScoreDialog = (TextView) dialog.findViewById(R.id.tvScoreDialog);
        TextView tvHighScoreDialog = (TextView) dialog.findViewById(R.id.tvHighScoreDialog);
        tvHighScoreDialog.setText(highScore());
        tvScoreDialog.setText(tvScore.getText().toString());

        Button btPlay = (Button) dialog.findViewById(R.id.btPlay);
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setProgress(50);
                tvScore.setText(""+0);
                randomNumber();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private String highScore(){
        SharedPreferences pre = getSharedPreferences("HighScore",MODE_PRIVATE);

        int highscore = pre.getInt("SCORE",0);
        int score = Integer.parseInt(tvScore.getText().toString());
        if(score >= highscore){
            highscore = score;
        }
        SharedPreferences.Editor edit = pre.edit();
        edit.putInt("SCORE",highscore);
        edit.commit();
        return ""+highscore;

    }
    @Override
    public void run() {
        mTime = mTime-1;
        if(mTime == 0){
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.playInRaw(MyActivity.this,R.raw.die).start();
            showDialog();
            return;
        }
        mProgressBar.setProgress(mTime);
        mHandler.postDelayed(this, 20);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
