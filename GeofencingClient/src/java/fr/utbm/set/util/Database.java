/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.utbm.set.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

/**
 *
 * @author WAFAA AIT CHEIK BIHI
 */
public class Database {
	public static final int ODBC = 1;
	public static final int MYSQL = 2;

	private final String ODBCdriver = "sun.jdbc.odbc.JdbcOdbcDriver";
	private final String ODBCbridge = "jdbc:odbc:";
	private final String MYSQLdriver = "com.mysql.jdbc.Driver"; // "org.gjt.mm.mysql.Driver";
	private final String MYSQLbridge = "jdbc:mysql:";

	String driver, bridge;

	String url;
	protected Connection db = null;

	public Database() {
		driver = ODBCdriver;
		bridge = ODBCbridge;
	}

	public Database(int type) {
		setDriver(type);
	}

	public void setDriver(int type) {
		switch (type) {
		case ODBC:
			driver = ODBCdriver;
			bridge = ODBCbridge;
			break;
		case MYSQL:
			driver = MYSQLdriver;
			bridge = MYSQLbridge;
			break;
		}
	}

	public boolean openDatabase(String name) {
		String url = bridge + name;

		try {
			Class.forName(driver);
			db = DriverManager.getConnection(url);
		} catch (Exception e) {
			writeln("Erreur d'ouverture :\n" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean openDatabase(String name, String pwd) {
		String url = bridge + name;

		try {
			Class.forName(driver);
			db = DriverManager.getConnection(url, " ", pwd);
		} catch (Exception e) {
			writeln("Erreur d'ouverture :\n" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean openDatabase(String name, String user, String pwd) {
		String url = bridge + name;

		try {
			Class.forName(driver);
		} catch (Exception e) {
			writeln("Erreur de chargement :\n" + e.getMessage());
			return false;
		}
		try {
			// db = DriverManager.getConnection(url,user,pwd);
			db = DriverManager.getConnection(url + "?user=" + user
					+ "&password=" + pwd);
		} catch (Exception e) {
			writeln(url + "?user=" + user + "&password=" + pwd);
			writeln("Erreur d'ouverture :\n" + e.getMessage());
			return false;
		}
		return true;
	}

	public int printInfos() {
		try {
			DatabaseMetaData dbm = db.getMetaData();
			writeln("------------------------------");
			writeln("Connexion : " + dbm.getURL());
			writeln("Driver    : " + dbm.getDriverName());
			writeln("Version   : " + dbm.getDriverVersion());

			ResultSet rs = dbm.getTables(null, null, "%",
					new String[] { "TABLE" });

			// Affichage des enregistrements :
			ResultSetMetaData rsm = rs.getMetaData();

			int nc = rsm.getColumnCount();
			for (int i = 1; i <= nc; i++)
				write(rsm.getColumnName(i) + "\t");
			writeln();
			String champ;
			while (rs.next()) {
				for (int i = 1; i <= nc; i++) {
					champ = rs.getString(i);
					if (champ == null)
						champ = "null";
					write("(" + champ + ")\t");
				}
				writeln();
			}
			writeln("------------------------------");
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	public String[] getFields(String table) {
		String fields[] = null;
		try {
			String requete = "Select * from " + table;
			Statement isql = db.createStatement();
			ResultSet rs = isql.executeQuery(requete);
			ResultSetMetaData rsm = rs.getMetaData();
			int nc = rsm.getColumnCount();
			fields = new String[nc];
			for (int i = 1; i <= nc; i++) {
				fields[i - 1] = rsm.getColumnName(i);
			}
			rs.close();
			isql.close();
		} catch (Exception e) {
			writeln("erreur :" + e.getMessage());
		}
		return fields;
	}

	public int insert(String Values[], String table) {
		try {
			String requete = "INSERT INTO " + table + " VALUES ('"
					+ Values[0].replace("\\", "\\\\") + "'";
			for (int i = 1; i < Values.length; i++) {
				if (Values[i] != null) {
					requete = requete + ",'" + Values[i].replace("\\", "\\\\")
							+ "'";
				} else
					requete = requete + ",NULL";
			}
			requete = requete + ")";
			System.out.println(requete);
			Statement isql = db.createStatement();
			int r = isql.executeUpdate(requete);
			isql.close();
			return r;
		} catch (Exception e) {
			System.out.println("Erreur d'insertion : " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Erreur d'insertion");
			return 0;
		}
	}

	public int insert(String fields[], String values[], String table) throws SQLException {
			String f = "(" + fields[0];
			for (int i = 1; i < fields.length; i++) {
				if (fields[i] != null)
					f = f + "," + fields[i];
			}
			f = f + ")";

			String requete = "INSERT INTO " + table + f + " VALUES ('"
					+ values[0].replace("\\", "\\\\") + "'";
			for (int i = 1; i < values.length; i++) {
				if (values[i] != null)
					requete = requete
							+ ",'"
							+ values[i].replace("\\", "\\\\")
									.replace("'", "''") + "'";
				else
					requete = requete + ",NULL";
			}

			requete = requete + ")";

			System.out.println(requete);

			Statement isql = db.createStatement();
			int r = isql.executeUpdate(requete);
			isql.close();
			return r;
	}

	public int updateRow(String field, String value, String values[], String table) {
		try {
			String fields[] = this.getFields(table);
			if (fields != null && fields.length > 0) {
				String requete = "UPDATE " + table + " SET " + fields[0]
						+ " = '" + values[0].replace("\\", "\\\\") + "' \n";

				for (int i = 1; i < fields.length; i++) {
					if (values[i] != null)
						requete = requete + ", " + fields[i] + " = '"
								+ values[i].replace("\\", "\\\\") + "' \n";
					else
						requete = requete + ", " + fields[i] + " = NULL \n";
				}
				requete = requete + " WHERE " + field + " = '" + value + "'";
				System.out.println(requete);
				Statement isql = db.createStatement();
				int r = isql.executeUpdate(requete);
				isql.close();

				return r;
			} else
				return 0;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erreur de mise à jour: "
					+ e.getMessage());
			return 0;
		}
	}

        public int updateRow(String field,  String value, String fields[], String values[], String table) {
		try {

			if (fields != null && fields.length > 0) {
				String requete = "UPDATE " + table + " SET " + fields[0]
						+ " = '" + values[0].replace("\\", "\\\\") + "' \n";

				for (int i = 1; i < fields.length; i++) {
					if (values[i] != null)
						requete = requete + ", " + fields[i] + " = '"
								+ values[i].replace("\\", "\\\\") + "' \n";
					else
						requete = requete + ", " + fields[i] + " = NULL \n";
				}
				requete = requete + " WHERE " + field + " = '" + value + "'";
				System.out.println(requete);
				Statement isql = db.createStatement();
				int r = isql.executeUpdate(requete);
				isql.close();

				return r;
			} else
				return 0;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erreur de mise à jour: "
					+ e.getMessage());
			return 0;
		}
	}

	/*
	 * public int updateRow(String conditionFields[], String conditionValues[],
	 * String values[],String table){ try { String fields[] =
	 * this.getFields(table); if (fields!=null && fields.length>0) { String
	 * requete = "UPDATE " + table + " SET " + fields[0] + " = '"
	 * +values[0].replace("\\", "\\\\") + "' \n"; for (int i=1;i<fields.length;i++) {
	 * requete = requete + ", " + fields[i] + " = '" + values[i].replace("\\",
	 * "\\\\") + "' \n"; } requete = requete + " WHERE " + conditionFields[0] +" = '" +
	 * conditionValues[0] + "'"; for (int i=1;i<conditionFields.length;i++) {
	 * requete = requete + " and " + conditionFields[i] + " = '" +
	 * conditionValues[i] + "' \n"; } System.out.println(requete); Statement
	 * isql = db.createStatement(); int r = isql.executeUpdate(requete);
	 * isql.close();
	 *
	 * return r; } else return 0; } catch(Exception e) {
	 * JOptionPane.showMessageDialog(null,"Erreur de mise à jour: " +
	 * e.getMessage()); return 0; } }
	 */

	public int removeRow(String fields[], String values[], String table) {
		try {

			if (fields.length > 0 && values.length > 0) {
				String requete = "DELETE FROM " + table + " WHERE " + fields[0]
						+ " = '" + values[0] + "' \n";
				for (int i = 1; i < fields.length; i++) {
					requete = requete + " and " + fields[i] + " = '"
							+ values[i] + "' \n";
				}
				Statement isql = db.createStatement();
				int r = isql.executeUpdate(requete);
				isql.close();
				return r;
			} else
				return 0;
		} catch (Exception e) {
			System.out.println("Erreur de suppression : " + e.getMessage());

			return 0;
		}
	}

	public String[] getNextRow(ResultSet rs, int NC) {
		try {
			if (rs.next()) {
				String T[] = new String[NC];
				for (int i = 0; i < NC; i++)
					T[i] = rs.getString(i + 1);
				return T;
			}
			return null;
		} catch (Exception e) {
			writeln("Erreur getNextRow :\n" + e.getMessage());
			return null;
		}
	}

	public int getNumCols(ResultSet rs) {
		try {
			ResultSetMetaData rsm = rs.getMetaData();
			int nc = rsm.getColumnCount();
			return nc;
		} catch (Exception e) {
			System.out.println("Erreur getNumCols : " + e.getMessage());
			return 0;
		}
	}

	public int getNumCols(String table) {
		try {
			String requete = "Select * from " + table;
			Statement isql = db.createStatement();
			ResultSet rs = isql.executeQuery(requete);
			ResultSetMetaData rsm = rs.getMetaData();
			int nc = rsm.getColumnCount();
			rs.close();
			isql.close();
			return nc;
		} catch (Exception e) {
			writeln("erreur getNumCols :" + e.getMessage());
			return -1;
		}
	}

	public int getNumRows(ResultSet rs) {
		try {
			int nr;
			if (rs.getType() == ResultSet.TYPE_SCROLL_SENSITIVE) {
				rs.last();
				nr = rs.getRow();
			} else {
				nr = 0;
				while (rs.next())
					nr++;
			}
			rs.beforeFirst();
			return nr;
		} catch (Exception e) {
			writeln("erreur getNumRows(ResultSet) :" + e.getMessage());
			return 0;
		}
	}

	public int getNumRows(String table) {
		try {
			String requete = "Select Count(*) from " + table;
			Statement isql = db.createStatement();
			ResultSet rs = isql.executeQuery(requete);
			int nr = 0;
			if (rs.next())
				nr = rs.getInt(1);
			rs.close();
			isql.close();
			return nr;
		} catch (Exception e) {
			writeln("erreur getNumRows(String) :" + e.getMessage());
			return -1;
		}
	}

	public ResultSet executeQuery(String q) {
		try {
			Statement isql = db
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = isql.executeQuery(q);
			return rs;
		} catch (Exception e) {
			writeln("erreur executeQuery(String) :" + e.getMessage());
			return null;
		}
	}

	public boolean executeUpdate(String q) {
		try {
			Statement isql = db
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			int index = isql.executeUpdate(q);
			isql.close();
			if (index == 1) {
				return true;
			}
		} catch (Exception e) {
			writeln("erreur executeUpdate(String) :" + e.getMessage());

		}
		return false;
	}

	public ResultSet executeQuery(PreparedStatement q) {
		try {
			ResultSet rs = q.executeQuery();
			// rs.getStatement()
			// System.out.println("OK");
			return rs;
		} catch (Exception e) {
			System.out.println("Erreur executeQuery(PreparedStatement) : "
					+ e.getMessage());
			return null;
		}
	}

	public String[] getNextRecord(ResultSet rs) {
		try {
			String TC[] = null;
			// if (rs.next()) {
			int nc = getNumCols(rs);
			TC = new String[nc];
			for (int i = 0; i < nc; i++) {
				TC[i] = rs.getString(i + 1);
			}
			// }
			return TC;
		} catch (Exception e) {
			writeln("Erreur getNextRecord :" + e.getMessage());
			return null;
		}
	}

	public String[] getPreviousRecord(ResultSet rs) {
		try {
			String TC[] = null;
			if (rs.previous()) {
				int nc = getNumCols(rs);
				TC = new String[nc];
				for (int i = 0; i < nc; i++) {
					TC[i] = rs.getString(i + 1);
				}
			}
			return TC;
		} catch (Exception e) {
			System.out.println("Erreur getPreviousRecord : " + e.getMessage());
			return null;
		}
	}

	public String[] getFirstRecord(ResultSet rs) {
		try {
			String TC[] = null;
			if (rs.first()) {
				int nc = getNumCols(rs);
				TC = new String[nc];
				for (int i = 0; i < nc; i++) {
					TC[i] = rs.getString(i + 1);
				}
			}
			return TC;
		} catch (Exception e) {
			System.out.println("Erreur getFirstRecord : " + e.getMessage());
			return null;
		}
	}

	public String[] getLastRecord(ResultSet rs) {
		try {
			String TC[] = null;
			if (rs.last()) {
				int nc = getNumCols(rs);
				TC = new String[nc];
				for (int i = 0; i < nc; i++) {
					TC[i] = rs.getString(i + 1);
				}
			}
			return TC;
		} catch (Exception e) {
			System.out.println("Erreur getLastRecord : " + e.getMessage());
			return null;
		}
	}

	public String[] seekForRecord(String field, String value, ResultSet rs) {
		try {
			String TC[] = null;
			rs.first();
			int nc = getNumCols(rs);
			String s = rs.getString(field);
			while (!s.equals(value) && rs.next())
				s = rs.getString(field);
			if (s.equals(value)) {
				TC = new String[nc];
				TC[0] = s;
				for (int i = 1; i < nc; i++) {
					TC[i] = rs.getString(i + 1);
				}
			} else
				rs.first();
			return TC;
		} catch (Exception e) {
			System.out.println("Erreur seekForRecord : " + e.getMessage());
			return null;
		}
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {
		}
	}

	public Connection getConnection() {
		return db;
	}

	public String addSlashes(String S) {
		if (S != null && S.indexOf("'") >= 0) {
			StringBuffer sb = new StringBuffer(S);
			int p = -2;
			char c = '\'';
			if (driver == ODBCdriver)
				c = '\'';
			else if (driver == MYSQLdriver)
				c = '\\';
			while ((p = sb.indexOf("'", p + 2)) >= 0)
				sb.insert(p, c);
			// TC[i].replaceAll("'",'\\'+"'");
			return sb.toString();
		}
		return S;
	}

	public int replace(String oldVal, String newVal, String field, String table) {
		String req = "UPDATE " + table + " SET " + field + " = '" + newVal
				+ "' WHERE " + field + " = '" + oldVal + "'";
		try {
			Statement isql = db.createStatement();
			int r = isql.executeUpdate(req);
			isql.close();
			return r;
		} catch (Exception e) {
			System.out.println("Erreur de remplacement : " + e.getMessage());
			return 0;
		}
	}

	private void write(String S) {
		System.out.print(S);
	}

	private void writeln(String S) {
		System.out.println(S);
	}

	private void writeln() {
		System.out.println();
	}
}
