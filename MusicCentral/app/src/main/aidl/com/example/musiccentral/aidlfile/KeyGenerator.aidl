package com.example.musiccentral.aidlfile;

import com.example.musiccentral.Information;

interface KeyGenerator {

    //void addSongs(String name, String artist, String imageId);
    String[] getKey();
    //parceable[] getAllInfo();
    List<Information> getAllInfo();
    Information getSpecificInfo(int id);
    String getSongURL(int id);

}



