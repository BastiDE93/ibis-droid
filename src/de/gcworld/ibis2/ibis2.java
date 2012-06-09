package de.gcworld.ibis2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ibis2 extends Activity {
    /** Called when the activity is first created. */
	TextView disp_1;
	TextView disp_2;
	TextView disp_route;
	TextView disp_ziel;
	TextView disp_linie;
	String omsi_date;
	String route_n;
	boolean isroute = false;
	boolean noerror = true;
	int maxroute = 0;
	
	String ip_address;
	String answer;
	boolean wrun;
	boolean socket_av = false;
	
	//final String IBIS_ROUTE = "IBIS_Route";
	
	private static final int SYNC_ID = Menu.FIRST+2;
	
	Socket socket;
	DataInputStream inFromServer;
    DataOutputStream outToServer;
    int MY_PORT = 50321;
    BufferedReader in;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences settings_string = PreferenceManager.getDefaultSharedPreferences(this);
    	ip_address = settings_string.getString("ip", "127.0.0.1");
    	Log.d("IBIS2","IP ist: " + ip_address);
       
        
        //assign buttons
        disp_1 = (TextView)findViewById(R.id.disp_1);
        disp_2 = (TextView)findViewById(R.id.disp_2);
        disp_route = (TextView)findViewById(R.id.disp_route);
        disp_ziel = (TextView)findViewById(R.id.disp_ziel);
        disp_linie = (TextView)findViewById(R.id.disp_linie);
        
        //default values
        disp_route.setText("00");
        
        
        
        
        Button delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(btnListener);
        
        Button date = (Button)findViewById(R.id.date);
        date.setOnClickListener(displayDate);
        
        Button eingabe = (Button)findViewById(R.id.eingabe);
        eingabe.setOnClickListener(setEingabe);
        
        Button route = (Button)findViewById(R.id.route);
        route.setOnClickListener(setRoute);
        
        Button n1 = (Button)findViewById(R.id.n1);
        n1.setOnClickListener(setN1);
        
        Button n9 = (Button)findViewById(R.id.n9);
        n9.setOnClickListener(setN9);
        
        Button n2 = (Button)findViewById(R.id.n2);
        n2.setOnClickListener(setN2);
        
        Button n3 = (Button)findViewById(R.id.n3);
        n3.setOnClickListener(setN3);
        
        Button n4 = (Button)findViewById(R.id.n4);
        n4.setOnClickListener(setN4);
       
        Button n5 = (Button)findViewById(R.id.n5);
        n5.setOnClickListener(setN5);
        
        Button n6 = (Button)findViewById(R.id.n6);
        n6.setOnClickListener(setN6);
        
        Button n7 = (Button)findViewById(R.id.n7);
        n7.setOnClickListener(setN7);
        
        Button n8 = (Button)findViewById(R.id.n8);
        n8.setOnClickListener(setN8);
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
		
		return(true);
		}
		
		@Override 
		public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.close: 
			try {
				close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("IBIS2", "Error closing Socket: " + e);
			} 
			return(true);
		case SYNC_ID: 
			//connect("http://www.gcworld.de/test.xml");
			//connect("http://gcworld.highrisehq.com/people.xml"); 
			return(true);
		case R.id.settings:
		
			startActivity(new Intent(this, Settings.class));
			return(true);
			
		case R.id.connect:
			new SendText2().execute("date_time");
			return(true);
		
		default:
            return super.onOptionsItemSelected(item);
		
		}
		//return(super.onOptionsItemSelected(item));
	}
    
    private void close() throws IOException {
			// TODO Auto-generated method stub
    		socket.close();
    		finish(); 
		}

    
    /*private void Send(String q) {
    	try {
			outToServer.writeBytes(q);
			StringBuffer inputLine = new StringBuffer();
			q = null;
            while ((q = inFromServer.readLine()) != null) {
                inputLine.append(q);
                //System.out.println(q);
                toast(q);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			toast("Error:" + e);
		}
		//return q;
    }*/

	private OnClickListener btnListener = new OnClickListener()
    {
        public void onClick(View v)
        {                        
			deleteDisp();
        }
    }; 
    
    private OnClickListener displayDate = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
        		disp_2.append("0");
        		maxroute++;
        		}
    		else {
    		//disp_1.setText(getDate());
    			try {
					outToServer.writeBytes("Hello World");
					outToServer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
    		}
    	}
    };
    
    private OnClickListener setRoute = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		disp_1.setText("ROUTE               :   ");
    		isroute = true;
    	}
    };
    
    private OnClickListener setN1 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("1");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN2 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("2");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN3 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("3");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN4 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("4");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN5 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("5");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN6 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("6");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN7 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("7");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN8 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("8");
    		maxroute++;
    		}
    	}
    };
    
    private OnClickListener setN9 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if((isroute) && (maxroute < 2)) {
    		disp_2.append("9");
    		maxroute++;
    		}
    	
    	}
    };
    
    private OnClickListener setEingabe = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		if(isroute) {
    			CharSequence t = disp_2.getText();
    			String text = t.toString();
    			// TODO validate route
    			//if(text.equals("99")) {
    			//	noerror = true;
    			//}
    			//else {
    			//	noerror = false;
    			//};
    			
    			if(noerror) {
    				deleteDisp();
    				disp_route.setText(text);
    			}
    			else{
    				deleteDisp();
    				disp_1.setText("Falsche Route");
    			};
    			isroute = false;
    		}
    	}
    };
    
    private String getDate()
    {
    	//new sendTextTask().execute("date_time");
    	//omsi_date = Send("date_time");
		//get Date from OMSI
    	return answer;
    }
    
    private void deleteDisp()
    {
    	new sendText().execute("IBIS_loeschen");
    	disp_1.setText("");
		disp_2.setText("");
		isroute = false;
		maxroute = 0;
    }
    
    private void toast(CharSequence t)
    {
    	Toast
		.makeText(this, t, 2000)
		.show();
    }
    public void onDestroy()
    {
    	try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IBIS2", "UNABLE: " + e);
		}
		wrun = false;
    }
    
    private class sendText extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String...Strings) {
			// TODO Auto-generated method stub
			
			// TODO check whether Socket is actually connected
		
			if(socket_av) {
				
				try {
					outToServer.writeBytes("IBIS_loeschen");
					outToServer.flush();
					Log.i("IBIS2","Sending IBIS_loeschen");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return null;
			
			//return answer;
		}
		protected void onPostExecute(Void unused) {
			//Nothing
		}
    	
    }

    private class SendText2 extends AsyncTask<String, String, String> {
    	@Override
        protected String doInBackground(String...Strings) {
            
        	String result = null;
            
        	if(isOnline()) {
        	
        	try {
				socket = new Socket("192.168.178.40", MY_PORT);
				outToServer = new DataOutputStream(socket.getOutputStream());
				//inFromServer = new DataInputStream(socket.getInputStream()); 
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				socket_av = true;
				outToServer.writeBytes("Hello World");
				outToServer.flush();
			
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				result = "Error: " + e1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				result = "Error: " + e1;
			}
			
			wrun = true;
			int i = 0;
			while(wrun) {
				try {
					Thread.sleep(1000);
					outToServer.writeBytes("Hello World");
					outToServer.flush();
					
					Log.i("IBIS2", "listening");
					answer = in.readLine();
					String answer2 = in.readLine();
					Log.i("IBIS2", "Received: " + answer + "answer2=" + answer2);
					
					
					//disp_1.append("- " + i + "-");
					i++;
					if (answer != null) {
						//wrun = false;
						publishProgress(answer);
						Log.i("IBIS2", "got something");
					}
					result = answer;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					result = "Error: " + e;
					Log.i("IBIS2", "Error" + e);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	}
        	result = "No Connection";
        	
        	return result;
        }

		@SuppressWarnings("unchecked")
        @Override
		protected void onProgressUpdate(String... answer) {
    		String answer2 = answer[0];
    		
    		StringTokenizer tokens = new StringTokenizer(answer2, ":");
    		String first = tokens.nextToken();// this will contain "Fruit"
    		String second = tokens.nextToken();// this will contain " they taste good"
    		String third = tokens.nextToken();
    		String fourth = tokens.nextToken();
    		String t5 = tokens.nextToken();
    		String t6 = tokens.nextToken();
    		String t7 = tokens.nextToken();
    		String t8 = tokens.nextToken();
    		
    		Log.d("IBIS2","1: " + first + " 2: " + second + "3: " + third + " 4: " + fourth + ":" + t7 + t8);
    		if(0==first.compareToIgnoreCase("IBIS_Route")) {
    			if(Integer.parseInt(second)<10) {
    				disp_route.setText("0"+second);
    			}
    			else {
    			disp_route.setText(second);
    			}
    		}
    		if(0==third.compareToIgnoreCase("IBIS_Ziel")) {
    			disp_ziel.setText(fourth);
    		}
    		if(0==t5.compareToIgnoreCase("IBIS_Linie")) {
    			disp_linie.setText(t6);
    		}
    		
    		if(0==t7.compareToIgnoreCase("IBIS_NZiel")) {
    			disp_1.setText(t8);
    		}
    		
            //disp_1.setText(answer2);
        }

        @Override
        protected void onPostExecute(String result) {
            toast(result);
            disp_1.setText(result);
            Log.i("IBIS2", "displaying toast with result");
        }
    }
    
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
}