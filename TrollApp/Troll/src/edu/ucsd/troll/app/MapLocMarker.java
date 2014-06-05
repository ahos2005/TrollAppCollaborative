package edu.ucsd.troll.app;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MapLocMarker {
	
	String name;
	LatLng myLatLng;
	Marker myMarker;

	public MapLocMarker(String incName, LatLng incLatLong, Marker incMarker) {
		name = incName;
		myLatLng = incLatLong;
		myMarker = incMarker;
	}	
	public LatLng getLatLng() {
		return myLatLng;
	}
	public Marker getMarker() {
		return myMarker;
	}
	public String getName() {
		return name;
	}
	public void setLatLng(LatLng incLatLng) {
		myLatLng = incLatLng;
	}
	public void setMarker(Marker incMarker) {
		myMarker = incMarker;
	}
	public void setName(String incName) {
		name = incName;
	}
}
