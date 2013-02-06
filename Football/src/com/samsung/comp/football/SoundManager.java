package com.samsung.comp.football;

import android.media.AudioManager;

import com.badlogic.gdx.audio.Sound;

public class SoundManager {

	AudioManager audioManager;

	public SoundManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}

	public void play(Sound sound) {
		// Only play sound when no call is being made or recieved
		if (audioManager.getMode() == AudioManager.MODE_NORMAL) {
			sound.play();
		}
	}

}
