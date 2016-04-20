package com.neatorobotics.sdk.android.beehive;

import android.os.AsyncTask;
import android.util.Log;

import com.neatorobotics.sdk.android.NeatoCallback;
import com.neatorobotics.sdk.android.NeatoError;
import com.neatorobotics.sdk.android.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

/**
 * Base HTTP Beehive client.
 * There is no logic into this class, only HTTP technical stuff.
 */
public class BeehiveBaseClient {

    private static final String TAG = "BeehiveBaseClient";

    public void executeCall(final String accessToken, final String verb, final String url, final JSONObject input, final NeatoCallback<BeehiveResponse> callback) {
        final AsyncTask<Void, Void, BeehiveResponse> task = new AsyncTask<Void, Void, BeehiveResponse>() {
            protected void onPreExecute() {}
            protected BeehiveResponse doInBackground(Void... unused) {
                return executeCall(accessToken,verb,url,input);
            }
            protected void onPostExecute(BeehiveResponse response) {
                callback.done(response);
            }
        };
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            task.execute();
        }
    }

    public BeehiveResponse executeCall(String accessToken, String verb,String url, JSONObject input) {

        Log.d(TAG, "### JSON input " + input);

        HttpsURLConnection urlConnection = null;
        OutputStream os = null;
        BufferedWriter writer = null;
        InputStream is = null;
        try {
            URL url1 = new URL(url);
            urlConnection = (HttpsURLConnection) url1.openConnection();
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            if(verb.equals("GET") || verb.equals("DELETE")) {
                urlConnection.setDoOutput(false);
            }else {
                urlConnection.setDoOutput(true);
            }
            urlConnection.setRequestMethod(verb);

            if(accessToken != null) {
                urlConnection.setRequestProperty("Authorization", "Bearer "+accessToken);
            }
            urlConnection.setRequestProperty("Accept", "application/vnd.neato.beehive.v1+json");
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setRequestProperty("X-Agent", DeviceUtils.getXAgentString());

            if(input != null) {
                os = urlConnection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(input.toString());
                writer.flush();
            }

            try//temp fix for 401 and WWW-AUTH header bug
            {
                is = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                is = new BufferedInputStream(urlConnection.getErrorStream());
            }
            String outputJson = getStringFromInputStream(is);
            Object json = new JSONTokener(outputJson).nextValue();
            if (json instanceof JSONArray) {
                outputJson = "{\"value\":"+outputJson+"}";
            }
            Log.d("JSON output from " + verb + " " + url, outputJson);

            int responsecode = urlConnection.getResponseCode();

            BeehiveResponse rerviceResult = new BeehiveResponse( responsecode, new JSONObject(outputJson));
            return rerviceResult;
        }
        catch (SSLHandshakeException e) {
            Log.e(TAG, "Exception", e);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception", e);
        }
        finally {
            try {
                if (writer != null) writer.close();
            }catch(IOException e){Log.e(TAG, "Exception", e);}
            try {
                if (is != null) is.close();
            }catch(IOException e){Log.e(TAG, "Exception", e);}
            try {
                if (os != null) os.close();
            }catch(IOException e){Log.e(TAG, "Exception", e);}
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    // convert InputStream to String
    private String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}