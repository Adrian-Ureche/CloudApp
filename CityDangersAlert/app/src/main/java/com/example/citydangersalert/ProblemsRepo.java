package com.example.citydangersalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ProblemsRepo implements Runnable{
    private List<Problem> repo;
    GoogleMap mMap;
    Handler bghandler;
    Handler uiHandler;
    Context appContext;
    public ProblemsRepo(GoogleMap googleMap,Handler handler, Handler uiHandler, Context appContext)
    {
        this.appContext=appContext;
        this.uiHandler=uiHandler;
        bghandler=handler;
        mMap=googleMap;
        repo=new ArrayList<Problem>();
    }
    public void addProblem(Problem problem)
    {
        repo.add(problem);
    }
    @Override
    public void run() {
        HttpURLConnection connection=null;
        try {
            URL url = new URL("https://citydangersapi.azurewebsites.net/problems");
            connection = (HttpURLConnection) url.openConnection();
            Log.e("dasfas","inainte de  a se stabili conexiunea");
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                StringBuilder content;
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    content = new StringBuilder();
                    line = in.readLine();
                    if(line==null)
                    {
                        throw new MalformedURLException();
                    }
                    else{
                        JSONArray array = new JSONArray(line);
                        for(int i=0; i < array.length(); i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            Problem problem= createProblemFromJson(object);
                            Problem existentProblem=containProblem(repo,problem);
                            if(existentProblem==null)
                            {
                                Log.e("as","s-a intrat in if");
                               repo.add(problem);
                               uiHandler.post(new AddMarkersToMap(problem));
                            }
                            else
                                if(problemHasUpdated(existentProblem, problem))
                                {
                                    Boolean notif=false;
                                    if(existentProblem.user.compareTo(CurrentUser.currentUser)==0)
                                    {
                                        notif=true;
                                    }
                                    uiHandler.post(new UpdateMarkersToMap(existentProblem,problem,notif));
                                }
                            /*
                            if(!repo.contains(new Problems(array))
                            return problem();
                             */
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            bghandler.postDelayed(this,5000);
            if(connection!=null)
            connection.disconnect();
        }
    }

    private boolean problemHasUpdated(Problem existentProblem, Problem problem) {
        if(existentProblem.status.compareTo(problem.status)==0)
            return false;
        return true;
    }

    private Problem containProblem(List<Problem> repo, Problem problem) {
     //   Log.e("problema cautata: ", problem.status+" "+problem.type+" "+problem.position);
        for(int i=0;i<repo.size();i++)
        {
            Problem p=repo.get(i);
         //   Log.e("problema in repo: ", p.status+" "+p.type+" "+p.position);
            if(problem.position.toString().compareTo(p.position.toString())==0&&
            p.type.compareTo(problem.type)==0) {
          //      Log.e("problema gasita: ", problem.status+" "+problem.type+" "+problem.position);
                return p;
            }
        }
        //Log.e("problema negasita: ", problem.status+" "+problem.type+" "+problem.position);
        return null;
    }

    private Problem createProblemFromJson(JSONObject object) throws JSONException {
        Problem problem=new Problem();
        problem.user=object.getString("user");
        problem.status=object.getString("status");
        problem.type=object.getString("partitionKey");
        String[] positionComponent=object.getString("rowKey").split(", ");
        problem.position=new LatLng (Double.valueOf(positionComponent[0]),Double.valueOf(positionComponent[1]));
        return problem;
    }
    class AddMarkersToMap implements Runnable
    {
        Problem problem;
public AddMarkersToMap(Problem problem)
{
    this.problem=problem;
}
        @Override
        public void run() {
            problem.marker = mMap.addMarker(createMarkerOptionFromProblem());
            problem.marker.setSnippet(problem.status);
            Log.e("asdf","marker adaugat");
        }
        private MarkerOptions createMarkerOptionFromProblem() {
            Bitmap bitmap=null;
            Bitmap resizedBitmap=null;
            if ("Pit on road".equals(problem.type)) {
                bitmap = BitmapFactory.decodeResource(appContext.getResources(),appContext.getResources().getIdentifier("road", "drawable", appContext.getPackageName()));
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
            } else if ("Destroyed bench".equals(problem.type)) {
                bitmap = BitmapFactory.decodeResource(appContext.getResources(),appContext.getResources().getIdentifier("bench", "drawable", appContext.getPackageName()));
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
            } else if ("Plaster falling off the wall".equals(problem.type)) {
                bitmap = BitmapFactory.decodeResource(appContext.getResources(),appContext.getResources().getIdentifier("wall", "drawable", appContext.getPackageName()));
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
            } else if ("Illegal dumping".equals(problem.type)) {
                bitmap = BitmapFactory.decodeResource(appContext.getResources(),appContext.getResources().getIdentifier("garbage", "drawable", appContext.getPackageName()));
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 125, 125, false);
            }
           return new MarkerOptions()
                    .position(problem.position)
                    .title(problem.type)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
        }
    }

    class UpdateMarkersToMap implements Runnable
    {
        Problem existentProblem;
        Problem newProblem;
        Boolean notif;
        public UpdateMarkersToMap(Problem existentProblem, Problem newProblem, Boolean notif)
        {
            this.existentProblem=existentProblem;
            this.newProblem=newProblem;
            this.notif=notif;
        }
        @Override
        public void run() {
            Log.e("update","s-a intrat in update");
            existentProblem.marker.setSnippet(newProblem.status);
            existentProblem.status=newProblem.status;
            if(notif==true)
            {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
                    NotificationChannel notifChannel = new NotificationChannel("My notif", "My notif", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager=appContext.getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(notifChannel);

                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext,"My notif");
                builder.setContentTitle("CityDangers");
                builder.setContentText("One of your reported problems has changed its status");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                builder.setSmallIcon(appContext.getResources().getIdentifier("bench", "drawable", appContext.getPackageName()));
                builder.setAutoCancel(true);
                NotificationManagerCompat managerCompat= NotificationManagerCompat.from(appContext);
                managerCompat.notify(1,builder.build());
            }
        }
    }
}
