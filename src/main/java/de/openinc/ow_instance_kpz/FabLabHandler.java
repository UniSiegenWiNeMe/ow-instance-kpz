package de.openinc.ow_instance_kpz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.openinc.ow.core.data.model.OpenWareDataItem;
import de.openinc.ow.core.data.model.OpenWareValue;
import de.openinc.ow.core.data.model.valuetypes.OpenWareValueDimension;
import de.openinc.ow.middleware.handler.DataHandler;

public class FabLabHandler implements DataHandler {

	private String prefix;
	JSONObject template;
	HashMap<String, String> unitMapping;

	public FabLabHandler(String prefix) throws FileNotFoundException, IOException {
		this.prefix = prefix;
		this.unitMapping = new HashMap<String, String>();
		unitMapping.put("sds_p1", "ppm");
		unitMapping.put("sds_p2", "ppm");
		unitMapping.put("humidity", "%");
		unitMapping.put("max_micro", "ppm");
		unitMapping.put("min_micro", "ppm");
		unitMapping.put("signal", "db");
		unitMapping.put("temperature", "°C");
	}

	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		if (!id.startsWith(this.prefix) && !id.startsWith("/" + this.prefix)) {
			return null;
		}
		String[] messageType = id.split("\\.");

		ArrayList<OpenWareDataItem> list = new ArrayList<>();
		JSONObject meta = new JSONObject();

		if (messageType[1].equals("Printers")) {

			JSONObject jData = new JSONObject(data);
			long res = jData.getLong("_timestamp");
			OpenWareValueDimension first = OpenWareValueDimension.createNewDimension("Temperature Actual", "°C",
					"number");
			OpenWareValueDimension second = OpenWareValueDimension.createNewDimension("Temperature Target", "°C",
					"number");
			ArrayList<OpenWareValueDimension> vTypes = new ArrayList<OpenWareValueDimension>();
			vTypes.add(first);
			vTypes.add(second);
			OpenWareDataItem item = new OpenWareDataItem(
					"fablab.sensor.status." + messageType[1] + "." + messageType[2] + "." + messageType[4] + "."
							+ messageType[3],
					"fablab-01", messageType[1] + "-" + messageType[2] + "-" + messageType[4] + "-" + messageType[3],
					meta, vTypes);

			OpenWareValue owValue = new OpenWareValue(res);
			owValue.addValueDimension(item.getValueTypes().get(0).createValueForDimension(jData.getDouble("actual")));
			owValue.addValueDimension(item.getValueTypes().get(0).createValueForDimension(jData.getDouble("target")));
			List<OpenWareValue> owValueList = new ArrayList<>();
			owValueList.add(owValue);
			item.value(owValueList);

			list.add(item);
			return list;
		} else if (messageType[1].equals("Feinstaub")) {

			long res = new Date().getTime();

			ArrayList<OpenWareValueDimension> vTypes = new ArrayList<OpenWareValueDimension>();
			OpenWareValueDimension owvd = OpenWareValueDimension.createNewDimension(messageType[2],
					this.unitMapping.getOrDefault(messageType[2].toLowerCase(), ""), "number");
			vTypes.add(owvd);
			OpenWareDataItem item = new OpenWareDataItem(
					"fablab.sensor.status." + messageType[1] + "." + messageType[2], "fablab-01",
					messageType[1] + "-" + messageType[2], meta, vTypes);

			OpenWareValue owValue = new OpenWareValue(res);
			OpenWareValueDimension owValueValue = item.getValueTypes().get(0)
					.createValueForDimension(Double.valueOf(data));
			owValue.addValueDimension(owValueValue);

			List<OpenWareValue> owValueList = new ArrayList<>();
			owValueList.add(owValue);
			item.value(owValueList);
			list.add(item);
			return list;
		} else {
			return null;
		}

	}

}
