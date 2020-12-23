package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

// 实际不会执行，只是测试和创建数据库使用
// 重置MySQL内容，DROP, CREATE, INSERT
// 之前可能有很多杂乱无章的旧数据, 所以清理掉再添加新数据
public class MySQLTableCreation {
	public static void main(String[] args) {
		try {
			Connection conn = null;		// This is java.sql.Connection. Not com.mysql.jdbc.Connection.

			// Step 1：连接到MySQL数据库（数据库名为laiproject）
			try {
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				// 反射: Class是class(也是数据类型)的实例，通过Class实例获取class信息
				// 这里是运行期自动调用mysql-connector-java-8.0.21.jar这个类，用里面的方法
				// forName is used to search class named "com.mysql.jdbc.Driver"
				Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();	// 注册好driver: 数据库用来支持JDBC的API。     ".getConstructor().newInstance()"为了强制调用static{...}初始化
				conn = DriverManager.getConnection(MySQLDBUtil.URL);					// 通过driver，利用数据库的URL获得与MySQL数据库的连接
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn == null) {
				return;
			}

			// Step 2: 清理tables，回到初始状态
			// DROP TABLE IF EXISTS table_name;
			Statement stmt = conn.createStatement();	// 创建SQL语句
			String sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);					// 执行SQL语句，返回被更新值的数量
			
			sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			// Step 3: 创建tables(items - E, categories - E, users - E, history - R(items和users间))
			/*
			CREATE TABLE table_name (
				column1 datatype,
				column2 datatype,
				column3 datatype,
   				....
			);

			 */
			sql = "CREATE TABLE items"				// Item class里除了categories的fields
					+ "(item_id VARCHAR(255) NOT NULL,"	// String通常用VARCHAR
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
					+ " PRIMARY KEY (item_id, category),"					// 因为是多对多关系，所以两个组合PRIMARY KEY
					+ " FOREIGN KEY (item_id) REFERENCES items(item_id))";	// 从items table引用item_id，才能在categories table被使用
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
					+ " last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"	// 因为NOT NULL，所以用DEFAULT。这样如果没有值，就自动INSERT当前时间
					+ " PRIMARY KEY (user_id, item_id),"								// 因为是关系表，user_id和item_id都是外键，构成主键
					+ " FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ " FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			// Step 4: 插入数据
			// Create a fake user
			/*
			INSERT INTO table_name (column1, column2, column3, ...)	// 若省略这些columns，下面values必须按顺序
			VALUES (value1, value2, value3, ...);
			 */
			sql = "INSERT INTO users VALUES ("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			// 					password转化成 MD5格式(一种 hash function)
			System.out.println("Executing query: " + sql);		// debug
			stmt.executeUpdate(sql);
			
			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

