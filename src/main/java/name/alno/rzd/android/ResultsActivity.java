package name.alno.rzd.android;

import java.util.List;
import java.util.SortedMap;

import name.alno.rzd.api.ApiException;
import name.alno.rzd.api.TicketGroup;
import name.alno.rzd.api.TicketSearchService;
import name.alno.rzd.api.Train;

import org.acra.ErrorReporter;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.ExpandableListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity( R.layout.results )
public class ResultsActivity extends Activity {

	@ViewById( R.id.resultsList )
	protected ExpandableListView resultsList;

	private Dialog dialog;

	@Override
	public void onPause() {
		if ( dialog != null ) {
			dialog.dismiss();
			dialog = null;
		}

		super.onPause();
	}

	@Override
	public void onStop() {
		if ( dialog != null ) {
			dialog.dismiss();
			dialog = null;
		}

		super.onStop();
	}

	@AfterViews
	protected void initViews() {
		ProgressDialog loadingDialog = new ProgressDialog( this );
		loadingDialog.setMessage( getResources().getText( R.string.loading_results ) );
		loadingDialog.setCancelable( false );
		loadingDialog.show();

		dialog = loadingDialog;

		startLoading();
	}

	@Background
	protected void startLoading() {
		TicketSearchService service = new TicketSearchService( new DefaultHttpClient() );

		try {
			SortedMap<Train, List<TicketGroup>> results = service.findTickets( getIntentDate(), getIntent().getExtras().getString( "sourceName" ),
					getIntent().getExtras().getLong( "sourceId" ), getIntent().getExtras().getString( "destinationName" ), getIntent().getExtras().getLong( "destinationId" ) );

			completeLoading( results );
		} catch ( ApiException e ) {
			ErrorReporter.getInstance().handleSilentException( e );
			completeWithError( e );
		}
	}

	@UiThread
	protected void completeLoading( SortedMap<Train, List<TicketGroup>> results ) {
		dialog.dismiss();
		dialog = null;

		if ( results == null ) {
			dialog = new AlertDialog.Builder( this ).setTitle( R.string.bad_request ).setCancelable( true ).setOnCancelListener( createFinishCancelListener() ).create();
			dialog.show();
		} else if ( results.isEmpty() ) {
			dialog = new AlertDialog.Builder( this ).setTitle( R.string.nothing_found ).setCancelable( true ).setOnCancelListener( createFinishCancelListener() ).create();
			dialog.show();
		} else {
			resultsList.setAdapter( new ResultsAdapter( this, results ) );
		}
	}

	@UiThread
	protected void completeWithError( ApiException e ) {
		dialog.dismiss();
		dialog = new AlertDialog.Builder( this ).setTitle( R.string.app_error ).setCancelable( true ).setOnCancelListener( createFinishCancelListener() ).create();
		dialog.show();
	}

	protected OnCancelListener createFinishCancelListener() {
		return new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel( DialogInterface dialog ) {
				finish();
			}

		};
	}

	private String getIntentDate() {
		StringBuilder b = new StringBuilder();
		if ( getIntent().getExtras().getInt( "dateDay" ) < 10 )
			b.append( '0' );
		b.append( String.valueOf( getIntent().getExtras().getInt( "dateDay" ) ) );
		b.append( '.' );
		if ( getIntent().getExtras().getInt( "dateMonth" ) < 9 )
			b.append( '0' );
		b.append( String.valueOf( getIntent().getExtras().getInt( "dateMonth" ) + 1 ) );
		b.append( '.' );
		b.append( String.valueOf( getIntent().getExtras().getInt( "dateYear" ) ) );

		return b.toString();
	}

}
