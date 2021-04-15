package de.openinc.ow_instance_kpz;

import java.util.List;

import org.json.JSONObject;

import de.openinc.api.DataHandler;
import de.openinc.model.data.OpenWareDataItem;
import de.openinc.ow.helper.Config;
import de.openinc.owee.datahandler.DefaultDataHandler;

public class ZugNumberHandler implements DataHandler {

	private String idprefix;
	private DataHandler handler;
	private String user;
	private OpenWareDataItem item;

	public ZugNumberHandler(String id, String user) {
		this.idprefix = id;
		this.user = user;
		this.handler = new DefaultDataHandler();
		try {
			this.item = handler.handleData(idprefix, Config.mapId(idprefix).toString()).get(0);
		} catch (Exception e) {
			this.item = null;
		}
	}

	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		/*
		if(!id.startsWith(idprefix))return null;
		try{
				ArrayList<OpenWareValue> vals= new ArrayList<>();
				OpenWareValue val = new OpenWareValue(new Date().getTime());
				double value= Double.valueOf(data);
				if(idprefix.equals("sensor.zug.battery.percentage")) {
					value *= 100;
				}
				val.addValueDimension(new OpenWareNumber(value));
				vals.add(val);
				item.value(vals);
				if(user!=null)item.setUser(user);
				ArrayList<OpenWareDataItem> list = new ArrayList<>();
				list.add(item);
				return list;	
			
		}catch(JSONException e){
			int size = data.length();
			OpenWareInstance.getInstance().logDebug("ZugException: "+ e.getMessage() + ": " + data.substring(0,Math.min(size,100)));
			return null;
		}
		
		*/
		return null;
	}

	@Override
	public boolean setOptions(JSONObject options) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
