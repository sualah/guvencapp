package com.tr.guvencmakina.guvencapp.Products.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tr.guvencmakina.guvencapp.R;
import com.tr.guvencmakina.guvencapp.Utils.HttpConnection;
import com.tr.guvencmakina.guvencapp.Utils.PathJSONParser;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import es.dmoral.toasty.Toasty;

public class ProductLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {
	private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates
	LatLng agentLatLng;
	LatLng customerLatLng;
	Location location;
	private LocationRequest locationRequest;
	GoogleMap googleMap;
	final String TAG = "PathGoogleMapActivity";
	ArrayList markerPoints= new ArrayList();
	double customer_latitude = 0.0;
	double customer_longtitude = 0.0;
	Marker agentMarker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_product_location_map);
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		fm.getMapAsync(this);
		//googleMap = fm.getMap();

		customerLatLng =  new LatLng(customer_latitude,customer_longtitude);
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		locationRequest = LocationRequest.create();
	    locationRequest.setInterval(30000); // 30 second delay between each request
		locationRequest.setFastestInterval(30000); // 30 seconds fastest time in between each request
		locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		try {
			mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {

					location = locationResult.getLastLocation();
					agentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

					if(customer_latitude == 0.0 && customer_longtitude == 0.0){
						//Toasty.error(getApplicationContext(),getString(R.string.no_customer_location), Toast.LENGTH_SHORT).show();
					}
					updateUIFused(agentLatLng);

				}
			}, Looper.myLooper());

		} catch (SecurityException e) {
			e.printStackTrace();
		}


	}

	private String getMapsApiDirectionsUrl(LatLng orign, LatLng destination) {
		String str_origin = "origin=" + orign.latitude + "," + orign.longitude;

		// Destination of route
		String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
		String waypoints = "waypoints=optimize:true|";
		String sensor = "sensor=false";
		String mode = "mode=driving";
		String params = waypoints + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + mode;// waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params + "&key=" + getString(R.string.google_maps_key);
		return url;
	}

	private void addMarkers() {
		if (googleMap != null) {
			Marker agentMarker = googleMap.addMarker(new MarkerOptions().position(agentLatLng)
					.title("Agent")
			        .icon(getBitmapDescriptor(R.drawable.ic_location_on_red_400_24dp)));
                   agentMarker.showInfoWindow();
			//.icon(getBitmapDescriptor(R.drawable.posicon))
			Marker customerMarker = googleMap.addMarker(new MarkerOptions().position(customerLatLng)
					.title("Customer")
					.icon(getBitmapDescriptor(R.drawable.ic_person_pin_circle_red_400_24dp)));
                   //customerMarker.showInfoWindow();
// googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
//					.title("Third Point"));
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		try {
			googleMap = map;
			googleMap.setMyLocationEnabled(true);

		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private class ReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
				System.out.println("---------------------------- map routes " + jObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points =  new ArrayList<LatLng>();
			PolylineOptions polyLineOptions = new PolylineOptions();

			// traversing through routes
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);
				polyLineOptions.color(Color.BLUE);
			}
			if(polyLineOptions != null) {
				googleMap.addPolyline(polyLineOptions);
			}
			else {
				Log.d("onPostExecute","without Polylines drawn");
			}

		}
	}

	private BitmapDescriptor getBitmapDescriptor(int id) {
		Drawable vectorDrawable = getResources().getDrawable(id);
		//int h = ((int) Utils.convertDpToPixel(42, context));
		//int w = ((int) Utils.convertDpToPixel(25, context));
		vectorDrawable.setBounds(0, 0, 70, 70);
		Bitmap bm = Bitmap.createBitmap(70, 70, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
		vectorDrawable.draw(canvas);
		return BitmapDescriptorFactory.fromBitmap(bm);
	}
	private void updateUIFused(LatLng newlatLng) {
		LatLng newLocation = newlatLng;
		if (agentMarker != null) {
			animateAgent(newLocation);
			boolean contains = googleMap.getProjection()
					.getVisibleRegion()
					.latLngBounds
					.contains(newLocation);
			if (!contains) {
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
			}
		} else {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					newLocation, 12.0f));
			agentMarker = googleMap.addMarker(new MarkerOptions().position(newLocation)
					.title("Agent")
					.icon(getBitmapDescriptor(R.drawable.ic_location_on_red_400_24dp)));
			agentMarker.showInfoWindow();
			Marker customerMarker = googleMap.addMarker(new MarkerOptions().position(customerLatLng)
					.title("Customer")
					.icon(getBitmapDescriptor(R.drawable.ic_person_pin_circle_red_400_24dp)));

			//googleMap.addMarker(options);
			String url = getMapsApiDirectionsUrl(newLocation,customerLatLng);
			ReadTask downloadTask = new ReadTask();
			downloadTask.execute(url);
		}
	}
	private void animateAgent(final LatLng destination) {
		final LatLng startPosition = agentMarker.getPosition();
		final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
		final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

		ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.setDuration(5000); // duration 5 seconds
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				try {
					float v = animation.getAnimatedFraction();
					LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
					agentMarker.setPosition(newPosition);
				} catch (Exception ex) {
				}
			}
		});
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
			}
		});
		valueAnimator.start();
	}

	/*
        This interface defines the interpolate method that allows us to get LatLng coordinates for
        a location a fraction of the way between two points. It also utilizes a Linear method, so
        that paths are linear, as they should be in most streets.
     */
	private interface LatLngInterpolator {
		LatLng interpolate(float fraction, LatLng a, LatLng b);

		class LinearFixed implements LatLngInterpolator {
			@Override
			public LatLng interpolate(float fraction, LatLng a, LatLng b) {
				double lat = (b.latitude - a.latitude) * fraction + a.latitude;
				double lngDelta = b.longitude - a.longitude;
				if (Math.abs(lngDelta) > 180) {
					lngDelta -= Math.signum(lngDelta) * 360;
				}
				double lng = lngDelta * fraction + a.longitude;
				return new LatLng(lat, lng);
			}
		}
	}
}
