package com.example.citydangersalert;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    private Button registrationButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private static class RegistrationTaskParameters
    {
        public String username;
        public String password;
        public String confirmPassword;
        RegistrationTaskParameters(String username, String password,String confirmPassword)
        {
            this.username= username;
            this.password=password;
            this.confirmPassword=confirmPassword;
        }
    }
    private static class RegistrationTaskReturnParameters
    {
        public Boolean verif=false;
        public String error="";
        public String username;
        public String password;
    }
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        TextView logInTextView = findViewById(R.id.logInTextView);
        registrationButton=findViewById(R.id.RegistrationButton);
        usernameEditText=findViewById(R.id.usernameRegistration);
        passwordEditText=findViewById(R.id.passwordRegistration);
        passwordConfirmEditText=findViewById(R.id.passwordConfirmRegistration);
        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RegisterActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RegistrationTask().execute(
                        new RegistrationTaskParameters(
                                String.valueOf(usernameEditText.getText()),
                                String.valueOf(passwordEditText.getText()),
                                String.valueOf(passwordConfirmEditText.getText())));
            }
        });
    }

    class RegistrationTask extends AsyncTask<RegistrationTaskParameters,Void, RegistrationTaskReturnParameters>
    {
        @Override
        protected RegistrationTaskReturnParameters doInBackground(RegistrationTaskParameters... registrationTaskParameters) {
            RegistrationTaskReturnParameters r=new RegistrationTaskReturnParameters();
            r.password=registrationTaskParameters[0].password;
            r.username=registrationTaskParameters[0].username;
            if(registrationTaskParameters[0].password.compareTo(registrationTaskParameters[0].confirmPassword)!=0)
            {
                r.verif=false;
                r.error="Your password and confirmation password must match";
                return r;
            }
            HttpURLConnection connection=null;
            try {
                URL url = new URL("https://citydangersapi.azurewebsites.net/users");
                connection = (HttpURLConnection) url.openConnection();
                Log.e("dasfas","inainte de  a se stabili conexiunea");
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Log.e("dasfas","s a stabilit conexiunea");
                    // Do normal input or output stream reading
                    StringBuilder content;
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        content = new StringBuilder();
                        line = in.readLine();
                        Log.e("dasfas",line);
                        if(line==null)
                        {
                            Log.e("asdf","line goala");
                            throw new MalformedURLException();
                        }
                        else{
                            JSONArray array = new JSONArray(line);
                            for(int i=0; i < array.length(); i++)
                            {
                                JSONObject object = array.getJSONObject(i);
                                Log.e("dsafd",object.getString("partitionKey"));
                                Log.e("dsafd",object.getString("rowKey"));

                                if(registrationTaskParameters[0].username.compareTo(object.getString("partitionKey"))==0)
                                {
                                    r.verif=false;
                                    r.error="user already exists! try another name";
                                    return r;
                                }
                            }
                            r.verif=true;
                            r.error="";
                            return r;
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
                if(connection!=null)
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(RegistrationTaskReturnParameters registrationTaskReturnParameters) {
            super.onPostExecute(registrationTaskReturnParameters);
            if(!registrationTaskReturnParameters.verif)
            {
                if(registrationTaskReturnParameters.error.compareTo("Your password and confirmation password must match")==0)
                {
                    passwordConfirmEditText.setError("Your password and confirmation password must match");
                }
                else
                if(registrationTaskReturnParameters.error.compareTo("user already exists! try another name")==0)
                {
                    usernameEditText.setError("user already exists! try another name");
                }
            }
            else
            {
                List<String> stringList = new ArrayList<>();
                stringList.add(String.valueOf(usernameEditText.getText()));
                stringList.add(String.valueOf(passwordEditText.getText()));
                new postRegistrationInfo().execute(stringList);
            }
        }
    }

    public class postRegistrationInfo extends AsyncTask<List<String>, Void, Boolean> {

        protected Boolean doInBackground(List<String>... strings) {
            HttpURLConnection httpURLConnection=null;
            try {
                URL url = new URL("https://citydangersapi.azurewebsites.net/users");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("partitionKey", strings[0].get(0));
                jsonObject.put("rowkey", strings[0].get(1));
                jsonObject.put("dicount","0");

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
                    }
                    httpURLConnection.disconnect();
                    return true;
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            finally {
                if(httpURLConnection!=null)
                httpURLConnection.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result==true) {
                Log.e("adfs", "registration is successful");
                Toast.makeText(RegisterActivity.this, "successful registration", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        }
    }
}