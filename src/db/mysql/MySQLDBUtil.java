package db.mysql;

// ��������MySQL
public class MySQLDBUtil {
	// ��̨���������ݿ�
	private static final String HOSTNAME = "localhost";
	// ���ݿ�˿�
	private static final String PORT_NUM = "8889"; // change it to your mysql port number
	// ���ݿ�����
	public static final String DB_NAME = "laiproject";
	// ���ݿ��û���
	private static final String USERNAME = "root";
	// ���ݿ�����
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
	// jdbc:mysql://localhost:8889/laiproject?user=root&password=root&autoReconnect=true&serverTimezone=UTC
	//               ���ݿ��ַ     ���ݿ�����
}
