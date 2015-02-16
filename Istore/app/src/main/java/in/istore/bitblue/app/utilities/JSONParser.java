package in.istore.bitblue.app.utilities;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JSONParser{
    static InputStream is;

    public JSONParser() {
    }


    public String makeHttpPostRequest(String url, List<NameValuePair> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpresponse = httpclient.execute(httppost);
            StatusLine statusLine = httpresponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
                HttpEntity httpentity = httpresponse.getEntity();
                is = httpentity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } else if (statusCode == 500) {
                sb = new StringBuilder("error");
                return sb.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String makeHttpPostRequestforJsonObject(String url, List<NameValuePair> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpresponse = httpclient.execute(httppost);
            StatusLine statusLine = httpresponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
                HttpEntity httpentity = httpresponse.getEntity();
                is = httpentity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                return sb.toString();
            } else if (statusCode == 500) {
                sb = new StringBuilder("error");
                return sb.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getResponse(String address) {
        StringBuilder sb = new StringBuilder();
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } else if (statusCode == 500) {
                sb = new StringBuilder("error");
                return sb.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}