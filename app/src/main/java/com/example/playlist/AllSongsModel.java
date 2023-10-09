package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class AllSongsModel {

    @SerializedName("name")
    private String name;

    @SerializedName("artist")
    private String artist;

    @SerializedName("time")
    private String time;

    @SerializedName("image")
    private String image;

    @SerializedName("views")
    private String views;

    public AllSongsModel(String name, String artist, String time, String image, String views) {
        this.name = name;
        this.artist = artist;
        this.time = time;
        this.image = image;
        this.views = views;
    } // Constructor END

    public AllSongsModel() {

    } // Default Constructor

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getTime() {
        return time;
    }

    public String getViews() {
        return views;
    }

    public String getImage() {
        return image;
    }

    public void setName() {
        this.name = name;
    } // setName

    public void setArtist() {
        this.artist = artist;
    } // setArtist

    public void setTime() {
        this.time = time;
    } // setTime

    public void setViews() {
        this.views = views;
    } // setViews

    public void setImage() {
        this.image = image;
    } // setImage

} // AllModel CLASS END
