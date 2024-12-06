package pdghbau.metodos;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class KwMySQL {

	private Connection conn;
	private File file;
	private Statement stmt;

	private KwMySQL(File f) {
		this.file = f;
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.file);
			this.stmt = this.conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private KwMySQL(String urlconn) {
		try {
			this.conn = DriverManager.getConnection(urlconn);
			this.stmt = this.conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static KwMySQL load(File f) {
		return new KwMySQL(f);
	}

	public static KwMySQL load(String f) {
		return new KwMySQL(new File(f));
	}

	public static KwMySQL load(String host, String database, String user, String pass) {
		return new KwMySQL("jdbc:mysql://" + host + "/" + database + "?" + "user=" + user + "&password=" + pass);
	}

	public void update(String q) {
		try {
			this.stmt.executeUpdate(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String q) {
		try {
			return this.stmt.executeQuery(q);
		} catch (Exception localException) {
		}
		return null;
	}

	public void close() {
		try {
			this.stmt.close();
			this.conn.close();
		} catch (Exception localException) {
		}
	}

	public boolean isConnected() {
		try {
			return (this.stmt != null) && (this.conn != null) && (!this.stmt.isClosed()) && (!this.conn.isClosed());
		} catch (Exception localException) {
		}
		return false;
	}

	public Connection getConnection() {
		return this.conn;
	}
}