package net.yaiba.keep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class DownloadChecker extends AsyncTask<String, String, String> {
	
    private Context context;
    
    public static final String updateUrl = "https://api.github.com/repos/benyaiba/keep/releases/latest";

    public DownloadChecker(Context context, boolean needUpdate) {
        this.context = context;
        Toast.makeText(context, "正在检查当前版本的下载次数，请稍后", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
                // Close the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            return null;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject release = new JSONObject(result);
            System.out.println("=============="+release.optJSONObject("assets").toString());
            String downloadCount = release.optJSONObject("assets").getString("download_count");
            System.out.println("=============="+downloadCount);
            Toast.makeText(context,"当前版本下载次数:"+downloadCount, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.update_error, Toast.LENGTH_LONG).show();
        }
    }

    public void checkDownloadCount() {
        super.execute(updateUrl);
    }


}
