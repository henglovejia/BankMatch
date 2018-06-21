import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlDao {
	private Connection con = null;
	private String driver = "com.mysql.jdbc.Driver";

	public SqlDao() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(Application.getUrl(), Application.getUser(), Application.getPassword());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public SqlDao(String created) {
		// TODO Auto-generated constructor stub
		String createThulac = "CREATE TABLE `" + Application.getTable() + "_thulac` ("
				+ "`Id` INT NOT NULL AUTO_INCREMENT," + "`questionId` INT NOT NULL,"
				+ "`thulac_content` LONGTEXT NOT NULL," + "`length` INT NOT NULL DEFAULT 0,"
				+ "`item` CHAR(2) NOT NULL DEFAULT 0," + "PRIMARY KEY (`Id`),"
				+ "INDEX `questionId` (`questionId` ASC)," + "INDEX `length` (`length` ASC),"
				+ "INDEX `item` (`item` ASC))";
		String createResult = "CREATE TABLE `" + Application.getTable() + "_result` ("
				+ "`Id` INT NOT NULL AUTO_INCREMENT," + "`questionId1` INT NOT NULL," + "`questionId2` INT NOT NULL,"
				+ "`content_similarity` INT NOT NULL," + "`item` CHAR(2) NOT NULL DEFAULT 0," + "PRIMARY KEY (`Id`))";
		String createCount = "CREATE TABLE `" + Application.getTable() + "_count` ("
				+ "`Id` INT NOT NULL AUTO_INCREMENT," + "`word` TEXT NOT NULL," + "`wordcount` INT NOT NULL DEFAULT 0,"
				+ "PRIMARY KEY (`Id`))";
		String createItemThulac = "CREATE TABLE `" + Application.getTable().replace("content", "item") + "_thulac` ("
				+ "`Id` INT NOT NULL AUTO_INCREMENT," + "`questionId` INT NOT NULL,"
				+ "`thulac_content` LONGTEXT NOT NULL," + "`length` INT NOT NULL DEFAULT 0," + "PRIMARY KEY (`Id`),"
				+ "INDEX `questionId` (`questionId` ASC))";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(Application.getUrl(), Application.getUser(), Application.getPassword());
			Statement state = con.createStatement();
			state.execute(createThulac);
			state.execute(createResult);
			state.execute(createCount);
			state.execute(createItemThulac);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// public SqlDao(String ip, String database,String user,String password) {
	// // TODO Auto-generated constructor stub
	// url = "jdbc:mysql://" + ip +":3306/" + database
	// +"?useUnicode=true&characterEncoding=UTF-8";
	// this.user = user;
	// this.password = password;
	// connection();
	// }

	public int getCountInTable() {
		ResultSet result = null;
		int count = 0;
		try {
			String sql = "select count(questionId) as num from " + Application.getTable();
			Statement state = con.createStatement();
			result = state.executeQuery(sql);
			result.next();
			count = result.getInt("num");
			result.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return count;
	}

	public ArrayList<ArrayList<String>> getQuery(String sql, String[] args, int runWhich) {
		ResultSet resultSet = null;
		ArrayList<ArrayList<String>> list = null;
		try {
			Statement state = con.createStatement();
			resultSet = state.executeQuery(sql);
			resultSet.last();
			int lines = resultSet.getRow();
			resultSet.beforeFirst();
			list = new ArrayList<>(lines);
			while (resultSet.next()) {
				String temp = new String();
				switch (runWhich) {
				case 0:
					temp = Translate(resultSet.getString(args[1]));
					break;
				case 1:
					temp = resultSet.getString(args[1]);
					break;
				default:
					break;
				}
				if (temp.length() < 1)
					continue;
				ArrayList<String> items = new ArrayList<>(temp.split(" ").length + 1);
				items.add(resultSet.getString(args[0]));
				for (String item : temp.split(" "))
					items.add(item);
				list.add(items);

			}
			resultSet.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("getQuery:" + sql + ":" + e.toString());
		}
		return list;
	}

	public void insert(String sql, String[][] datas) {
		int j = 0;
		try {
			con.setAutoCommit(false);
			PreparedStatement insert = con.prepareStatement(sql);
			for (String[] data : datas) {
				if (data[0] != null) {
					try {
						int i = 1;
						for (String item : data)
							insert.setString(i++, item);
						insert.addBatch();
						if (j++ == 1000) {
							j = 0;
							insert.executeBatch();
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("SqlDao:insert:" + e.toString() + " " + data[0] + " " + data[1]);
					}
				}
			}
			insert.executeBatch();
			con.commit();
			insert.clearBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteAll(String table) {
		try {
			String deletesql = "delete from " + table;
			con.createStatement().executeUpdate(deletesql);
			String autosql = "ALTER TABLE " + table + " AUTO_INCREMENT = 1";
			con.createStatement().executeUpdate(autosql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
	}

	public void deleteById(String ids) {
		try {
			Statement state = con.createStatement();
			String deletesql = "delete from " + Application.getTable() + " where questionId in (" + ids + ")";
			state.executeUpdate(deletesql);
			deletesql = "delete from " + Application.getTable() + "_thulac where questionId in (" + ids + ")";
			state.executeUpdate(deletesql);
			deletesql = "delete from " + Application.getTable().replace("content", "item")
					+ "_thulac where questionId in (" + ids + ")";
			state.executeUpdate(deletesql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
	}

	public void update(String content_table, String item_table) {
		try {
			String updatesql = "UPDATE " + content_table + " t1," + "(select questionId,SUM(length) sum_length from "
					+ item_table + " group by questionId) t2 "
					+ "set t1.length = t1.length + t2.sum_length,t1.item = '1'" + "where t1.questionId = t2.questionId";
			con.createStatement().executeUpdate(updatesql);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
	}

	public String Translate(String input) {
		String text = new String();
		try {
			if (input.indexOf("<text>") != -1)
				if (Application.getTable().indexOf("english") != -1)
					text = Xml2Text.getTextOnly(input).replaceAll(" +", " ");
				else
					text = Xml2Text.getTextOnly(input).replace(" ", "");
		} catch (Exception e) {
			System.out.println("Translate:" + input);
		}
		return text.replace("nbsp", "").replace("amp", "").replace("&", "").replace("_", "").replace("．", "")
				.replace(";", "").replace("-", "");
	}

	public void closecon() {
		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
