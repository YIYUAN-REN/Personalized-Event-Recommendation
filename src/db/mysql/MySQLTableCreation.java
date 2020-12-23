package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

// ʵ�ʲ���ִ�У�ֻ�ǲ��Ժʹ������ݿ�ʹ��
// ����MySQL���ݣ�DROP, CREATE, INSERT
// ֮ǰ�����кܶ��������µľ�����, ��������������������
public class MySQLTableCreation {
	public static void main(String[] args) {
		try {
			Connection conn = null;		// This is java.sql.Connection. Not com.mysql.jdbc.Connection.

			// Step 1�����ӵ�MySQL���ݿ⣨���ݿ���Ϊlaiproject��
			try {
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				// ����: Class��class(Ҳ����������)��ʵ����ͨ��Classʵ����ȡclass��Ϣ
				// �������������Զ�����mysql-connector-java-8.0.21.jar����࣬������ķ���
				// forName is used to search class named "com.mysql.jdbc.Driver"
				Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();	// ע���driver: ���ݿ�����֧��JDBC��API��     ".getConstructor().newInstance()"Ϊ��ǿ�Ƶ���static{...}��ʼ��
				conn = DriverManager.getConnection(MySQLDBUtil.URL);					// ͨ��driver���������ݿ��URL�����MySQL���ݿ������
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn == null) {
				return;
			}

			// Step 2: ����tables���ص���ʼ״̬
			// DROP TABLE IF EXISTS table_name;
			Statement stmt = conn.createStatement();	// ����SQL���
			String sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);					// ִ��SQL��䣬���ر�����ֵ������
			
			sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			// Step 3: ����tables(items - E, categories - E, users - E, history - R(items��users��))
			/*
			CREATE TABLE table_name (
				column1 datatype,
				column2 datatype,
				column3 datatype,
   				....
			);

			 */
			sql = "CREATE TABLE items"				// Item class�����categories��fields
					+ "(item_id VARCHAR(255) NOT NULL,"	// Stringͨ����VARCHAR
					+ " name VARCHAR(255),"
					+ " rating FLOAT,"
					+ " address VARCHAR(255),"
					+ " image_url VARCHAR(255),"
					+ " url VARCHAR(255),"
					+ " distance FLOAT,"
					+ " PRIMARY KEY (item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE categories"
					+ "(item_id VARCHAR(255) NOT NULL,"
					+ " category VARCHAR(255) NOT NULL,"
					+ " PRIMARY KEY (item_id, category),"					// ��Ϊ�Ƕ�Զ��ϵ�������������PRIMARY KEY
					+ " FOREIGN KEY (item_id) REFERENCES items(item_id))";	// ��items table����item_id��������categories table��ʹ��
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users"
					+ "(user_id VARCHAR(255) NOT NULL,"
					+ " password VARCHAR(255) NOT NULL,"
					+ " first_name VARCHAR(255),"
					+ " last_name VARCHAR(255),"
					+ " PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE history"
					+ "(user_id VARCHAR(255) NOT NULL,"
					+ " item_id VARCHAR(255) NOT NULL,"
					+ " last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"	// ��ΪNOT NULL��������DEFAULT���������û��ֵ�����Զ�INSERT��ǰʱ��
					+ " PRIMARY KEY (user_id, item_id),"								// ��Ϊ�ǹ�ϵ��user_id��item_id�����������������
					+ " FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ " FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			// Step 4: ��������
			// Create a fake user
			/*
			INSERT INTO table_name (column1, column2, column3, ...)	// ��ʡ����Щcolumns������values���밴˳��
			VALUES (value1, value2, value3, ...);
			 */
			sql = "INSERT INTO users VALUES ("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			// 					passwordת���� MD5��ʽ(һ�� hash function)
			System.out.println("Executing query: " + sql);		// debug
			stmt.executeUpdate(sql);
			
			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

