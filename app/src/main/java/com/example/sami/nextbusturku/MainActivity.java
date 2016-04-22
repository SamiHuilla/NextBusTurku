package com.example.sami.nextbusturku;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "HttpExample";
    private String stringUrlStop = "http://data.foli.fi/gtfs/stop_times/stop/";
    private String stringUrlTrip = "http://data.foli.fi/gtfs/trips/trip/";
    private String stringUrlRoutes = "http://data.foli.fi/gtfs/routes";
    private String tripId;
    private String routeId;
    private String downloadedString;
    private Object[] stopNumbers;
    protected boolean downloadComplete = false;
    protected int downloadCount = 0;
    private String stopString = "";
    private String info = "";
    private TextView textView;
    private TextView textView2;
    private AutoCompleteTextView autoCompleteTextView;
    private TimeFormat currentTime;
    private TimeFormat stopTime;
    private JSONObject stopsjson = new JSONObject();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateStopNumberArray();

        textView = (TextView) findViewById(R.id.myText);
        textView2 = (TextView) findViewById(R.id.myText2);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_stop);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, stopNumbers);
        autoCompleteTextView.setAdapter(adapter);


        setCurrentTime();

        // listener for the auto complete text view, starts download when the user selects a stop
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                selection = selection.substring(0,selection.indexOf(" - "));
                stringUrlStop += selection;
                initiateDownload(stringUrlStop);
                autoCompleteTextView.dismissDropDown();
                hideKeyboard(MainActivity.this);

            }
        });







        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void generateStopNumberArray() {
        stopsjson = JsonFromResource(R.raw.stops);
        JSONArray numarray = stopsjson.names();

        ArrayList<String> stopnumberlist = new ArrayList<>();
        for (int i=0; i<numarray.length(); i++){
            String number = numarray.optString(i);
            String name = "";
            try {
                name = stopsjson.getJSONObject(number).getString("stop_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            stopnumberlist.add(number + " - " + name);
        }
        Collections.sort(stopnumberlist);
        stopNumbers =  stopnumberlist.toArray();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // When called, refreshes the current time using the information provided by Calendar.
    public void setCurrentTime() {
        TimeFormat time = new TimeFormat();
        Calendar c = Calendar.getInstance();
        time.setHour(c.get(Calendar.HOUR_OF_DAY));
        time.setMinute(c.get(Calendar.MINUTE));
        time.setSecond(c.get(Calendar.SECOND));
        currentTime = time;
    }
    // Returns the string depending on current day
    public String currentService(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch(day){
            case Calendar.MONDAY:
                return "A:FOLI_Arki";
            case Calendar.TUESDAY:
                return "A:FOLI_Arki";
            case Calendar.WEDNESDAY:
                return "A:FOLI_Arki";
            case Calendar.THURSDAY:
                return "A:FOLI_Arki";
            case Calendar.FRIDAY:
                return "A:FOLI_Arki";
            case Calendar.SATURDAY:
                return "L:FOLI_Lauantai";
            case Calendar.SUNDAY:
                return "S:FOLI_Pyha";
        }
        return null;
    }

    public TimeFormat getCurrentTime() {
        return currentTime;
    }

    public JSONObject JsonFromResource(int resId){
        InputStream is = getResources().openRawResource(resId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        JSONObject stopsjson = new JSONObject();
        try {
            stopsjson = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stopsjson;
    }
    // Hakee pysäkin aikataulu-jsonista seuraavan pysähtyvän vuoron
    public JSONObject nextTrip(JSONArray array) {
        stopTime = new TimeFormat();
        String arrivalTime;
        JSONObject nextTrip = new JSONObject();
        for (int i = 0; i < array.length(); i++) {

            try {
                nextTrip = array.getJSONObject(i);
                arrivalTime=nextTrip.getString("arrival_time");
              //  textView.setText(arrivalTime);
                stopTime.update(arrivalTime);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // exit loop when the next arrival time has been found:
            if (stopTime.compareTo(currentTime) == 1) {
              //  textView.setText(stopTime.toString());
                break;
            }

        }
        return nextTrip;
    }
    // Hakee routes-jsonista routeobjektin jolla on haluttu routeId
    public JSONObject getRouteObject(JSONArray array){
        JSONObject route = new JSONObject();
        for (int i = 0; i < array.length(); i++) {

            try {
                route = array.getJSONObject(i);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (route.optString("route_id").equals(routeId)) {
                //  textView.setText(stopTime.toString());
                break;
            }

        }
        return route;
    }

    // kutsutaan asynctaskin onPostExcecutesta, tekee pysäkki-jsonin ja etsii siitä seuraavan tripin
    public void findNextTrip(){
        stopString = downloadedString;
        try {
            //JSONObject stop = new JSONObject(stopString);

            JSONArray stopArray = new JSONArray(stopString);
            JSONObject trip = nextTrip(stopArray);
            tripId = trip.optString("trip_id");
           // textView.setText(tripId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // kutsutaan asynctaskin onPostExcecutesta, tekee tripin jsonin ja hakee siitä routeId:n
    public void findRoute(){
        try {

            JSONArray tripArray = new JSONArray(downloadedString);
            JSONObject tripInfo = tripArray.getJSONObject(0);
            routeId = tripInfo.optString("route_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // kutsutaan asynctaskin onPostExcecutesta, hakee saapuvan linjan nimen ja näyttää sen ja saapumisajan textviewissä
    public void findShortName(){
        try {

            JSONArray routeArray = new JSONArray(downloadedString);
            JSONObject route = getRouteObject(routeArray);
            info = stopTime.toString()+" / "+route.optString("route_short_name");
            textView2.setText(info);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sami.nextbusturku/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sami.nextbusturku/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void initiateDownload (String url){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            textView.setText("No network connection available.");
        }
    }
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            downloadedString = result;
            downloadComplete = true;
            switch (downloadCount){
                case 0:
                    findNextTrip();
                    initiateDownload(stringUrlTrip + tripId);

                    currentTime = stopTime;
                case 1:
                    findRoute();
                    initiateDownload(stringUrlRoutes);


                case 2:
                    findShortName();

            }
            downloadCount++;



        }
        @Override
        protected void onPreExecute(){
            downloadComplete = false;
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;


            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }
    }
}

