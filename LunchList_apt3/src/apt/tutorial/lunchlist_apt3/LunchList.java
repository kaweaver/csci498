package apt.tutorial.lunchlist_apt3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TabHost;
import android.widget.AdapterView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
public class LunchList extends ListActivity {
	  Cursor model=null;
	  RestaurantAdapter adapter=null;
	  EditText name=null;
	  EditText address=null;
	  RadioGroup types=null;
	  EditText notes=null;
	  Restaurant current=null;
	  RestaurantHelper helper=null;
	  public final static String ID_EXTRA="apt.tutorial._ID";
	  SharedPreferences prefs = null;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		helper=new RestaurantHelper(this);
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		initList();
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}
	class RestaurantAdapter extends CursorAdapter {
		RestaurantAdapter(Cursor c) {
				super(LunchList.this, c);
		}
		@Override
		public void bindView(View row, Context ctxt, Cursor c) {
			RestaurantHolder holder=(RestaurantHolder)row.getTag();
			holder.populateFrom(c, helper);
		}
		@Override
		public View newView(Context ctxt, Cursor c, ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.row, parent, false);
			RestaurantHolder holder=new RestaurantHolder(row);
			row.setTag(holder);
			return(row);
		}
	}
	static class RestaurantHolder {
		private TextView name=null;
		private TextView address=null;
		private ImageView icon=null;
		RestaurantHolder(View row) {
			name=(TextView)row.findViewById(R.id.title);
			address=(TextView)row.findViewById(R.id.address);
			icon=(ImageView)row.findViewById(R.id.icon);
		}
		void populateFrom(Cursor c, RestaurantHelper helper) {
			name.setText(helper.getName(c));
			address.setText(helper.getAddress(c));
			if (helper.getType(c).equals("sit_down")) {
				icon.setImageResource(R.drawable.ball_red);
			}else if (helper.getType(c).equals("take_out")) {
				icon.setImageResource(R.drawable.ball_yellow);
			}else {
				icon.setImageResource(R.drawable.ball_green);
			}
		}
	}
	public void onListItemClick(ListView list, View view, int position, long id) {
			Intent i=new Intent(LunchList.this, DetailForm.class);
			i.putExtra(ID_EXTRA, String.valueOf(id));
			startActivity(i);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.add) {
			startActivity(new Intent(LunchList.this, DetailForm.class));
			return(true);
		}else if (item.getItemId()==R.id.prefs) {
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.option, menu);
		return(super.onCreateOptionsMenu(menu));
	}
	private SharedPreferences.OnSharedPreferenceChangeListener prefListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
			if (key.equals("sort_order")) {
				initList();
			}
		}
	};
	private void initList() {
		if (model!=null) {
			stopManagingCursor(model);
			model.close();
		}
		model=helper.getAll(prefs.getString("sort_order", "name"));
		startManagingCursor(model);
		adapter=new RestaurantAdapter(model);
		setListAdapter(adapter);
	}
}
