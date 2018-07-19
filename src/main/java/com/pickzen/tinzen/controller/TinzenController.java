package com.pickzen.tinzen.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pickzen.tinzen.controller.beans.NewSessionBean;
import com.pickzen.tinzen.service.TinzenService;
import static com.pickzen.api.shared.Utils.stringToList;

@CrossOrigin
@RestController
@RequestMapping(value="tinzen")
public class TinzenController {
	private static final Logger log = LoggerFactory.getLogger(TinzenController.class);
	
	@Autowired
	private TinzenService tinzenService;	
	
	@RequestMapping(value="/new_session", method=RequestMethod.POST)
	public ResponseEntity<NewSessionBean> newSession() {
		log.debug("Tinzen: new_session");
		
		String sessionId = UUID.randomUUID().toString();
		//tinzenService.setUser(sessionId);
		
		return new ResponseEntity<>(new NewSessionBean(sessionId), HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/set_item/{iid}/{cat}", method=RequestMethod.POST)
	public void setItem(@PathVariable("iid") String iid, @PathVariable("cat") String cat) throws Exception {
		ArrayList<String> categories = stringToList(cat);
		tinzenService.setItem(iid, categories);		
	}
	
	
	@RequestMapping(value="/event/{uid}/{iid}", method=RequestMethod.POST)
	public void sendEvent(@PathVariable("uid") String uid, @PathVariable("iid") String iid) {
		tinzenService.sendEvent(uid, iid);
	}
	
	
	@RequestMapping(value="/recommendation/{liked_items}/{numRecs}/{blist}/{cats}", method=RequestMethod.GET)
	public HashMap<String,ArrayList<HashMap<String, Object>>> query(
			@PathVariable("liked_items") String likedItems, 
			@PathVariable("numRecs") int numRecs,
			@PathVariable("blist") String blacklist,
			@PathVariable("cats") String cats) {
		
		ArrayList<String> likeList = stringToList(likedItems);
		ArrayList<String> blackList = stringToList(blacklist);
		ArrayList<String> categories = stringToList(cats);
		JsonObject rec= tinzenService.sendQuery(likeList, numRecs, blackList, categories);
		
		JsonArray arrayOfItems = rec.get("itemScores").getAsJsonArray();
		HashMap<String, ArrayList<HashMap<String, Object>>> jsonMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		ArrayList<HashMap<String, Object>> listOfItems = new ArrayList<HashMap<String, Object>>();
		
		int len = arrayOfItems.size();
		for (int i = 0; i < len; i = i+1) {
			HashMap<String, Object> itemMap = new HashMap<String, Object>();
			JsonObject item = arrayOfItems.get(i).getAsJsonObject();
			String itemID = item.get("item").getAsString();
			Float score = item.get("score").getAsFloat();
			itemMap.put("score", score);
			itemMap.put("item", itemID);
			listOfItems.add(itemMap);
		}
		
		jsonMap.put("itemScores", listOfItems);
		
		return jsonMap;
	} 
	
	
	@RequestMapping(value="/new/{mid}/{blacklist}", method=RequestMethod.GET)
	public String getNewItem(@PathVariable int mid, @PathVariable String blacklist) {
		ArrayList<String> blist= stringToList(blacklist);
		return tinzenService.newItem(mid, blist);
	}
	
	
	@RequestMapping(value="/random/{mid}/{eid}", method=RequestMethod.GET)
	public int randomRate(@PathVariable int mid, @PathVariable int eid) {
		return tinzenService.randomRate(mid, eid);
	}
	
	@RequestMapping(value="/skus/{eid}", method=RequestMethod.GET)
	public ArrayList<String> getSku(@PathVariable int eid) throws SQLException {
		return tinzenService.getSkus(eid);
	}
	
	public void setMultipleUsers() {
		// TODO
	}
	
	
	public void setMultipleItems() {
		// TODO
	}
}