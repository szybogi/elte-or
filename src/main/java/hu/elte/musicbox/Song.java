package hu.elte.musicbox;

public class Song {

    private int id = 0;
    private String title;
    private String sheetMusic;
    private String lyrics;

    public Song(String title) {
        id += 1;
        this.title = title;

    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSheetMusic() {
        return sheetMusic;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String setLyrics(String lyrics) {
        this.lyrics = lyrics;
        return this.lyrics;
    }

    public String setSheetMusic(String sheetMusic) {
        this.sheetMusic = sheetMusic;
        return this.sheetMusic;
    }

    @Override
    public String toString() {
        return title + " " + sheetMusic;
    }
}
