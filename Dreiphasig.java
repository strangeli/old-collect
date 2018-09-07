import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Properties;

public class Dreiphasig {
	public long[] time;
	private String info;
	public int kmax;
	public int typemax;
	private int i;
	public double mess[][];
	public int[] col;
	private String framework = "embedded";
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	Statement stmt = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	ResultSet rs2 = null;
	ResultSet rst = null;
	ResultSet rst2 = null;
	Connection conn = null;
	public static String dbName = "GridVisDB3Phasen2"; // the name of the database
	Properties props = new Properties(); // connection properties

	/**
	 * @param args
	 */
	static Dreiphasig wert = new Dreiphasig(1);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			wert.connection(dbName);
			System.out.println("Connected.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			wert.read();
			System.out.println("Read.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		try {
			wert.print();
			System.out.println("Printed.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// try {
		// wert.Export();
		// System.out.println("Exported.");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		

			shutdown();
			System.out.println("Shut down.");
	
		
	}
	
	public Dreiphasig(int m){};

	public void connection(String dbName) throws SQLException {
		/* parse the arguments to determine which framework is desired */
		

		System.out.println("Dreiphasig starting in " + framework + " mode");
		loadDriver();
		conn = DriverManager.getConnection(protocol + dbName);
	}
	
	public static void shutdown(){
		try{
		DriverManager.getConnection(protocol + ";shutdown=true");
		}
		catch (SQLException se)
        {
            if (( (se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState()) ))) {
                // we got the expected exception
                System.out.println("Derby shut down normally");
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                System.err.println("Derby did not shut down normally");
            
            }
            }
	}

	public void read() throws SQLException {
		stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT COUNT(TIME_ID) FROM VALUE_TIME");
		rs.next();
		kmax = rs.getInt(1);
		System.out.println("kmax = " + kmax);
		mess = new double[kmax][11];
		ResultSet rs2 = stmt.executeQuery("SELECT COUNT(TYPE) FROM VALUE_DESC");
		rs2.next();
		typemax = rs2.getInt(1);
		System.out.println("typemax = " + typemax);
		col = new int[typemax];

		for (i = 1; i <= typemax; i++) {
			ResultSet rs3 = stmt
					.executeQuery("SELECT TYPE from VALUE_DESC where VALUE_ID ="
							+ i);
			rs3.next();
			switch (rs3.getInt(1)) {
			case 4097:
				col[i - 1] = 1;
				break;
			case 4098:
				col[i - 1] = 2;
				break;
			case 4099:
				col[i - 1] = 3;
				break;
			case 57345:
				col[i - 1] = 4;
				break;
			case 57346:
				col[i - 1] = 5;
				break;
			case 57347:
				col[i - 1] = 6;
				break;
			case 57348:
				col[i - 1] = 7;
				break;
			case 135169:
				col[i - 1] = -1;
				break;
			case 135170:
				col[i - 1] = -1;
				break;
			case 135171:
				col[i - 1] = -1;
				break;
			case 143361:
				col[i - 1] = 8;
				break;
			case 143362:
				col[i - 1] = 9;
				break;
			case 143363:
				col[i - 1] = 10;
				break;
			default:
				System.out.println("Fehler.");
			}
			info =  ("i = " + i + ", type = " + rs3.getInt(1)
					+ ", Spalte " + col[i - 1]);
		}

		for (int k = 1; k <= kmax; k=k+100) {
			ResultSet rst = stmt
					.executeQuery("SELECT TIME_ID, VALUE_AVG from VALUE_DATA where TIME_ID BETWEEN "
							+ k + " AND " + Math.min(k+99,kmax)); // mehr Time_IDs gleichzeitig
			rst.next();
			for (int n = 0; n < Math.min(100,kmax-k+1); n++) {
			for (int j = 1; j <= typemax; j++) {
				if (col[j - 1] > 0) {
					mess[rst.getInt(1)-1][col[j - 1]] = rst.getDouble(2);
					 }
				rst.next();
				}
			}
			
			
//			while (rst.next()) {
//				if (col[rst.getInt(1) - 1] > 0) {
//					mess[k - 1][col[rst.getInt(1) - 1]] = rst.getDouble(2);
//				}
//			}

//			 for (int j = 1; j <= typemax; j++) { // Alternative zur while-Schleife
//			 if (col[j-1] > 0) { 
//			 mess[k - 1][col[j - 1]] = rst.getDouble(4);
//		  			}
//			 rst.next();
//			 }

			
			if (k % 1000 == 0) {
				System.out.print("Read " + k + " rows from database. ");
			}
			if (k % 3000 == 0) {
				System.out.println();
			}
		
			ResultSet rst2 = stmt
					.executeQuery("SELECT TIME_ID, FROM_NS from VALUE_TIME where TIME_ID Between "
							+ k + " and " + Math.min(k+99,kmax));
					
			for (int n = 0; n < Math.min(100,kmax-k+1); n++) {
			rst2.next();
			mess[rst2.getInt(1)-1][0] = rst2.getLong(2);
			}

		}
		System.out.println("typemax = " + typemax);
		System.out.println("kmax = " + kmax);
		System.out.println();
		
	}

	public static double[][] getmess() {
		return wert.mess;
	}
	
	public void print() throws SQLException {
		System.out.println("kmax = " + kmax);
		System.out.print("t U1 U2 U3 I1 I2 I3 phi1 phi2 phi3");
		System.out.println();
		for (double[] row : mess) {
			for (double value : row) {
				System.out.print(value + ", ");
			}
			System.out.println();
		}
	}

	public void Export() throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter("Array_" + dbName
				+ ".csv"));
		pw.print("t U1 U2 U3 I1 I2 I3 phi1 phi2 phi3");
		pw.println();
		for (double[] row : mess) {
			for (double value : row) {
				pw.print(value + ", ");
			}
			pw.println();
		}
		pw.println();
		pw.close(); // Without this, the output file may be empty
	}

	private void loadDriver() {

		try {
			Class.forName(driver).newInstance();
			System.out.println("Loaded the appropriate driver");
		} catch (ClassNotFoundException cnfe) {
			System.err.println("\nUnable to load the JDBC driver " + driver);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err.println("\nUnable to instantiate the JDBC driver "
					+ driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println("\nNot allowed to access the JDBC driver "
					+ driver);
			iae.printStackTrace(System.err);
		}
	}

//	private void parseArguments(String[] args) {
//		if (args.length > 0) {
//			if (args[0].equalsIgnoreCase("derbyclient")) {
//				framework = "derbyclient";
//				driver = "org.apache.derby.jdbc.ClientDriver";
//				protocol = "jdbc:derby://localhost:1527/";
//			}
//		}}
	}

