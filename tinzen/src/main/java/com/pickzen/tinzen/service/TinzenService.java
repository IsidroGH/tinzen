package com.pickzen.tinzen.service;

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
	
	public void setItem(String itemID) {
		pioRepository.setItem(itemID);
	}
	
	public JsonObject sendQuery(ArrayList<String> likedItems, int numRecs, ArrayList<String> blacklist) {
		return pioRepository.sendQuery(likedItems, numRecs, blacklist);
	}
	
	public String newItem(int mid, ArrayList<String> blacklist) {
		return pioRepository.getNewItem(mid, blacklist);
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
