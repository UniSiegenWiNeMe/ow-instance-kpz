package de.openinc.ow_instance_kpz;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import de.openinc.ow.OpenWareInstance;
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
