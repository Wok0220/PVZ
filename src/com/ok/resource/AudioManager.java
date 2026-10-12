package com.ok.resource;

public class AudioManager {
    private static AudioManager instance;
    
    private AudioManager() {
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    public void playSound(String soundName) {
        // 实现播放音效的逻辑
    }
    
    public void playMusic(String musicName) {
        // 实现播放音乐的逻辑
    }
    
    public void stopMusic() {
        // 实现停止音乐的逻辑
    }
    
    public void setVolume(float volume) {
        // 实现设置音量的逻辑
    }
}