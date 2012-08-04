package name.alno.rzd.android;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes( formKey = "dGhGSXAwdWxZVm85RE9FR1BGOFBjMnc6MQ" )
public class RzdApplication extends Application {

	@Override
	public void onCreate() {
		ACRA.init( this );
		super.onCreate();
	}
}
