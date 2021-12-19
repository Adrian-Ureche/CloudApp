package com.example.citydangersalert;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Problem {
    public String user;
    public String status;
    public String type;//partitionkey
    public LatLng position;//rowkey
    public Marker marker;
}
