package com.pickzen.tinzen.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.sql.*;
import io.prediction.EventClient;
import io.prediction.Event;
import io.prediction.EngineClient;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.pickzen.api.shared.exceptions.APIException;
import java.util.Random;


@Repository
public class TinzenRepository {
	
	private EventClient eventClient = new EventClient("tIcGA4UZYfJpTdl8UuDx8ZNT-1G1jvEF7sEDz8tis7gdeETvaEGwqinkZd97q10g");
	
	private EngineClient engineClient = new EngineClient("http://localhost:8000");
	
	Map<String, Object> emptyProperty = ImmutableMap.of();
	
	/** Creates event in which user uid view item itemID 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ExecutionException */
	public void sendViewEvent(String uid, String itemID) {
		try {
			Event viewEvent = new Event().event("view").entityType("user").entityId(uid).targetEntityType("item").targetEntityId(itemID);
			eventClient.createEvent(viewEvent);			
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	public void setUser(String uid) {
		try {
			Event userEvent = new Event().event("$set").entityType("user").entityId(uid);
			eventClient.createEvent(userEvent);
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	public void setItem(String itemID) {
		try {
			Event itemEvent = new Event().event("$set").entityType("item").entityId(itemID).property("categories", ImmutableList.of("c1"));
			eventClient.createEvent(itemEvent);
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	public JsonObject sendQuery(ArrayList<String> likedItems, int numRecs, ArrayList<String> blacklist) {
		try {
			JsonObject response = engineClient.sendQuery(ImmutableMap.<String, Object>of("items", ImmutableList.copyOf(likedItems), "num", numRecs, "blackList", ImmutableList.copyOf(blacklist)));
			return response;
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	public String getNewItem(int mid, ArrayList<String> blacklist) {
		try {
			System.out.println("entered repository");
			Connection conn = DriverManager.getConnection("jdbc:mysql://pickzen-www-db.cokbeskpvwsa.eu-west-1.rds.amazonaws.com/ms_tinder?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "ms_tinder", "ms_tinder***");
			Statement stmt = conn.createStatement();
			System.out.println("made sql connection and statement objects");
			Random rand = new Random();
			int bucketID = rand.nextInt(10);
			int bucket;
			if (bucketID <=5) {
				bucket = 1;
			} else if (bucketID <= 8) {
				bucket = 2;
			} else {
				bucket = 3;
			}
			System.out.println("got bucket");
			String sizeQuery = "SELECT size FROM bucket WHERE id = " + Integer.toString(bucket) + " AND model = " + Integer.toString(mid);
			ResultSet sizeResult = stmt.executeQuery(sizeQuery);
			sizeResult.next();
			int size = sizeResult.getInt("size");
			
			//Assume items will be indexed starting at 0
			blacklist.add("starter");
			String sku = "starter";
			int loops = 0;
			while ((blacklist.contains(sku)) && (loops < 3) ) {
				int itemIndex = rand.nextInt(size);
				String itemQuery = "SELECT sku from item_bucket where model = " + Integer.toString(mid) + " AND bucket = " + Integer.toString(bucket) + " AND `index` = " + Integer.toString(itemIndex);
				System.out.println(itemQuery);
				ResultSet itemResult = stmt.executeQuery(itemQuery);
				itemResult.next();
				sku = itemResult.getString("sku");
				loops = loops + 1;
			}
			if (blacklist.contains(sku)) {
				sku = "No random";
			}
			
			System.out.println("SKU: " + sku);
			return sku;
		} catch (SQLException e) {
			throw new APIException("db", e);
		}
	}
}
