package com.example.audiosample;
/**
 * Code By Shahid Iqbal and Ashish Ramtekkar
 * Email Address :ashu_w20001@yahoo.com,
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	boolean isProcessingOn = false;
	int RECORDER_SAMPLERATE;
	String[] recfiles;
	Button first,sec,mix,play;
	String filechoosepath[]=null;
	int i=0;
	String path1=null,path2=null;

	private static final int FILE_SELECT_CODE = 0;
	private static final int FILE_SELECT_CODE1= 1;
	TextView path1text,path2text; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		first=(Button) findViewById(R.id.button1);
		sec=(Button) findViewById(R.id.button2);
		mix=(Button) findViewById(R.id.button3);
		play=(Button) findViewById(R.id.button4);
		path1text=(TextView) findViewById(R.id.textView2);
		path2text=(TextView) findViewById(R.id.textView3);

		first.setOnClickListener(this);
		sec.setOnClickListener(this);
		mix.setOnClickListener(this);

	}
	
	class MixFile extends AsyncTask<Void , Void, Void>{


		ProgressDialog dialog;
		protected void onPreExecute() {
			dialog= new ProgressDialog(MainActivity.this);
			dialog.setCancelable(false);
			dialog.setMessage("Mixing two wav files");
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			short[] audioData1 = null;
			short[] audioData2 = null;

			int n = 0;

			try {
				DataInputStream in1;
				//				in1 = new DataInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + "/Soundrecpluspro/one.wav"));
				in1 = new DataInputStream(new FileInputStream(path1));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				try {

					while ((n = in1.read()) != -1) {
						bos.write(n);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
				bb.order(ByteOrder.LITTLE_ENDIAN);
				ShortBuffer sb = bb.asShortBuffer();
				audioData1 = new short[sb.capacity()];

				for (int i = 0; i < sb.capacity(); i++) {
					audioData1[i] = sb.get(i);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				DataInputStream in1;
				in1 = new DataInputStream(new FileInputStream(path2));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {

					while ((n = in1.read()) != -1) {
						bos.write(n);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
				bb.order(ByteOrder.LITTLE_ENDIAN);
				ShortBuffer sb = bb.asShortBuffer();
				audioData2=  new short[sb.capacity()];
				sb.get(audioData2);
				System.out.println();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// find the max:
			float max = 0;
			Log.d("File audio lenght 1 ", ""+audioData1.length);
			Log.d("File audio lenght 2 ", ""+audioData2.length);
			System.out.println("MainActivity.MixFile.doInBackground() 1"+audioData1.length);
			System.out.println("MainActivity.MixFile.doInBackground() 2"+audioData2.length);
			if(audioData1.length > audioData2.length){

				for (int i = 22; i < audioData2.length; i++) {
					if (Math.abs(audioData1[i] + audioData2[i]) > max)
						max = Math.abs(audioData1[i] + audioData2[i]);
				}

				System.out.println("" + (Short.MAX_VALUE - max));
				int a, b, c;
				// now find the result, with scaling:
				for (int i = 22; i < audioData2.length; i++) {
					a = audioData1[i];
					b = audioData2[i];

					c = Math.round(Short.MAX_VALUE * (audioData1[i] + audioData2[i])
							/ max);

					if (c > Short.MAX_VALUE)
						c = Short.MAX_VALUE;
					if (c < Short.MIN_VALUE)
						c = Short.MIN_VALUE;


					audioData1[i] = (short) c; 

				}

				// to turn shorts back to bytes.
				byte[] end = new byte[audioData1.length * 2];
				ByteBuffer.wrap(end).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData1);

				try {
					OutputStream out  = new FileOutputStream(Environment.getExternalStorageDirectory() + "/AudioSample/mixer1.wav");
					for (int i = 0; i < end.length; i++) {
						out.write(end[i]);
						out.flush();
					}
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else{

				System.out.println("MainActivity.MixFile.doInBackground() smaller one");
				for (int i = 22; i < audioData1.length; i++) {
					if (Math.abs(audioData2[i] + audioData1[i]) > max)
						max = Math.abs(audioData2[i] + audioData1[i]);
				}

				System.out.println("" + (Short.MAX_VALUE - max));
				int a, b, c;
				// now find the result, with scaling:
				for (int i = 22; i < audioData1.length; i++) {
					a = audioData2[i];
					b = audioData1[i];

					c = Math.round(Short.MAX_VALUE * (audioData2[i] + audioData1[i])
							/ max);

					if (c > Short.MAX_VALUE)
						c = Short.MAX_VALUE;
					if (c < Short.MIN_VALUE)
						c = Short.MIN_VALUE;
					audioData2[i] = (short) c; 
				}
				// to turn shorts back to bytes.
				byte[] end = new byte[audioData2.length * 2];
				ByteBuffer.wrap(end).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData2);
				try {
					OutputStream out  = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Assets/mixer1.wav");
					for (int i = 0; i < end.length; i++) {
						out.write(end[i]);
						out.flush();
					}
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if(dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button1:

			showFileChooser(0);
			break;
		case R.id.button2:
			showFileChooser(1);
			break;
		case R.id.button3:
			new MixFile().execute();
			break;
		case R.id.button4:
			MediaPlayer mediaPlayer= MediaPlayer.create(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory() + "/Assets/mixer1.wav"));
			mediaPlayer.start();
			break;
		default:
			break;
		}

	}



	private void showFileChooser(int code) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType("*/*"); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select an .wav file"),code);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.", 
					Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file 
				Uri uri = data.getData();
				Log.d("File", "File Uri: " + uri.toString());

				// Get the path

				try {
					path1 = getPath(this, uri);
					Log.d("File", "File path 1" +path1);
					path1text.setText(path1);
				} catch (URISyntaxException e) {

					e.printStackTrace();
				}

				// Get the file instance
				// File file = new File(path);
				// Initiate the upload
			}
			break;
		case FILE_SELECT_CODE1:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file 
				Uri uri = data.getData();
				//				Log.d("File  url", "File path 2" + uri.toString());

				// Get the path

				try {
					path2 = getPath(this, uri);
					Log.d("File  url", "File path 2" + path2);
					path2text.setText(path2);

				} catch (URISyntaxException e) {

					e.printStackTrace();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {

			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {

			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	} 

}
