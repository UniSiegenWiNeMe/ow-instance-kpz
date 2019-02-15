package de.openinc.ow_instance_kpz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.openinc.ow.core.data.model.OpenWareContainerItem;
import de.openinc.ow.core.data.model.OpenWareDataItem;
import de.openinc.ow.core.data.model.OpenWareValue;
import de.openinc.ow.core.data.model.valuetypes.OpenWareGeo;
import de.openinc.ow.core.data.model.valuetypes.OpenWareNumber;
import de.openinc.ow.middleware.handler.DataHandler;

public class OwnTracksDataHandler implements DataHandler {

	private String prefix;
	private ArrayList<String> locationNames;
	private ArrayList<String> locationUnits;
	private ArrayList<String> batteryNames;
	private ArrayList<String> batteryUnits;
	public OwnTracksDataHandler(String topicPrefix) {
		this.prefix = topicPrefix;
		this.locationNames = new ArrayList<>();
		locationNames.add("Standort");
		this.locationUnits = new ArrayList<>();
		locationUnits.add("");
		
		this.batteryNames = new ArrayList<>();
		batteryNames.add("Prozent Akku-Ladestand");
		this.batteryUnits = new ArrayList<>();
		batteryUnits.add("%");
	}
	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		if(!id.startsWith(prefix))return null;
		
		ArrayList<OpenWareDataItem> res = new ArrayList<>();
		JSONObject mData = new JSONObject(data);
		
		
		String user =id.replace(prefix,""); 
		long ts = mData.getLong("tst")*1000l;
		
		if(mData.getString("_type").equals("location")) {
			double lat = mData.getDouble("lat");
			double lon = mData.getDouble("lon");
			
			JSONObject geoJSON = new JSONObject();
			JSONObject prop = new JSONObject();
			JSONObject geom = new JSONObject();
			
			geom.put("type", "Point");
			JSONArray coord = new JSONArray();
			coord.put(lon);
			coord.put(lat);
			geom.put("coordinates", coord);
			
			prop.put("start", ts);
			prop.put("timestamp", ts);
			
			geoJSON.put("type", "Feature");
			geoJSON.put("properties", prop);
			geoJSON.put("geometry", geom);
			
			OpenWareDataItem item = new OpenWareDataItem("owntracks.location",user, "Standort", "", new JSONObject());
			item.valueNames(this.locationNames);
			item.units(this.locationUnits);
			OpenWareValue val = new OpenWareValue(ts);
			val.addValueDimension(new OpenWareGeo(geoJSON));
			ArrayList<OpenWareValue> vals = new ArrayList<>();
			vals.add(val);
			item.value(vals);
			
			res.add(item);
		}

		if(mData.has("batt")) {
			double battery = mData.getDouble("batt");
			OpenWareDataItem item = new OpenWareDataItem("owntracks.battery",user, "Ladezustand", "", new JSONObject());
			item.valueNames(this.batteryNames);
			item.units(this.batteryUnits);
			OpenWareValue val = new OpenWareValue(ts);
			val.addValueDimension(new OpenWareNumber(battery));
			ArrayList<OpenWareValue> vals = new ArrayList<>();
			vals.add(val);
			item.value(vals);
			
			res.add(item);
		}
		return res;
		
	}

}
