package com.example.android.crazymath;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

public class MyMediaPlayer extends MediaPlayer {

	private static MyMediaPlayer myMediaPlayer;

	public static enum MP_STATE {
		PLAYING, PAUSED, STOPPED
	}

	private MP_STATE state;

	public static MyMediaPlayer getInstance() {
		if (myMediaPlayer == null) {
			myMediaPlayer = new MyMediaPlayer();
		}
		return myMediaPlayer;
	}

	private MyMediaPlayer() {
		super();
		state = MP_STATE.STOPPED;
	}

	@Override
	public void start() {
		state = MP_STATE.PLAYING;
		super.start();
	}

	@Override
	public void pause() {
		state = MP_STATE.PAUSED;
		super.pause();
	}

	@Override
	public void stop() {
		state = MP_STATE.STOPPED;
		super.stop();
		super.reset();
	}
	

	public void playLocalFile(Context c, Uri filepath) {
		if (!state.equals(MP_STATE.STOPPED)) {
			stop();
		}
		setAudioStreamType(AudioManager.STREAM_MUSIC);
		setOnPreparedListener(new MyOnPreparedListener());
		try {
			setDataSource(c, filepath);
			prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playRemoteFile(String url) {
		if (!state.equals(MP_STATE.STOPPED)) {
			stop();
		}
		setAudioStreamType(AudioManager.STREAM_MUSIC);
		setOnPreparedListener(new MyOnPreparedListener());
		try {
			setDataSource(url);
			prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static MyMediaPlayer playInRaw(Context context, int resId){
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
            if (afd == null) return null;
            myMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            myMediaPlayer.prepare();
            return myMediaPlayer;
        } catch (Exception e){

        }
        return null;
    }

	public synchronized MP_STATE getState() {
		return state;
	}


	private class MyOnPreparedListener implements OnPreparedListener {
		@Override
		public void onPrepared(MediaPlayer mediaPlayer) {
			mediaPlayer.start();
		}
	}

}
