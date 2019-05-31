package de.openinc.ow_instance_kpz;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import de.openinc.ow.analytics.providers.DescriptiveAnalyticsProvider;
import de.openinc.ow.analytics.providers.DiffValueProvider;
import de.openinc.ow.analytics.providers.FrequencyAnalyticsProvider;
import de.openinc.ow.core.analytics.SensorProvider.ParseAnalyticSensorProvider;
import de.openinc.ow.core.api.AdminAPI;
import de.openinc.ow.core.api.MiddlewareApi;
import de.openinc.ow.core.api.OpenWareInstance;
import de.openinc.ow.core.api.analytics.AnalyticsService;
import de.openinc.ow.core.api.analytics.AnalyticsServiceAPI;
import de.openinc.ow.core.api.events.AlarmService;
import de.openinc.ow.core.data.DataService;
import de.openinc.ow.core.data.DataSubscriber;
import de.openinc.ow.core.data.DefaultPersistenceAdapter;
import de.openinc.ow.core.data.PersistenceAdapter;
import de.openinc.ow.core.data.model.OpenWareDataItem;
import de.openinc.ow.core.helper.Config;
import de.openinc.ow.core.opcua.OPCUAServer;
import de.openinc.ow.core.user.ParseUserAdapter;
import de.openinc.ow.core.user.UserAPI;
import de.openinc.ow.core.user.UserService;
import de.openinc.ow.middleware.handler.DefaultDataHandler;
import de.openinc.ow.middleware.sender.RabbitMQSender;
import de.openinc.ow.rabbitmq.RabbitMQConnection;
import de.openinc.ow.reporting.DescriptivesReport;
import de.openinc.ow.reporting.ReportsAPI;
import de.openinc.ow_instance_openinc.IoTGatewayHandler;

/**
 * This Main class is the starting point of the server it includes: server
 * configuration APIs registration
 */
public class MyInstance {

	public static void main(String[] args) throws IOException, TimeoutException {

		OpenWareInstance.getInstance();

		// ----------------------------------------- Services
		// -------------------------------------------------------------------------------------
		// User
		UserService userService = UserService.getInstance();
		userService.setAdapter(new ParseUserAdapter());

		// Data
		DataService.init(Config.mappingsCollection);
		PersistenceAdapter mongo = new DefaultPersistenceAdapter();
		DataService.setPersistenceAdapter(mongo);

		// Analytics
		// AnalyticsService.getInstance().setSensorProvider(new
		// JSONAnalyticsSensorProvider("analytics.json"));
		AnalyticsService.getInstance().setSensorProvider(new ParseAnalyticSensorProvider());
		AnalyticsService.getInstance().registerAnalyticsProvider(DescriptiveAnalyticsProvider.oid,
				new DescriptiveAnalyticsProvider());
		AnalyticsService.getInstance().registerAnalyticsProvider(FrequencyAnalyticsProvider.oid,
				new FrequencyAnalyticsProvider());
		AnalyticsService.getInstance().registerAnalyticsProvider(DiffValueProvider.oid, new DiffValueProvider());
		//AnalyticsService.getInstance().registerAnalyticsProvider(STLProvider.oid, new STLProvider());

		// ----------------------------------------- OPC-UA-Server
		// -------------------------------------------------------------------------------------
		OPCUAServer opc = new OPCUAServer();
		opc.start();

		// ----------------------------------------- APIs
		// -------------------------------------------------------------------------------------

		// Middleware Data API
		MiddlewareApi middlewareApi = new MiddlewareApi();

		// UserManagement API
		UserAPI userAPI = new UserAPI();

		// AnalyticsService API
		AnalyticsServiceAPI asa = new AnalyticsServiceAPI();

		// Report API
		ReportsAPI reports = new ReportsAPI("/report");
		reports.addReportType(DescriptivesReport.TAG, new DescriptivesReport());
		// Admin API
		AdminAPI adminApi = new AdminAPI();

		// Alarm & Event API
		AlarmService as = AlarmService.getInstance();

		OpenWareInstance.registerService(middlewareApi);
		OpenWareInstance.registerService(userAPI);
		OpenWareInstance.registerService(asa);
		OpenWareInstance.registerService(reports);
		OpenWareInstance.registerService(adminApi);
		OpenWareInstance.registerService(as);

		OpenWareInstance.getInstance().startInstance();

		// ----------------------------------------- DataHandler
		// -------------------------------------------------------------------------------------
		// Default Data
		DataService.addHandler(new IoTGatewayHandler("si-"));
		DataService.addHandler(new DefaultDataHandler());
		DataService.addHandler(new FabLabHandler(".fablab"));

		// Owntracks Handler
		OwnTracksDataHandler oth = new OwnTracksDataHandler("owntracks.");
		DataService.addHandler(oth);

		/*
		// ZUG Linear
		DataService.addHandler(new ZugLinearHandler("sensor.zug.LinearAcceleration", "test@zug40.de"));
		// ZUG Einzelne Number Values
		
		DataService.addHandler(new ZugNumberHandler("sensor.zug.LightIntensity.x", "test@zug40.de"));
		DataService.addHandler(new ZugNumberHandler("sensor.zug.speed", "test@zug40.de"));
		DataService.addHandler(new ZugNumberHandler("sensor.zug.battery.percentage", "test@zug40.de"));
		DataService.addHandler(new ZugNumberHandler("sensor.zug.noise.decibels", "test@zug40.de"));
		DataService.addHandler(new ZugStringHandler("sensor.zug.rfid", "test@zug40.de"));
		*/
		// ----------------------------------------- DataPublisher
		// -------------------------------------------------------------------------------------
		if (Config.publishParsedData) {
			RabbitMQSender sender = new RabbitMQSender(Config.rmqPath, "owcore.data");
			DataService.addSubscription(new DataSubscriber() {
				public void receive(OpenWareDataItem item) throws Exception {
					String topic = item.getUser().replaceAll("\\.", "") + "." + item.getId();
					sender.send(item.toString(), topic);
				}
			});
		}

		// ----------------------------------------- DataConsumer
		// -------------------------------------------------------------------------------------

		RabbitMQConnection defaultRabbit = new RabbitMQConnection();
		defaultRabbit.consumeDefault();

		/*
		//Fake Rüst Daten
		while (true) {
			long ts = new Date().getTime();
			System.out.println("Storing fake Data");
			String s = "{\"id\":\"data.test.ruestie\",\"user\":\"fit2\",\"parent\":[],\"meta\":{},\"name\":\"Maschine_4711_Rüstenprozess\",\"icon\":\"\",\"valueTypes\":[{\"unit\":\"#\",\"name\":\"Ruestschritt\",\"type\":\"Number\"},{\"unit\":\"ms\",\"name\":\"Schrittdauer\",\"type\":\"Number\"},{\"unit\":\"\",\"name\":\"Rüstvorgangsid\",\"type\":\"String\"},{\"unit\":\"\",\"name\":\"Kommentar\",\"type\":\"String\"}]}";
			JSONObject obj = new JSONObject(s);
			int[] bases = new int[] { 60, 120, 50, 80, 90, 110, 180, 20, 75 };
			int[] adds = new int[] { 10, 30, 5, 10, 8, 40, 100, 1, 10 };
			String source = "fit.rustie" + "Session" + ts;
			byte[] bytes = source.getBytes("UTF-8");
			UUID uuid = UUID.nameUUIDFromBytes(bytes);
			String session = uuid.toString();
			JSONArray values = new JSONArray();
			for (int i = 0; i < bases.length; i++) {
				double rand1 = Math.random();
				double rand2 = Math.random();
				double plusminus = 1;
				if (rand2 < 0.5) {
					plusminus = -1;
				}
				long duration = (long) ((bases[i] * 1000l) + rand1 * (adds[i] * 1000 * plusminus));
				JSONObject value = new JSONObject();
				value.put("date", ts);
				JSONArray cVal = new JSONArray();
				cVal.put(i + 1);
				cVal.put(duration);
				cVal.put(session);
				cVal.put("");
				value.put("value", cVal);
				values.put(value);
				obj.put("values", values);
				DataService.onNewData("test", obj.toString());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		 	
		}
		*/
	}// main

}// class
