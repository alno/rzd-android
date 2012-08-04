package name.alno.rzd.android;

import java.util.ArrayList;

import name.alno.rzd.api.Station;
import name.alno.rzd.api.StationSearchService;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity( R.layout.main )
public class MainActivity extends Activity implements StationSelectorAdapter.ChangeListener {

	private final StationSearchService stationsService = new StationSearchService( new DefaultHttpClient() );

	@ViewById( R.id.sourceField )
	protected AutoCompleteTextView sourceField;

	@ViewById( R.id.destinationField )
	protected AutoCompleteTextView destinationField;

	@ViewById( R.id.datePicker )
	protected DatePicker datePicker;

	@ViewById( R.id.searchTicketsButton )
	protected Button searchTicketsButton;

	protected ArrayList<Station> history = new ArrayList<Station>();

	protected StationSelectorAdapter source;
	protected StationSelectorAdapter destination;

	@Override
	public void stationChanged() {
		updateCompleteness();
	}

	@AfterViews
	protected void initViews() {
		loadHistory();

		source = new StationSelectorAdapter( this, sourceField, stationsService, history, this );
		destination = new StationSelectorAdapter( this, destinationField, stationsService, history, this );

		updateCompleteness();
	}

	@Click( R.id.searchTicketsButton )
	protected void searchTicketsButtonClick() {
		history.remove( source.station );
		history.add( 0, source.station );

		history.remove( destination.station );
		history.add( 0, destination.station );

		saveHistory();

		Intent intent = new Intent( this, ResultsActivity_.class );
		intent.putExtra( "sourceName", source.station.name );
		intent.putExtra( "sourceId", source.station.id );
		intent.putExtra( "destinationName", destination.station.name );
		intent.putExtra( "destinationId", destination.station.id );
		intent.putExtra( "dateYear", datePicker.getYear() );
		intent.putExtra( "dateMonth", datePicker.getMonth() );
		intent.putExtra( "dateDay", datePicker.getDayOfMonth() );

		startActivity( intent );
	}

	private void saveHistory() {
		Log.i( "HISTORY", "Saving history: " + history.toString() );

		StringBuilder hb = new StringBuilder();

		for ( Station st : history ) {
			if ( hb.length() > 0 )
				hb.append( "@@" );

			hb.append( st.name );
			hb.append( '@' );
			hb.append( String.valueOf( st.id ) );
		}

		getPreferences( MODE_PRIVATE ).edit().putString( "stationHistory", hb.toString() );
	}

	private void loadHistory() {
		history.clear();

		String historyData = getPreferences( MODE_PRIVATE ).getString( "stationHistory", "" );

		if ( historyData.length() > 0 ) {
			for ( String str : historyData.split( "@@" ) ) {
				String[] pp = str.split( "@" );
				history.add( new Station( pp[0], Long.valueOf( pp[1] ) ) );
			}
		}

		Log.i( "HISTORY", "History loaded: " + history.toString() );
	}

	private void updateCompleteness() {
		searchTicketsButton.setEnabled( source.station != null && destination.station != null );
	}
}
