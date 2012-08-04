package name.alno.rzd.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.acra.ErrorReporter;

import name.alno.rzd.api.ApiException;
import name.alno.rzd.api.Station;
import name.alno.rzd.api.StationSearchService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class StationsAdapter extends BaseAdapter implements Filterable {

	private final LayoutInflater inflater;

	private final StationSearchService service;

	private final List<Station> history;

	private List<Station> data = Collections.emptyList();

	public StationsAdapter( Context context, StationSearchService service, List<Station> history ) {
		this.inflater = LayoutInflater.from( context );
		this.service = service;
		this.history = history;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Station getItem( int index ) {
		return data.get( index );
	}

	@Override
	public long getItemId( int index ) {
		return data.get( index ).id;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering( CharSequence constraint ) {
				FilterResults filterResults = new FilterResults();

				filterResults.values = Collections.emptyList();
				filterResults.count = 0;

				if ( constraint == null ) {
					filterResults.values = history;
					filterResults.count = history.size();
				} else if ( constraint.length() < 2 ) {
					String constraintUpCase = constraint.toString().toUpperCase();
					ArrayList<Station> filtered = new ArrayList<Station>();

					for ( Station st : history )
						if ( st.name.startsWith( constraintUpCase ) )
							filtered.add( st );

					filterResults.values = filtered;
					filterResults.count = filtered.size();
				} else {
					try {
						List<Station> stations = service.findStations( constraint.toString() );

						if ( stations.size() > 5 )
							stations = stations.subList( 0, 5 );

						filterResults.values = stations;
						filterResults.count = stations.size();
					} catch ( ApiException e ) {
						ErrorReporter.getInstance().handleSilentException( e );
					}
				}

				return filterResults;
			}

			@SuppressWarnings( "unchecked" )
			@Override
			protected void publishResults( CharSequence contraint, FilterResults results ) {
				data = (List<Station>) results.values;

				if ( results != null && results.count > 0 ) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

		};
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View vi = convertView;

		if ( convertView == null )
			vi = inflater.inflate( android.R.layout.simple_dropdown_item_1line, null );

		((TextView) vi.findViewById( android.R.id.text1 )).setText( data.get( position ).name );

		return vi;
	}

}
