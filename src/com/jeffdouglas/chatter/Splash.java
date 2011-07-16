package com.jeffdouglas.chatter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Splash extends Activity {
	
	public static final int AUTH_ID = Menu.FIRST;
	
	private static final int AUTHORIZE = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
    	Context context = getApplicationContext();
    	CharSequence text = "Refreshing Chatter feeds...";
    	int duration = Toast.LENGTH_LONG;

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
        
        ImageView image = (ImageView) findViewById(R.id.splash);
        
        image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
           
    	    	Intent intent = new Intent(Splash.this,StatusList.class);
                startActivity(intent);
                
            }

        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, AUTH_ID, 0, R.string.menu_authorize).setIcon(android.R.drawable.ic_menu_set_as);
        return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case AUTH_ID:
            Intent i = new Intent(this, Authorize.class);
            startActivityForResult(i, AUTHORIZE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

}
