package de.openinc.ow_instance_kpz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.openinc.api.DataHandler;
import de.openinc.model.data.OpenWareDataItem;
import de.openinc.model.data.OpenWareValue;
import de.openinc.model.data.OpenWareValueDimension;

public class OwnTracksDataHandler implements DataHandler {

	private String prefix;
	private ArrayList<String> locationNames;
	private ArrayList<String> locationUnits;
	private ArrayList<String> batteryNames;
	private ArrayList<String> batteryUnits;
	private ArrayList<OpenWareValueDimension> valuetypesGeo;
	private ArrayList<OpenWareValueDimension> valuetypesBatt;
	private OpenWareDataItem locItem;
	private OpenWareDataItem battItem;

	public OwnTracksDataHandler(String topicPrefix) {
		this.prefix = topicPrefix;
		this.valuetypesGeo = new ArrayList<OpenWareValueDimension>();
		this.valuetypesBatt = new ArrayList<OpenWareValueDimension>();
		valuetypesGeo.add(OpenWareValueDimension.createNewDimension("Standort", "", "geo"));
		valuetypesBatt.add(OpenWareValueDimension.createNewDimension("Prozent Akku-Ladestand", "%", "number"));
		locItem = new OpenWareDataItem("owntracks.location", "toSet", "Standort", new JSONObject(), valuetypesGeo);
		battItem = new OpenWareDataItem("owntracks.battery", "toSet", "Ladezustand", new JSONObject(), valuetypesBatt);
	}

	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		if (!id.startsWith(prefix))
			return null;

		ArrayList<OpenWareDataItem> res = new ArrayList<>();
		JSONObject mData = new JSONObject(data);

		String user = id.replace(prefix, "");
		locItem.setUser(user);
		long ts = mData.getLong("tst") * 1000l;

		if (mData.getString("_type").equals("location")) {
			try {
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

			OpenWareValue val = new OpenWareValue(ts);
			
				val.addValueDimension(locItem.getValueTypes().get(0).createValueForDimension(geoJSON));
				ArrayList<OpenWareValue> vals = new ArrayList<>();
				vals.add(val);
				locItem.value(vals);

				res.add(locItem);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		if (mData.has("batt")) {
			try {
				double battery = mData.getDouble("batt");
				battItem.setUser(user);
				OpenWareValue val = new OpenWareValue(ts);
				val.addValueDimension(battItem.getValueTypes().get(0).createValueForDimension((battery)));
				ArrayList<OpenWareValue> vals = new ArrayList<>();
				vals.add(val);
				battItem.value(vals);

				res.add(battItem);	
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		return res;

	}

	@Override
	public boolean setOptions(JSONObject options) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
