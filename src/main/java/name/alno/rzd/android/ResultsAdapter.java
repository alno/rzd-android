package name.alno.rzd.android;

import java.util.List;
import java.util.SortedMap;

import name.alno.rzd.api.TicketGroup;
import name.alno.rzd.api.Train;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ResultsAdapter extends BaseExpandableListAdapter {

	private final LayoutInflater inflater;
	private final Train[] trains;
	private final TicketGroup[][] tickets;

	public ResultsAdapter( Context context, SortedMap<Train, List<TicketGroup>> data ) {
		this.inflater = LayoutInflater.from( context );
		this.trains = data.keySet().toArray( new Train[data.size()] );
		this.tickets = new TicketGroup[trains.length][];

		for ( int i = 0; i < trains.length; ++i ) {
			List<TicketGroup> l = data.get( trains[i] );
			tickets[i] = l.toArray( new TicketGroup[l.size()] );
		}
	}

	@Override
	public int getGroupCount() {
		return trains.length;
	}

	@Override
	public int getChildrenCount( int i ) {
		return tickets[i].length;
	}

	@Override
	public Object getGroup( int i ) {
		return trains[i];
	}

	@Override
	public Object getChild( int i, int j ) {
		return tickets[i][j];
	}

	@Override
	public long getGroupId( int i ) {
		return i;
	}

	@Override
	public long getChildId( int i, int j ) {
		return i * 100 + j;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView( int i, boolean paramBoolean, View convertView, ViewGroup paramViewGroup ) {
		Train train = trains[i];
		View vi = convertView;

		if ( convertView == null )
			vi = inflater.inflate( android.R.layout.simple_expandable_list_item_2, null );

		((TextView) vi.findViewById( android.R.id.text1 )).setText( train.departure + " - " + train.arrival );
		((TextView) vi.findViewById( android.R.id.text2 )).setText( train.num + " " + train.source + " - " + train.destination );

		return vi;
	}

	@Override
	public View getChildView( int i, int j, boolean paramBoolean, View convertView, ViewGroup paramViewGroup ) {
		TicketGroup tg = tickets[i][j];
		View vi = convertView;
		Resources res = inflater.getContext().getResources();

		if ( convertView == null )
			vi = inflater.inflate( android.R.layout.simple_expandable_list_item_2, null );

		((TextView) vi.findViewById( android.R.id.text1 )).setText( tg.price + " " + res.getText( R.string.of_rubles ).toString() + ", "
				+ res.getText( R.string.ticket_amount ).toString() + " " + tg.count );
		((TextView) vi.findViewById( android.R.id.text2 )).setText( tg.type );

		return vi;
	}

	@Override
	public boolean isChildSelectable( int i, int j ) {
		return false;
	}

}
