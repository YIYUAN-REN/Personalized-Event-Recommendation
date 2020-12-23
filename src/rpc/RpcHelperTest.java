package rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import entity.Item;
import entity.Item.ItemBuilder;

// 测试getJSONArray方法(检查方法里return的JSON array和我们test得到的JSON array是否一样)
public class RpcHelperTest {

	@Test
	public void testGetJSONArray() throws JSONException {
		Set<String> category = new HashSet<String>();
		category.add("category one");
		Item one = new ItemBuilder().setItemId("one").setRating(5).setCategories(category).build();
		Item two = new ItemBuilder().setItemId("two").setRating(5).setCategories(category).build();
		List<Item> listItem = new ArrayList<Item>();
		listItem.add(one);
		listItem.add(two);
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(one.toJSONObject());
		jsonArray.put(two.toJSONObject());
		// 把listItem用getJSONArray方法转成jsonArray，对比原jsonArray。
		// 若成功，则getJSONArray方法没问题
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
		
	}

}