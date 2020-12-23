package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// ��һ������TicketMaster���ص�JSON event array,ֻ������Ҫ��items���ٷ��ظ�servlets
// servlet��ȡJSON��Ϣ�ܸ�����
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
	// TicketMaster���ص�ĳЩfields������null���������������constructor���кܶ������
	// ����builder pattern�Ļ��û�ÿ����Ҫinput 8����������������8���Ļ�java���Բ�֧�ֹ��캯������Ĭ��ֵ ������ҪItemBuilder
	// constructor���private����Ϊֻ���ڲ���itemBuilder class����
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
	
	// ����ǰ��(�������java)��Ҫת����������JSON
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
	
	// ֻ��Ҫget����data fields�����������ͨ����Ҫgetters��setters�������ﲻ��ı�construct���item instance��
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

	// builder pattern����inner class������Ҫ������class
	/* 
	 ���캯����
	 Item(String itemId); Item(String name); Item(String itemId, String name); ��
	 ʵ������
	 Item item = new Item(itemId, name);
	 */
	/* 
	 ʵ����(��item builder)��
	 Item item = new ItemBuilder().setItemId().setName().set....build(); 
	 */
	
	// class����public��static����Ϊ��item class���棬û��ʵ����(ʵ������Ҫitem class)���ٱ�main��������
	public static class ItemBuilder {
		// ��������fields
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		// ʵ����ʱ����setter��������̬���
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;	// ������Ķ��󣬿��Լ�������cascading��ʵ�����в�����object��setter��
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
		
		// build��������itemBuilder class��object(ֻ���˱�set����fields)��Ϊ��������item class�������������ɵ�item instance
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
