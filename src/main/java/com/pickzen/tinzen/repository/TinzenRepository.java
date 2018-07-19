package com.pickzen.tinzen.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import io.prediction.EventClient;
import io.prediction.Event;
import io.prediction.EngineClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
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
	
	
	public void setItem(String itemID, ArrayList<String> categories) {
		try {
			Event itemEvent = new Event().event("$set").entityType("item").entityId(itemID).property("categories", ImmutableList.copyOf(categories));
			eventClient.createEvent(itemEvent);
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	
	public JsonObject sendQuery(ArrayList<String> likedItems, int numRecs, ArrayList<String> blacklist, ArrayList<String> categories) {
		try {
			JsonObject response = engineClient.sendQuery(ImmutableMap.<String, Object>of("items", ImmutableList.copyOf(likedItems), "num", numRecs, "categories", ImmutableList.copyOf(categories), "blackList", ImmutableList.copyOf(blacklist)));
			return response;
		} catch (Throwable e) {
			throw new APIException("pio", e);
		}
	}
	
	
	public String getNewItem(int mid, ArrayList<String> blacklist) {
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
		
		int size = jdbcTemplate.queryForObject(
                "SELECT size FROM bucket WHERE id = ? AND model = ?", 
                new Object[] { Integer.toString(bucket), Integer.toString(mid)}, 
                (rs, rowNum) -> rs.getInt("size"));
		
		//Assume items will be indexed starting at 0
		blacklist.add("starter");
		String sku = "starter";
		int loops = 0;
		while ((blacklist.contains(sku)) && (loops < 3) ) {
			int itemIndex = rand.nextInt(size);
			
			sku = jdbcTemplate.queryForObject(
	                "SELECT sku FROM item_bucket WHERE model = ? AND bucket = ? AND `index` = ?", 
	                new Object[] { Integer.toString(mid), Integer.toString(bucket), Integer.toString(itemIndex)}, 
	                (rs, rowNum) -> rs.getString("sku"));
			loops = loops + 1;
		}
		if (blacklist.contains(sku)) {
			sku = "No random";
		}
		System.out.println(sku);
		return sku;
	}
	
	
	public int getRandomRate(int mid, int eid) {
		return jdbcTemplate.queryForObject(
                "SELECT random_rate FROM model WHERE id = ? AND ecommerce = ?", 
                new Object[] { Integer.toString(mid), Integer.toString(eid)}, 
                (rs, rowNum) -> rs.getInt("random_rate"));
	}
	
	public ArrayList<String> getSkus(int eid) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://pickzen-www-db.cokbeskpvwsa.eu-west-1.rds.amazonaws.com/ms_tinder?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "ms_tinder", "ms_tinder***");
		Statement stmt = conn.createStatement();
		String randQuery = "SELECT sku FROM products WHERE ecommerce= " + Integer.toString(eid);
		ResultSet result = stmt.executeQuery(randQuery);
		ArrayList<String> skus = new ArrayList<String>();
		while(result.next()) {
			skus.add(result.getString("sku"));
		}
//		randResult.next();
//		int rand_rate = randResult.getInt("random_rate");
//		return rand_rate;
//		ArrayList<String> skus = (ArrayList<String>) jdbcTemplate.queryForObject("SELECT sku FROM products WHERE ecommerce = ?", 
//				new Object[] {Integer.toString(eid)},
//				(rs, rowNum) -> console.log(rs); rs.getArray("sku")); 
		return skus;
	}
}
