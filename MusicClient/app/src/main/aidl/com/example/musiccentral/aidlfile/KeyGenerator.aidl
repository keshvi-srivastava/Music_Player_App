package com.example.musiccentral.aidlfile;

import com.example.musicclient.Information;

interface KeyGenerator {

    String[] getKey();
    List<Information> getAllInfo();
    Information getSpecificInfo(int id);
    String getSongURL(int id);

}