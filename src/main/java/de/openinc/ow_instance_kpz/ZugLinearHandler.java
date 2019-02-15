package de.openinc.ow_instance_kpz;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.openinc.ow.core.api.OpenWareInstance;
import de.openinc.ow.core.data.model.OpenWareDataItem;
import de.openinc.ow.core.data.model.OpenWareValue;
import de.openinc.ow.core.data.model.valuetypes.OpenWareNumber;
import de.openinc.ow.core.data.model.valuetypes.OpenWareValueDimension;
import de.openinc.ow.core.helper.Config;
import de.openinc.ow.middleware.handler.DataHandler;
import de.openinc.ow.middleware.handler.DefaultDataHandler;
import de.openinc.ow.middleware.handler.TemplateDataHandler;

public class ZugLinearHandler implements DataHandler{

	private String idprefix;
	private DataHandler handler;
	private String user;
	private double[] value;
	private OpenWareDataItem item;
	public ZugLinearHandler(String id, String user){
		this.idprefix = id;
		this.user = user;
		this.handler = new DefaultDataHandler();
		this.value = null;
		this.item = handler.handleData(idprefix, Config.mapId(idprefix).toString()).get(0);
	}
	
	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		if(!id.startsWith(idprefix))return null;
		try{
			
			if (value==null) {
				value = new double[3];
				
				value[0]=Double.MAX_VALUE;
				value[1]=Double.MAX_VALUE;
				value[2]=Double.MAX_VALUE;
			}
			if(id.replace(idprefix+".", "").equals("x")) {
				value[0]= Double.valueOf(data);
			}
			if(id.replace(idprefix+".", "").equals("y")) {
				value[1]= Double.valueOf(data);
			}
			if(id.replace(idprefix+".", "").equals("z")) {
				value[2]= Double.valueOf(data);
			}
			if(value[0]!=Double.MAX_VALUE &&value[1]!=Double.MAX_VALUE && value[2]!=Double.MAX_VALUE) {
				ArrayList<OpenWareValue> vals= new ArrayList<>();
				OpenWareValue val = new OpenWareValue(new Date().getTime());
				val.addValueDimension(new OpenWareNumber(this.value[0]));
				val.addValueDimension(new OpenWareNumber(this.value[1]));
				val.addValueDimension(new OpenWareNumber(this.value[2]));
				vals.add(val);
				this.value = null;
				item.value(vals);
				item.setUser(user);
				ArrayList<OpenWareDataItem> list = new ArrayList<>();
				list.add(item);
				return list;	
			}else {
				return null;
			}
			
		}catch(JSONException e){
			int size = data.length();
			OpenWareInstance.getInstance().logDebug("ZugException: "+ e.getMessage() + ": " + data.substring(0,Math.min(size,100)));
			return null;
		}
		
	
	}

}
