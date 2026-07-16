package Graphic;

import javax.sound.sampled.*;
import java.io.File;

public class MusicManager {
    private static Clip menuMusic;
    private static Clip gameMusic;
    private static boolean isMenuMuted = false;
    private static boolean isGameMuted = false;
    private static boolean isMenuMusicPlaying = false;
    private static boolean isGameMusicPlaying = false;

    public static void playMenuMusic() {
        if (isMenuMusicPlaying || isMenuMuted) return;
        stopMusic(menuMusic);
        menuMusic = loadMusic("menu_music.wav", "menu_music.mp3");
        if (menuMusic != null) {
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMenuMusicPlaying = true;
        }
    }

    public static void stopMenuMusic() {
        if (menuMusic != null && isMenuMusicPlaying) {
            menuMusic.stop();
            isMenuMusicPlaying = false;
        }
    }

    public static void toggleMenuMute() {
        isMenuMuted = !isMenuMuted;
        if (menuMusic != null) {
            if (isMenuMuted) {
                menuMusic.stop();
                isMenuMusicPlaying = false;
            } else {
                menuMusic.start();
                menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                isMenuMusicPlaying = true;
            }
        }
    }

    public static void playGameMusic() {
        if (isGameMusicPlaying || isGameMuted) return;
        stopMusic(gameMusic);
        gameMusic = loadMusic("game_music.wav", "game_music.mp3");
        if (gameMusic != null) {
            gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isGameMusicPlaying = true;
        }
    }

    public static void stopGameMusic() {
        if (gameMusic != null && isGameMusicPlaying) {
            gameMusic.stop();
            isGameMusicPlaying = false;
        }
    }

    public static void toggleGameMute() {
        isGameMuted = !isGameMuted;
        if (gameMusic != null) {
            if (isGameMuted) {
                gameMusic.stop();
                isGameMusicPlaying = false;
            } else {
                gameMusic.start();
                gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
                isGameMusicPlaying = true;
            }
        }
    }

    public static boolean isMenuMuted() {
        return isMenuMuted;
    }

    public static boolean isGameMuted() {
        return isGameMuted;
    }

    private static Clip loadMusic(String wavName, String mp3Name) {
        try {
            java.net.URL url = MusicManager.class.getResource(wavName);
            if (url == null) {
                url = MusicManager.class.getResource(mp3Name);
            }
            if (url == null) {
                File file = new File(wavName);
                if (!file.exists()) file = new File(mp3Name);
                if (file.exists()) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    return clip;
                } else {
                    System.err.println("Music file not found: " + wavName + " or " + mp3Name);
                    return null;
                }
            } else {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                return clip;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void stopMusic(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}