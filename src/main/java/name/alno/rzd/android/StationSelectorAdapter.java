package name.alno.rzd.android;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import name.alno.rzd.api.Station;
import name.alno.rzd.api.StationSearchService;

public class StationSelectorAdapter implements TextWatcher, AdapterView.OnItemClickListener, View.OnKeyListener {

	interface ChangeListener {

		void stationChanged();

	}

	public Station station;

	public final Context context;

	public final ChangeListener listener;

	public final List<Station> history;

	public StationSelectorAdapter( Context context, AutoCompleteTextView view, StationSearchService service, List<Station> history, ChangeListener listener ) {
		this.context = context;
		this.listener = listener;
		this.history = history;

		view.setAdapter( new StationsAdapter( context, service, history ) );
		view.setMaxLines( 1 );
		view.setThreshold( 1 );
		view.addTextChangedListener( this );
		view.setOnItemClickListener( this );
		view.setOnKeyListener( this );
	}

	@Override
	public void afterTextChanged( Editable paramEditable ) {
		station = null;
		listener.stationChanged();
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View paramView, int position, long paramLong ) {
		station = (Station) parent.getItemAtPosition( position );
		listener.stationChanged();
	}

	@Override
	public boolean onKey( View view, int key, KeyEvent event ) {
		if ( (event.getAction() == KeyEvent.ACTION_DOWN) && (key == KeyEvent.KEYCODE_ENTER) ) {
			((InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE )).hideSoftInputFromWindow( view.getWindowToken(), 0 );
			return true;
		}

		return false;
	}

	@Override
	public void beforeTextChanged( CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3 ) {
		// NOP
	}

	@Override
	public void onTextChanged( CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3 ) {
		// NOP
	}

}
