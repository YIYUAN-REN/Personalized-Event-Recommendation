package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// 进一步处理TicketMaster返回的JSON event array,只保留需要的items，再返回给servlets
// servlet读取JSON信息能更方便
public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;
	
	/**
	 * This is a builder pattern in Java.
	 */
	// TicketMaster返回的某些fields可能是null，所以如果单纯用constructor会有很多种组合
	// 不用builder pattern的话用户每次需要input 8个参数，而不输入8个的话java语言不支持构造函数参数默认值 所以需要ItemBuilder
	// constructor设成private，因为只被内部的itemBuilder class调用
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	// 传给前端(不能理解java)需要转化成能理解的JSON
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	// 只需要get（让data fields被其他类访问通常需要getters和setters，但这里不想改变construct后的item instance）
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}

	// builder pattern：用inner class创建需要创建的class
	/* 
	 构造函数：
	 Item(String itemId); Item(String name); Item(String itemId, String name); …
	 实例化：
	 Item item = new Item(itemId, name);
	 */
	/* 
	 实例化(用item builder)：
	 Item item = new ItemBuilder().setItemId().setName().set....build(); 
	 */
	
	// class必须public且static，因为在item class里面，没法实例化(实例化需要item class)后再被main函数调用
	public static class ItemBuilder {
		// 复制所有fields
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		// 实例化时调用setter方法，动态添加
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;	// 返回类的对象，可以级联操作cascading（实例化中不断在object后setter）
		}

		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}

		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}

		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}

		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
		
		// build方法：把itemBuilder class的object(只存了被set过的fields)作为参数传给item class，返回最终生成的item instance
		public Item build() {
			return new Item(this);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemBuilder other = (ItemBuilder) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}
	
	public static void main() {
		Item item = new Item.ItemBuilder().setAddress("abc").setName("1234").build();
	}
}
