package de.gcworld.ibis2;

//import de.gcworld.ibis2.Settings;

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

import com.bugsense.trace.BugSenseHandler;

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

public class Ibis2 extends Activity {
    /** Called when the activity is first created. */
	TextView disp_1;
	TextView disp_2;
	TextView disp_route;
	TextView disp_ziel;
	TextView disp_linie;
	TextView disp_zone;
	String omsi_date;
	String route_n;
	int cockpit_std;
	int cockpit_min;
	int IBIS_mode;
	boolean isroute = false;
	boolean noerror = true;
	int maxroute = 0;
	int maxlinie = 0;
	int maxziel = 0;
	
	SharedPreferences settings_string;
	
	String ziel_code;
	
	String cockpit_min_s;
	String currentDateTimeString;
	
	String ip_address;
	String answer;
	boolean wrun;
	boolean socket_av = false;
	boolean disp;
	String linie_number;
	
	boolean linie_executed = false;
	boolean delete_executed = false;
	boolean route_executed = false;
	boolean ziel_executed = false;
	
	String busstop_code;

	
	//final String IBIS_ROUTE = "IBIS_Route";
	
	private static final int SYNC_ID = Menu.FIRST+2;
	
	Socket socket;
	DataInputStream inFromServer;
    DataOutputStream outToServer;
    int MY_PORT = 50321;
    BufferedReader in;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// The following line triggers the initialization of ACRA
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        BugSenseHandler.setup(this, "b4dccaea");
        
        settings_string = PreferenceManager.getDefaultSharedPreferences(this);
    	ip_address = settings_string.getString("ip", "127.0.0.1");
    	Log.d("IBIS2","IP ist: " + ip_address);
       
        
        //assign buttons
        disp_1 = (TextView)findViewById(R.id.disp_1);
        disp_2 = (TextView)findViewById(R.id.disp_2);
        disp_route = (TextView)findViewById(R.id.disp_route);
        disp_ziel = (TextView)findViewById(R.id.disp_ziel);
        disp_linie = (TextView)findViewById(R.id.disp_linie);
        disp_zone = (TextView)findViewById(R.id.disp_zone);
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
        
        Button linie = (Button)findViewById(R.id.linie);
        linie.setOnClickListener(setLinie);
        
        Button ziel = (Button)findViewById(R.id.ziel);
        ziel.setOnClickListener(setZiel);
        
        Button vor_stumm = (Button)findViewById(R.id.vor_stumm);
        vor_stumm.setOnClickListener(vorStummClass);
        
        Button vor = (Button)findViewById(R.id.vor);
        vor.setOnClickListener(vorClass);
        
        Button rueck = (Button)findViewById(R.id.rueck);
        rueck.setOnClickListener(rueckClass);
        
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
				if(socket_av) {
					close();	
				}
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
		
			Log.d("IBIS2", "Starting Preferences");
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
    		disp = true;
    		if(IBIS_mode == 2 && maxroute < 2) {
    			disp_2.append("0");
    			maxroute++;
    		}
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("0");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("0");
    			}
    			else{
					disp_2.append("0");
				}
    			maxlinie++;
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
    
    private OnClickListener setLinie = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		setLinieClass();
    		new sendText().execute("IBIS_setmode_linie");
    	}
    };
    
    private OnClickListener setZiel = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		setZielClass();
    		new sendText().execute("IBIS_setmode_ziel");
    	}
    };
    
    private void setRouteClass()
    {
    	disp = true;
    	disp_1.setText("ROUTE               :   ");
		isroute = true;
    }
    
    private void setLinieClass()
    {
    	disp = true;
    	disp_1.setText("LINIE/KURS          :   ");
    	disp_2.setText(linie_number);
    	
    }
    
    private void setZielClass()
    {
    	disp = true;
    	disp_1.setText("Ziel                :   ");
    	
    }
    
    private void setDisplayName()
    {
    	String karten_code = settings_string.getString("karten", "a");
    	String tmp_ziel = getStringResourceByName(karten_code + ziel_code + "b" + busstop_code);
		if(tmp_ziel == null) {
			tmp_ziel = karten_code + ziel_code + "b" + busstop_code;
		}
		disp_1.setText(tmp_ziel);
    }
    
    private OnClickListener vorStummClass = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    	new sendText().execute("IBIS_vor_stumm");
    	
    }
    };
    
    private String getStringResourceByName(String aString)
    {
      String packageName = "de.gcworld.ibis2";
      int resId = getResources().getIdentifier(aString, "string", packageName);
      Log.d("IBIS2", "Resource ID:" + resId);
      if(resId==0) {
    	  resId = getResources().getIdentifier("a0b0", "string", packageName);
      }
      return getString(resId);
    }
    
    	private OnClickListener vorClass = new OnClickListener()
        {
        	public void onClick(View v)
        	{
    	new sendText().execute("IBIS_vor");
    	
    }
        };
    
   private OnClickListener rueckClass = new OnClickListener()
            {
            	public void onClick(View v)
            	{
    	new sendText().execute("IBIS_rueck");
    	
    }
            };
    
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("1");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
    			
    			if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("1");
    			}
    			else{
					disp_2.append("1");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("2");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("2");
    			}
				else{
					disp_2.append("2");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("3");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("3");
    			}
				else{
					disp_2.append("3");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("4");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("4");
    			}
				else{
					disp_2.append("4");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("5");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("5");
    			}
				else{
					disp_2.append("5");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("6");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("6");
    			}
    			else{
					disp_2.append("6");
				}
    			maxlinie++;
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
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("7");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("7");
    			}
				else{
					disp_2.append("7");
				}
    			maxlinie++;
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
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("8");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("8");
    			}
    			else{
					disp_2.append("8");
				}
    			maxlinie++;
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
    		
    		if(IBIS_mode == 3 && maxziel < 3) {
    			disp_2.append("9");
    			maxziel++;
    		}
			
			if(IBIS_mode == 1 && maxlinie < 5) {
				if(maxlinie==0) {
    				disp_2.setText("");
    				disp_2.append("9");
    			}
				else{
					disp_2.append("9");
				}
    			maxlinie++;
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
			maxlinie = 0;
			maxziel = 0;
			disp = false;
    	}
    };
  
    
    private void deleteDisp()
    {
    	Log.d("IBIS2", "Trying to send Button");
    	new sendText().execute("IBIS_loeschen");
    	disp_1.setText("");
		disp_2.setText("");
		isroute = false;
		maxroute = 0;
		maxlinie = 0;
		maxziel = 0;
		//disp = false;
    }
    
    private void deleteDisp2()
    {
    	disp_1.setText("");
		disp_2.setText("");
		isroute = false;
		maxroute = 0;
		maxlinie = 0;
		maxziel = 0;
		disp = false;
    }
    
    private void toast(CharSequence t)
    {
    	Toast
		.makeText(this, t, Toast.LENGTH_SHORT)
		.show();
    }
    public void onDestroy()
    {
		super.onDestroy();
    	wrun = false;
    	try {
			if(socket_av) {
			socket.close();}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IBIS2", "UNABLE: " + e);
		} catch (NullPointerException e) {
			Log.e("IBIS2", "UNABLE: " + e);
		}
    }
    
    private class sendText extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String...Strings) {
			// TODO Auto-generated method stub
			Log.d("IBIS2", "OK lets go");
			String text = Strings[0];
			// TODO check whether Socket is actually connected
			String result;
			if(socket_av) {
				
				try {
					outToServer.writeBytes(text);
					outToServer.flush();
					Log.i("IBIS2","Sending IBIS_loeschen");
					result = "All good";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					result = "No Connection";
				} catch (Exception e) {
					result = "NullPointerException";
				}
				
			}
			else {
				result = "No Connection";
			}
			return result;
			
			//return answer;
		}
		protected void onPostExecute(String result) {
			//Nothin
			if(result!="All good"){
				toast(result);
			}
			Log.i("IBIS","Ending SendText Thread");
		}
    	
    }

    private class SendText2 extends AsyncTask<String, String, String> {
    	@Override
        protected String doInBackground(String...Strings) {
            
        	String result = null;
            
        	if(isOnline()) {
        	
        	try {
				socket = new Socket(ip_address, MY_PORT);
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
			} catch (NullPointerException e1) {
				Log.e("IBIS2", "UNABLE: " + e1);
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
    		String t7 = tokens.nextToken();
    		//String t7 = tokens.nextToken();
    		//String t8 = tokens.nextToken();
    		
    		busstop_code = t7;
    		
    		ziel_code = second;
    		
    		linie_number = third;
    		
    		IBIS_mode = Integer.parseInt(fourth);
    		
    		disp_zone.setText(t6);

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
    			
    			if(IBIS_mode == 0) {
    				if(!delete_executed) {
    					if(disp) {
    				
    						deleteDisp2();
    						delete_executed = true;
    					}
    				}
    			}
    			else {
    				delete_executed = false;
    			}
    			
    			if(IBIS_mode == 1) {
    				if(!linie_executed) {
    				setLinieClass();
    				linie_executed = true;
    				}
    			}
    			else {
    				linie_executed = false;
    			}
    			
    			if(IBIS_mode == 2) {
    				if(!route_executed) {
    					
    					route_executed = true;
    					setRouteClass();
    				}
    			}
    			else {
    				route_executed = false;
    			}
    			
    			if(IBIS_mode == 3) {
    				if(!ziel_executed) {
    					
    					ziel_executed = true;
    					setZielClass();
    				}
    			}
    			else {
    				ziel_executed = false;
    			}
    			
    			if(IBIS_mode == 0 || IBIS_mode == 8) {
    				
    					setDisplayName();
    				
    			}
    			
    			if(IBIS_mode == 9) {
    				disp_1.setText(currentDateTimeString + "            " + cockpit_std + ":" + cockpit_min_s);
    			}
				
				if(IBIS_mode == 4) {
					disp_1.setText("         Falsche Route");
					/*try {
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					disp_1.setText("");*/
				}//TODO RICHTIGE NUMMER
				
				if(IBIS_mode == 6) {
					disp_1.setText("         Falsches Ziel");
				}
    			
    			//cockpit_std = Integer.parseInt(t5) / 3600;
    			
    			//cockpit_min = (Integer.parseInt(t5) - (cockpit_std*3600)) / 60;
    		//}
    		
            //disp_1.setText(answer2);
        }

		@Override
        protected void onPostExecute(String result) {
            toast(result);
            socket_av = false;
            Log.i("IBIS2", "ending socket connection");
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
