package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


// ���վ�γ�Ⱥ͹ؼ���, ����ɸѡ���JSON��Ϣ
public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "AlCP6XQYfdnXrUiuFGLh0w6CaV9H7yVL";
	
	/**
	 * Helper methods
	 */

	//  {
	//    "name": "laioffer",
              //    "id": "12345",
              //    "url": "www.laioffer.com",
	//    ...
	//    "_embedded": {
	//	    "venues": [
	//	        {
	//		        "address": {
	//		           "line1": "101 First St,",
	//		           "line2": "Suite 101",
	//		           "line3": "...",
	//		        },
	//		        "city": {
	//		        	"name": "San Francisco"
	//		        }
	//		        ...
	//	        },
	//	        ...
	//	    ]
	//    }
	//    ...
	//  }
	
	/* ����getAddress, getImageUrl, getItemList
	       ��Ϊaddress, imageUrl, categories��TicketMaster����ںܶ�����棬������Ҫ����������ʹ�� */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				
				// ���ڱ���Ŀֻ��Ҫ���venues JSONArray��ĵ�һ��JSONObject(����address, city��)
				// ��Ϊ�˷�ֹ��ֹ��һ��JSONObjectΪ�գ�������ѭ��������JSONObject
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					// �õ�address JSONObject��Ϣ����ӵ�StringBuilder
					if (!venue.isNull("address") ) {
						JSONObject address = venue.getJSONObject("address");
						
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append(" ");
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append(" ");
							sb.append(address.getString("line3"));
						}
					}
					
					// �õ�city JSONObject��Ϣ����ӵ�StringBuilder
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						
						if (!city.isNull("name")) {
							sb.append(" ");
							sb.append("name");
						}
					}
					
					if (!sb.toString().equals("")) {
						return sb.toString();
					}
				}
			}
		}
		
		return "";
	}

	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			
			for (int i = 0; i < images.length(); ++i) {
				JSONObject image = images.getJSONObject(i);
				
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		
		return "";
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			
			// ����������event������categories
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject(("segment"));
					
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						categories.add(name);
					}
				}
			}
		}
		return categories;
	}

	// ��ȡ��TicketMaster API classɸѡ��(����Item class)��8��items
	// ��JSONArrayת����list
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		// ֻ��ʼ��(����item builder)ÿ��event�д��ڵ�items
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			// ��5��event����events�µĵ�һ��
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
			//ʣ�������صñȽ���
			builder.setCategories(getCategories(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			
			// �Ѵ����õ�һ��item��ӵ�list
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	// ����TicketMasterĳ���ص��events��optional���ṩ�ؼ��ʣ�
    public List<Item> search(double lat, double lon, String keyword) {
    	if (keyword == null) {
    		keyword = DEFAULT_KEYWORD;
    	}
    	
    	// ת���ؼ���Ϊ����HTTP����ĸ�ʽbyte string����Ϊ����������/�������ĵȣ�
    	try {
    		keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	String geoHash = GeoHash.encodeGeohash(lat, lon, 8);													// lat��lon��Ҫͨ��GeoHash��ת���ɹ�ϣֵ
    	String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
    	
    	try {
        	// ��������(ƴ�ӳ���)����request�����response
        	// connection���������ӳ���� Ticket Master ��service
    		HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();		// �����ʹӻ�����URL Connection ת���� Http URL Connection
    		int responseCode = connection.getResponseCode();													// ����connection���󣬲����״̬(����200��404, ...)
    		
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			
			if (responseCode != 200) {
				// ...
			}
			
			// ��ȡresponse���ݣ�Ŀǰ��_embedded JSONObject��array���ɵ�string
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));			// BufferedReader�������ڴ�ֻ�ǻ��� ������һ�ζ�һ��
			String inputLine;
			StringBuilder response = new StringBuilder();														// StringBulder������һ����ƴ�Ӷ���string
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			// ȡ��_embedded object���events array
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {																		// ��_embedded object���ڣ���ʾresponseΪ��
				return new ArrayList<>();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			
			return getItemList(events);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return new ArrayList<>();    	
    }
    
    // ��debug
    // ���search��ȡevents�Ƿ���ȷ
	private void queryAPI(double lat, double lon) {
		// �ѻ�ȡ��eventת����JSON��ӡ����
		List<Item> events = search(lat, lon, null);
		try {
		    for (Item event : events) {
		        System.out.println(event.toJSONObject());	// Item��Ϣ����û�ã�����ת����JSONObject
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}        
}
