package com.example.ckgns.playerproject;

class MainData {

    private String id;
    private String albumId;
    private String title;
    private String artist;
    private boolean like;

    public MainData() {
    }

    public MainData(String id, String albumId, String title, String artist, boolean like) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.like = like;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
