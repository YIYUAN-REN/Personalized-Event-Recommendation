package db;

import db.mysql.MySQLConnection;

// factory pattern
// ����DBConnection��ѡ�����ݿ⣬ת����������
// ������factory��ÿ�ζ�Ҫnew MySQLConnection()������ÿ���޸Ķ�Ҫ�ֱ��new����instance���޸�
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
