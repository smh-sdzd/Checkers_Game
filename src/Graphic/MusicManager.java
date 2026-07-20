package Graphic;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Expected file names:
 * - menu_music.wav or menu_music.mp3
 * - game_music.wav or game_music.mp3
 */


public class MusicManager {
    private static Clip menuMusic, gameMusic;
    private static boolean isMenuMuted = false, isGameMuted = false;
    private static boolean isMenuMusicPlaying = false, isGameMusicPlaying = false;

    /**
     * Starts playing menu music if not already playing or muted.
     */
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

    /**
     * Starts playing game music if not already playing or muted.
     * Typically called when a game session begins.
     */

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

    public static boolean isMenuMuted() { return isMenuMuted; }
    public static boolean isGameMuted() { return isGameMuted; }



    private static Clip loadMusic(String wav, String mp3) {
        try {
            java.net.URL url = MusicManager.class.getResource(wav);
            if (url == null) url = MusicManager.class.getResource(mp3);
            if (url == null) {
                File f = new File(wav);
                if (!f.exists()) f = new File(mp3);
                if (f.exists()) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    return clip;
                }
                System.err.println("Music not found: " + wav + " or " + mp3);
                return null;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
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