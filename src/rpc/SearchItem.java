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
		
		//debug��
		/*
		// �������string
		PrintWriter out = response.getWriter();  					// getWriter()��response�������
		if (request.getParameter("username") != null) {				// ����Ϊusername��parameter����
			String username = request.getParameter("username");			
			out.println("Hello " + username);								// ��ӡ����
		}
		if (request.getParameter("password") != null) {				// ͬ��
			String password = request.getParameter("password");
			out.print("password: " + password);
		}
		out.close();
		*/
		
		/*
		// �������HTML��ҳ
		response.setContentType("text/html");						// ��ʾ���͸ĳ�html
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println("<h1>This is a HTML page</h1>");
		out.println("</body></html>");
		out.close();
		*/
		
		/*
		// ����JSON object
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String username = "";
		if (request.getParameter("username") != null) {
			username = request.getParameter("username");
		}
		JSONObject obj = new JSONObject();							// ��Ӧjar��Libraries/Web App Libraries
		try {														// ����JSON putʱ��������
			obj.put("username", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(obj);
		out.close();
		*/
		
		/*
		// ����JSON array
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
			double lat = Double.parseDouble(request.getParameter("lat"));	// Double���stringת����double
			double lon = Double.parseDouble(request.getParameter("lon"));
			String keyword = request.getParameter("term");					// ����û��
			
			DBConnection connection = DBConnectionFactory.getConnection();	// ������ݿ�����
			List<Item> items = connection.searchItems(lat, lon, keyword);	// ����MySQLConnextion---��TM API�������items��ͬʱ����Щitems������ݿ�
			
			Set<String> favorite = connection.getFavoriteItemIds(userId);
	 		connection.close();
			

			for (Item item : items) {
				// Add a thin version of restaurant object
				JSONObject obj = item.toJSONObject();	// ��Item����ת����JSONObject����
				// Check if this is a favorite one.
				// This field is required by frontend to correctly display favorite items.
				obj.put("favorite", favorite.contains(item.getItemId()));
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, array);		// ��arrayд��response���ظ�ǰ�ˣ�ֱ�ӵ���helper class�ľ�̬����
	}
				
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
