package com.android.photography.webservice;

import java.util.List;

public class VenuesList {

	public List<Venue> venues;

	public List<Venue> getVenues() {
		return venues;
	}

	public void setVenues(List<Venue> venues) {
		this.venues = venues;
	}

	public String[] getListOfVenues() {
		String[] arrayOfVenues = new String[venues.size()];

		for (int i = 0; i < venues.size(); i++) {
			arrayOfVenues[i] = venues.get(i).getName();
		}

		return arrayOfVenues;
	}
}
