package db.mysql;

// 帮助连接MySQL
public class MySQLDBUtil {
	// 哪台机器的数据库
	private static final String HOSTNAME = "localhost";
	// 数据库端口
	private static final String PORT_NUM = "8889"; // change it to your mysql port number
	// 数据库名字
	public static final String DB_NAME = "laiproject";
	// 数据库用户名
	private static final String USERNAME = "root";
	// 数据库密码
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
	// jdbc:mysql://localhost:8889/laiproject?user=root&password=root&autoReconnect=true&serverTimezone=UTC
	//               数据库地址     数据库名字
}
