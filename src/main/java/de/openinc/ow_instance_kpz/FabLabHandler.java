package de.openinc.ow_instance_kpz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;

import de.openinc.ow.core.data.model.OpenWareDataItem;
import de.openinc.ow.core.data.model.OpenWareValue;
import de.openinc.ow.core.data.model.valuetypes.OpenWareNumber;
import de.openinc.ow.core.data.model.valuetypes.OpenWareValueDimension;
import de.openinc.ow.middleware.handler.DataHandler;
import de.openinc.ow.middleware.sender.RabbitMQSender;

public class FabLabHandler implements DataHandler {

	private String prefix;
	JSONObject template;
	private Properties properties;
	private String HOST;
	private String user;
	private String pw;
	private String appid;
	private String session;
	private HashMap<String, String> headers;
	private HashMap<String, JSONObject> locations;
	private HashMap<String, HashMap<String, OpenWareDataItem>> mapping;
	private HashMap<String, HashMap<String, Integer>> statusMapping;
	private String lastTagConfiguration;
	private DateTimeFormatter format;
	private RabbitMQSender sender;

	public FabLabHandler(String prefix) throws FileNotFoundException, IOException {
		this.prefix = prefix;
		this.mapping = new HashMap<>();
		this.statusMapping = new HashMap<>();
		format = ISODateTimeFormat.dateTime();
		//requestMarketData();
		//sender = new RabbitMQSender(Config.rmqPath, Config.rmqExchange);
	}

	@Override
	public List<OpenWareDataItem> handleData(String id, String data) {
		if (!id.startsWith(this.prefix)) {
			return null;
		}
		String[] messageType = id.split("\\.");

		ArrayList<OpenWareDataItem> list = new ArrayList<>();
		JSONObject meta = new JSONObject();

		if (messageType[1].equals("Printers")) {
			OpenWareDataItem item = new OpenWareDataItem(
					"fablab.sensor.status." + messageType[1] + "." + messageType[2] + "." + messageType[4] + "."
							+ messageType[3],
					"fablab-01", messageType[1] + "-" + messageType[2] + "-" + messageType[4] + "-" + messageType[3],
					meta);
			JSONObject jData = new JSONObject(data);
			long res = jData.getLong("_timestamp");
			String valueNameOpt = "Temperature";
			OpenWareValue owValue = new OpenWareValue(res);
			OpenWareValueDimension owValueValue = new OpenWareNumber(jData.getDouble("actual"));
			OpenWareValueDimension owValueValueSecond = new OpenWareNumber(jData.getDouble("target"));
			owValue.addValueDimension(owValueValue);
			owValue.addValueDimension(owValueValueSecond);

			List<OpenWareValue> owValueList = new ArrayList<>();
			owValueList.add(owValue);
			item.value(owValueList);

			List<String> valueTypesListe = new ArrayList<>();
			valueTypesListe.add(valueNameOpt + "-Current");
			valueTypesListe.add(valueNameOpt + "-Target");
			item.valueNames(valueTypesListe);

			List<String> valueTypesListeUnit = new ArrayList<>();
			valueTypesListeUnit.add("C");
			valueTypesListeUnit.add("C");
			item.units(valueTypesListeUnit);

			list.add(item);
			return list;
		} else if (messageType[1].equals("Feinstaub")) {
			OpenWareDataItem item = new OpenWareDataItem(
					"fablab.sensor.status." + messageType[1] + "." + messageType[2], "fablab-01",
					messageType[1] + "-" + messageType[2], meta);

			long res = new Date().getTime();

			OpenWareValue owValue = new OpenWareValue(res);
			OpenWareValueDimension owValueValue = new OpenWareNumber(Double.valueOf(data));
			owValue.addValueDimension(owValueValue);

			List<OpenWareValue> owValueList = new ArrayList<>();
			owValueList.add(owValue);
			item.value(owValueList);

			List<String> valueTypesListe = new ArrayList<>();
			valueTypesListe.add(messageType[2]);
			item.valueNames(valueTypesListe);

			List<String> valueTypesListeUnit = new ArrayList<>();
			valueTypesListeUnit.add("C");
			item.units(valueTypesListeUnit);

			list.add(item);
			return list;
		} else {
			return null;
		}

	}

}
