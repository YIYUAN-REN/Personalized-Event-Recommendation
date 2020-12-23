package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		//debug用
		/*
		// 测试输出string
		PrintWriter out = response.getWriter();  					// getWriter()是response的输出流
		if (request.getParameter("username") != null) {				// 若名为username的parameter存在
			String username = request.getParameter("username");			
			out.println("Hello " + username);								// 打印出来
		}
		if (request.getParameter("password") != null) {				// 同上
			String password = request.getParameter("password");
			out.print("password: " + password);
		}
		out.close();
		*/
		
		/*
		// 测试输出HTML网页
		response.setContentType("text/html");						// 显示类型改成html
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println("<h1>This is a HTML page</h1>");
		out.println("</body></html>");
		out.close();
		*/
		
		/*
		// 测试JSON object
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String username = "";
		if (request.getParameter("username") != null) {
			username = request.getParameter("username");
		}
		JSONObject obj = new JSONObject();							// 对应jar在Libraries/Web App Libraries
		try {														// 可能JSON put时会有问题
			obj.put("username", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(obj);
		out.close();
		*/
		
		/*
		// 测试JSON array
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONArray array = new JSONArray();
		try {
			array.put(new JSONObject().put("username", "REN"));
			array.put(new JSONObject().put("username", "FENG"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(array);
		out.close();
		*/
		JSONArray array = new JSONArray();
		try {
			String userId = request.getParameter("user_id");
			double lat = Double.parseDouble(request.getParameter("lat"));	// Double类把string转化成double
			double lon = Double.parseDouble(request.getParameter("lon"));
			String keyword = request.getParameter("term");					// 可以没有
			
			DBConnection connection = DBConnectionFactory.getConnection();	// 获得数据库连接
			List<Item> items = connection.searchItems(lat, lon, keyword);	// 调用MySQLConnextion---从TM API获得所有items，同时把这些items存进数据库
			
			Set<String> favorite = connection.getFavoriteItemIds(userId);
	 		connection.close();
			

			for (Item item : items) {
				// Add a thin version of restaurant object
				JSONObject obj = item.toJSONObject();	// 把Item类型转化成JSONObject类型
				// Check if this is a favorite one.
				// This field is required by frontend to correctly display favorite items.
				obj.put("favorite", favorite.contains(item.getItemId()));
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, array);		// 把array写进response返回给前端，直接调用helper class的静态方法
	}
				
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
