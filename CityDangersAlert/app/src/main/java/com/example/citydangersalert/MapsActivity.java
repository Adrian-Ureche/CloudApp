package com.example.citydangersalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.citydangersalert.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler problemsHandler=new Handler();
    private RepetingThread repetingThread;
    private ProblemsRepo problemsRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        repetingThread=new RepetingThread();
        repetingThread.start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        problemsRepo=new ProblemsRepo(mMap,repetingThread.handler, new Handler(), getApplicationContext());
        repetingThread.handler.post(problemsRepo);
        mMap.setBuildingsEnabled(true);
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(15);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        }
                    }
                });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Problem problem=new Problem();
                problem.position=latLng;
                problem.status="New";
                problem.user=CurrentUser.currentUser;
                final String[] problems = {
                        "Pit on road", "Destroyed bench", "Plaster falling off the wall", "Illegal dumping"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Select a text size");
                builder.setItems(problems, new DialogInterface.OnClickListener() {@
                        Override
                public void onClick(DialogInterface dialog, int which) {
                    problem.type=problems[which];
                    new PostProblem().execute(problem);
                }
                });
                builder.show();
            }
        });
    }

    public class PostProblem extends AsyncTask<Problem, Void, Integer> {
        public int Code=404;
        private Problem problem_;
        protected Integer doInBackground(Problem... problems) {
            problem_=problems[0];
            try {
                URL url = new URL("https://citydangersapi.azurewebsites.net/problems");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", problems[0].user);
                jsonObject.put("status", problems[0].status);
                jsonObject.put("partitionKey",problems[0].type);
                jsonObject.put("rowKey",String.valueOf(problems[0].position.latitude)+", "
                +String.valueOf(problems[0].position.longitude));


                DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));

                int code = httpURLConnection.getResponseCode();
                Log.e("code: ", String.valueOf(code));
                if (code == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    StringBuffer stringBuffer = new StringBuffer();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                        Log.e("asdfasfd",stringBuffer.toString());
                    }
                    httpURLConnection.disconnect();
                }
                return code;
            } catch (Exception e) {

                e.printStackTrace();
            }
            return 404;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            if(code==200)
            {
                Log.e("asdf","problema trimisa");
                Bitmap bitmap=null;
                Bitmap resizedBitmap=null;
                if ("Pit on road".equals(problem_.type)) {
                    bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("road", "drawable", getPackageName()));
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
                } else if ("Destroyed bench".equals(problem_.type)) {
                    bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("bench", "drawable", getPackageName()));
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
                } else if ("Plaster falling off the wall".equals(problem_.type)) {
                    bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("wall", "drawable", getPackageName()));
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
                } else if ("Illegal dumping".equals(problem_.type)) {
                    bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("garbage", "drawable", getPackageName()));
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
                }
                mMap.addMarker(new MarkerOptions()
                        .position(problem_.position)
                        .title(problem_.type)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)))
                        .setSnippet("Status: new");
                problemsRepo.addProblem(problem_);

            }
        }
    }
}