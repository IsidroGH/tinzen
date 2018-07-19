package com.pickzen.tinzen.service;

import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.pickzen.tinzen.repository.TinzenRepository;

@Service
public class TinzenService {
	
	@Autowired
	private TinzenRepository pioRepository;
	
	
	public void sendEvent(String uid, String itemID) {
		pioRepository.sendViewEvent(uid, itemID);
	}
	
	
	public void setUser(String uid) {
		pioRepository.setUser(uid);
	}
	
	
	public void setItem(String itemID, ArrayList<String> categories) {
		pioRepository.setItem(itemID, categories);
	}
	
	
	public JsonObject sendQuery(ArrayList<String> likedItems, int numRecs, ArrayList<String> blacklist, ArrayList<String> categories) {
		return pioRepository.sendQuery(likedItems, numRecs, blacklist, categories);
	}
	
	
	public String newItem(int mid, ArrayList<String> blacklist) {
		return pioRepository.getNewItem(mid, blacklist);
	}
	
	
	public int randomRate(int mid, int eid) {
		return pioRepository.getRandomRate(mid, eid);
	}
	
	public ArrayList<String> getSkus(int eid) throws SQLException {
		return pioRepository.getSkus(eid);
	}
	
	public void sendEvents() {
		// TODO
	}
	
	
	public void setUsers() {
		// TODO
	}
	
	
	public void setItems() {
		// TODO
	}
}
