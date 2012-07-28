package de.gcworld.ibis2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledExecutorService;

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
	int cockpit_std;
	int cockpit_min;
	int IBIS_mode;
	boolean isroute = false;
	boolean noerror = true;
	int maxroute = 0;
	
	String cockpit_min_s;
	String currentDateTimeString;
	
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
        TextView disp_delay = (TextView)findViewById(R.id.delay);
        
        //default values
        disp_route.setText("00");
        disp_2.setText("");
        
        
        currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        
        
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
    		//if((isroute) && (maxroute < 2)) {
        	/*if(IBIS_mode != 9) {
    		disp_2.append("0");
        		maxroute++;
        		}
    		else {*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("0");
    			maxroute++;
    		}
    			new sendText().execute("IBIS_date");
    			//new sendText().execute("IBIS_0");
    		//disp_1.setText(getDate());
    			//disp_1.setText(cockpit_std + ":" + cockpit_min);
    			/*try {
					outToServer.writeBytes("Hello World");
					outToServer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		
    		//}
    	}
    };
    
    private OnClickListener setRoute = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		setRouteClass();
    		new sendText().execute("IBIS_setmode_route");
    	}
    };
    
    private void setRouteClass()
    {
    	disp_1.setText("ROUTE               :   ");
		isroute = true;
    }
    
    private OnClickListener setN1 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("1");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("1");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_1");
    	}
    };
    
    private OnClickListener setN2 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("2");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("2");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_2");
    	}
    };
    
    private OnClickListener setN3 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("3");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("3");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_3");
    	}
    };
    
    private OnClickListener setN4 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("4");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("4");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_4");
    	}
    };
    
    private OnClickListener setN5 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("5");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("5");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_5");
    	}
    };
    
    private OnClickListener setN6 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("6");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("6");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_6");
    	}
    };
    
    private OnClickListener setN7 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("7");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("7");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_7");
    	}
    };
    
    private OnClickListener setN8 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("8");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("8");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_8");
    	}
    };
    
    private OnClickListener setN9 = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if((isroute) && (maxroute < 2)) {
    		disp_2.append("9");
    		maxroute++;
    		}*/
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("9");
    			maxroute++;
    		}
    		new sendText().execute("IBIS_9");
    	
    	}
    };
    
    private OnClickListener setEingabe = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		/*if(isroute) {
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
    		}*/
    		new sendText().execute("IBIS_eingabe");
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		disp_1.setText("");
    		disp_2.setText("");
    		maxroute = 0;
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
    	Log.d("IBIS2", "Trying to send Button");
    	new sendText().execute("IBIS_loeschen");
    	disp_1.setText("");
		disp_2.setText("");
		isroute = false;
		maxroute = 0;
    }
    
    private void toast(CharSequence t)
    {
    	Toast
		.makeText(this, t, Toast.LENGTH_SHORT)
		.show();
    }
    public void onDestroy()
    {
    	wrun = false;
    	try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IBIS2", "UNABLE: " + e);
		}
    }
    
    private class sendText extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String...Strings) {
			// TODO Auto-generated method stub
			Log.d("IBIS2", "OK lets go");
			String text = Strings[0];
			// TODO check whether Socket is actually connected
		
			if(socket_av) {
				
				try {
					outToServer.writeBytes(text);
					outToServer.flush();
					Log.i("IBIS2","Sending IBIS_loeschen");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					toast("No Connection");
				} catch (Exception e) {
					toast("NullPointerException");
				}
				
			}
			else {
				toast("No Connection");
			}
			return null;
			
			//return answer;
		}
		protected void onPostExecute(Void unused) {
			//Nothing
			Log.i("IBIS","Ending SendText Thread");
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
				if(isCancelled()) {
					wrun = false;
					break;
				}
				try {
					Thread.sleep(1000);
					outToServer.writeBytes("Hello World");
					outToServer.flush();
					
					Log.i("IBIS2", "listening");
					answer = in.readLine();
					
					if(answer == "") {
						outToServer.writeBytes("Hello World");
						Log.e("IBIS2", "Something is wrong, server not responding");
					}
					//String answer2 = in.readLine();
					Log.i("IBIS2", "Received: " + answer);
					
					
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
    		//String t7 = tokens.nextToken();
    		//String t8 = tokens.nextToken();
    		
    		IBIS_mode = Integer.parseInt(fourth);
    		

    			if(Integer.parseInt(first)<10) {
    				disp_route.setText("0"+first);
    			}
    			else {
    			disp_route.setText(first);
    			}
    		
    		//if(0==third.compareToIgnoreCase("IBIS_Ziel")) {
    			disp_ziel.setText(second);

    			if(third.length() > 2) {
    			third = third.substring(0, third.length() - 2);
    			}
    			disp_linie.setText(third);
    
    			//disp_1.setText(fourth);
    			
    			int time = Integer.parseInt(t5);
    			
    			cockpit_std = time / 60;
    			
    			int cockpit_min_temp = (time*100/60 - cockpit_std*100);
    			cockpit_min = cockpit_min_temp *60 /100;
    			if(cockpit_min < 10) {
    				cockpit_min_s = "0" + cockpit_min;
    			}
    			else {
    				cockpit_min_s = Integer.toString(cockpit_min);
    			}
    			
    			if(IBIS_mode == 2) {
    				setRouteClass();
    			}
    			
    			if(IBIS_mode == 9) {
    				disp_1.setText(currentDateTimeString + "            " + cockpit_std + ":" + cockpit_min_s);
    			}
    			
    			//cockpit_std = Integer.parseInt(t5) / 3600;
    			
    			//cockpit_min = (Integer.parseInt(t5) - (cockpit_std*3600)) / 60;
    		//}
    		
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