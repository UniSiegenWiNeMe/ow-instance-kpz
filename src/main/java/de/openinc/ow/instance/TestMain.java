package de.openinc.ow.instance;

import java.io.IOException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import de.openinc.model.data.OpenWareDataItem;
import de.openinc.model.data.OpenWareGeneric;
import de.openinc.model.data.OpenWareValue;
import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;

public class TestMain {

	public static void main(String[] args) {
		OpenWareDataItem item = new OpenWareDataItem("asd", "asd", "name", new JSONObject(),
				Arrays.asList(OpenWareGeneric.createNewDimension("generic", "", OpenWareGeneric.TYPE)));

		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		array.put("192.169.178.1");
		array.put("192.169.178.2");
		array.put("192.169.\"178.3");
		array.put("192.169.178.4");
		data.put("payload", array);

		item.value()
			.add(OpenWareValue	.build()
								.generic(data, "test")
								.get());

		item.value()
			.add(OpenWareValue	.build()
								.generic(data, "test")
								.get());

		item.value()
			.add(OpenWareValue	.build()
								.generic(data, "test")
								.get());

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			item.streamPrint(out);

			String res = new String(out.toByteArray());
			System.out.println(res);

			JSONObject obj = new JSONObject(res);
			System.out.println(obj.toString(2));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
