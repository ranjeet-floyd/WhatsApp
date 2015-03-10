package in.istore.bitblue.app.utilities;


import android.net.http.AndroidHttpClient;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class JSONParser {
    static InputStream is;

    public JSONParser() {
    }

    public static String buildJsonQueryFromNameValuePair(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        result.append("{");
        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                //result.append("&");
                result.append(",");
            result.append("\"");
            result.append(pair.getName());
            result.append("\"");
            result.append(":");
            result.append("\"");
            //result.append("=");
            result.append(pair.getValue());
            result.append("\"");
        }
        result.append("}");
        return result.toString();
    }

    public String makeHttpUrlConnectionRequest(String url, List<NameValuePair> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        try {
            URL Url = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) Url.openConnection();
          /*  httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);*/
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            httpURLConnection.connect();
            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
            wr.write(buildJsonQueryFromNameValuePair(nameValuePairs));
            wr.flush();
            wr.close();
            int statusCode = httpURLConnection.getResponseCode();
            if (statusCode == 200 || statusCode == 201) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeHttpPostRequest(String url, List<NameValuePair> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                if (!httpRequest.containsHeader("Accept-Encoding")) {
                    httpRequest.addHeader("Accept-Encoding", "gzip");
                }
            }
        });

        httpclient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                HttpEntity entity = httpResponse.getEntity();
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (HeaderElement codec : codecs) {
                        if (codec.getName().equalsIgnoreCase("gzip")) {
                            httpResponse.setEntity(new GzipDecompressingEntity(httpResponse.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
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

    public String makeAndroidHttpClientRequest(String url, List<NameValuePair> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpPost httpRequest = new HttpPost(url);
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpRequest);
            HttpResponse httpResponse = client.execute(httpRequest);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
               /* HttpEntity httpentity = httpResponse.getEntity();
                is = httpentity.getContent();*/
                InputStream inputStream = AndroidHttpClient.getUngzippedContent(httpResponse.getEntity());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
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
        } catch (IOException e) {
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

    static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent()
                throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();

            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }

    }
}