package com.guleryuz.puantajonline.OnlineService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;

import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadFileToServer extends AsyncTask<Void, Void, Void> {
    private ServiceCallBack mCallback;
    public String uid;
    public String uFile;
    public Context context;
    private ProgressDialog proDialog;
    private boolean status=false;
    public String reqtype;

    public UploadFileToServer() {

    }

    public UploadFileToServer(ServiceCallBack callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mCallback!=null) {
            if (proDialog == null) {
                proDialog = new ProgressDialog(context);
                proDialog.setCanceledOnTouchOutside(false);
                proDialog.setCancelable(false);
                proDialog.setTitle("Dosyalar");
                proDialog.setMessage("Sunucuya Gönderiliyor...");
            }
            proDialog.show();
        }
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        status=false;

        try{
            if(uploadFile(uFile)==200)
                status=true;
            //uploadFile(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + resper.get(i).get("RESIM"));
        }catch (Exception ex){
            Log.w("Files2Server", ex.getMessage());
            status=false;
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        //db.close();
        if(mCallback!=null) {
            mCallback.PipeAsyncFinish(status,"File2Server",null, reqtype);

            if (proDialog.isShowing())
                proDialog.dismiss();
        }
    }

    //
    //  String name=file.substring(file.lastIndexOf('/')+1);
    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];
        Log.w("uploadFile", selectedFilePath);
        Log.w("uploadFile", ""+selectedFile.isFile());
        if (selectedFile.isFile()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                //URL url = new URL("http://sistem.guleryuzcv.net/vtys/vtys6.php?token=6ce304f73ce841efaf1490bb98474eef&op=uploadek&uid=uid&tt="+System.currentTimeMillis());
                //URL url = new URL( "http://www.guleryuzcv.net/t_istakip/mobilsrv/vtysv6.php?token=6ce304f73ce841efaf1490bb98474eef&op=uploadek&uid=uid&tt="+System.currentTimeMillis());
                URL url = new URL( context.getResources().getString(R.string.serviceUrl)+"?token=6ce304f73ce841efaf1490bb98474eef&op=uploadek&uid=uid&tt="+System.currentTimeMillis());

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("uploadfile", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });*/
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.w("uploadFile", "File Not Found");
                /*e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });*/
            } catch (MalformedURLException e) {
                Log.w("uploadFile", "URL error!");
                /*e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
*/
            } catch (IOException e) {
                Log.w("uploadFile", "Cannot Read/Write File!");
  /*              e.printStackTrace();
                Toast.makeText(MainActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();*/
            }
        }
        return serverResponseCode;
    }
}
