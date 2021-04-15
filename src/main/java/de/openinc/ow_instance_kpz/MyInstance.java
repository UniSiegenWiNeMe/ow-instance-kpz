package de.openinc.ow_instance_kpz;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import de.openinc.ow.OpenWareInstance;
import de.openinc.ow.middleware.services.DataService;
import de.openinc.owee.opcua.OPCUAServer;

/**
 * This Main class is the starting point of the server it includes: server
 * configuration APIs registration
 */
public class MyInstance {

	public static void main(String[] args) throws IOException, TimeoutException {

		OpenWareInstance.getInstance();

		// ----------------------------------------- OPC-UA-Server
		// -------------------------------------------------------------------------------------
		OPCUAServer opc = new OPCUAServer();

		// ----------------------------------------- APIs
		// -------------------------------------------------------------------------------------

		OpenWareInstance.getInstance().startInstance();

		//SpeedTest
		
		/*
		//Fake Ruest Daten
		long curr  = System.currentTimeMillis();
		//while (System.currentTimeMillis()-curr<10000) {
		while (true) {
			long ts = System.currentTimeMillis();
			String s2 = "{\"id\":\"data.test.ruestie2\",\"user\":\"fit2\",\"parent\":[],\"meta\":{},\"name\":\"Maschine_4711_R�stenprozess\",\"icon\":\"\",\"valueTypes\":[{\"unit\":\"#\",\"name\":\"Ruestschritt\",\"type\":\"Number\"}]}";
			//String s = "{\"id\":\"data.test.ruestie2\",\"user\":\"fit2\",\"parent\":[],\"meta\":{},\"name\":\"Maschine_4711_R�stenprozess\",\"icon\":\"\",\"valueTypes\":[{\"unit\":\"#\",\"name\":\"Ruestschritt\",\"type\":\"Number\"},{\"unit\":\"ms\",\"name\":\"Schrittdauer\",\"type\":\"Number\"},{\"unit\":\"\",\"name\":\"Ruestvorgangsid\",\"type\":\"String\"},{\"unit\":\"\",\"name\":\"Kommentar\",\"type\":\"String\"}]}";
			JSONObject obj = new JSONObject(s2);
			
			int[] bases = new int[] { 60, 120, 50, 80, 90, 110, 180, 20, 75 };
			int[] adds = new int[] { 10, 30, 5, 10, 8, 40, 100, 1, 10 };
			String source = "fit.rustie" + "Session" + ts;
			byte[] bytes = source.getBytes("UTF-8");
			UUID uuid = UUID.nameUUIDFromBytes(bytes);
			String session = uuid.toString();
			for (int i = 0; i < 30; i++) {
				JSONArray values = new JSONArray();
				double rand1 = Math.random();
				double rand2 = Math.random();
				double plusminus = 1;
				if (rand2 < 0.5) {
					plusminus = -1;
				}
				long ts2 = System.currentTimeMillis();
				//long duration = (long) ((bases[i] * 1000l) + rand1 * (adds[i] * 1000 * plusminus));
				JSONObject value = new JSONObject();
				value.put("date", ts2);
				JSONArray cVal = new JSONArray();
				cVal.put(i + 1);
				//cVal.put(duration);
				//cVal.put(session);
				//cVal.put("");
				value.put("value", cVal);
				values.put(value);
				obj.put("values", values);
				
				if(i%2==0) {
					obj.put("reference", "test");
				}else {
					obj.remove("reference");
				}
				
				obj.put("id", "data.test.ruestie."+0);
				Future<Boolean> saved=  DataService.onNewData("test", obj.toString());
				
				try {
					saved.get();
					//System.out.println("Saved:"+ );
					//Thread.sleep(10);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		 	
		}
		
		*/
		
		// ----------------------------------------- DataPublisher
		// -------------------------------------------------------------------------------------

		/*
				ConsumptionReport cr = new ConsumptionReport();
				System.out.println(options);
				cr.init(new JSONObject(options), UserService.getInstance().getUserByUsername("mst206ster@googlemail.com"));
		*/
		/*
		
		ReferenceAdapter dra = new DefaultReferenceAdapter();
		dra.init();
			List<OpenWareDataItem> items = dra.getLastItemsForReference("AU-1575208800000",
				UserService.getInstance().getUser("mst206ster@googlemail.com"));
		
		for (OpenWareDataItem item : items) {
			System.out.println(item);
		}
		*/

		// ----------------------------------------- DataConsumer
		// -------------------------------------------------------------------------------------

		//
		//		JSONObject options = new JSONObject();
		//		options.put("name", "Test1Min");
		//		options.put("source", "si-1371-000");
		//		options.put("sensorid", "iot.si-1371-000.col_7.Wirkleistung_L1_L3_UMG_96_2");
		//		options.put("operation", "mean");
		//		options.put("dimension", 0);
		//
		//		AggregationJob ajob = new AggregationJob(null, UserService.getInstance().getUser("vettertablet@krantechnik.de"),
		//				"10secTest", AggregationJob.AGGREGATION_GROUP_TAG, "0 * * * * ?");
		//		ajob.setOptions(options);
		//		SchedulerService.getInstance().schedule(ajob);

	}// main

}// class
