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


// 接收经纬度和关键词, 返回筛选后的JSON信息
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
	
	/* 对于getAddress, getImageUrl, getItemList
	       因为address, imageUrl, categories在TicketMaster里包在很多层下面，所以需要方法来帮助使用 */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				
				// 对于本项目只需要获得venues JSONArray里的第一个JSONObject(包含address, city等)
				// 但为了防止防止第一个JSONObject为空，这里用循环跳过空JSONObject
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					// 拿到address JSONObject信息，添加到StringBuilder
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
					
					// 拿到city JSONObject信息，添加到StringBuilder
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
			
			// 添加属于这个event的所有categories
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

	// 获取从TicketMaster API class筛选后(利用Item class)的8个items
	// 从JSONArray转化成list
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		// 只初始化(利用item builder)每个event中存在的items
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			// 这5个event都在events下的第一层
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
			
			//剩下三个藏得比较深
			builder.setCategories(getCategories(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			
			// 把创建好的一个item添加到list
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	// 查找TicketMaster某个地点的events（optional：提供关键词）
    public List<Item> search(double lat, double lon, String keyword) {
    	if (keyword == null) {
    		keyword = DEFAULT_KEYWORD;
    	}
    	
    	// 转化关键词为可以HTTP传输的格式byte string（因为可能是中文/阿拉伯文等）
    	try {
    		keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	String geoHash = GeoHash.encodeGeohash(lat, lon, 8);													// lat和lon需要通过GeoHash类转化成哈希值
    	String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
    	
    	try {
        	// 创建链接(拼接出来)发送request，获得response
        	// connection是用来连接程序和 Ticket Master 的service
    		HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();		// 把类型从基本的URL Connection 转换成 Http URL Connection
    		int responseCode = connection.getResponseCode();													// 发送connection请求，并获得状态(比如200，404, ...)
    		
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			
			if (responseCode != 200) {
				// ...
			}
			
			// 读取response内容，目前是_embedded JSONObject下array构成的string
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));			// BufferedReader：不在内存只是缓存 作用是一次读一行
			String inputLine;
			StringBuilder response = new StringBuilder();														// StringBulder作用是一次性拼接多行string
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			// 取出_embedded object里的events array
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {																		// 若_embedded object不在，表示response为空
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
    
    // 纯debug
    // 检测search获取events是否正确
	private void queryAPI(double lat, double lon) {
		// 把获取的event转化成JSON打印出来
		List<Item> events = search(lat, lon, null);
		try {
		    for (Item event : events) {
		        System.out.println(event.toJSONObject());	// Item信息看了没用，所以转化成JSONObject
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
