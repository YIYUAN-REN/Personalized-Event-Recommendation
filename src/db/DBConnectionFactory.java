package db;

import db.mysql.MySQLConnection;

// factory pattern
// 创建DBConnection，选择数据库，转化成新类型
// 若不用factory，每次都要new MySQLConnection()，这样每次修改都要分别把new过的instance都修改
public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case "mysql":
			return new MySQLConnection();
		case "mongodb":
			//return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("invalid db:" + db);
		}
	}
	
	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}
	
}
