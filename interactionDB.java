import java.awt.BorderLayout;
import java.sql.*;
import java.util.Arrays;
import javax.swing.*;
import java.sql.*;


public class interactionDB extends JFrame{
	
	
	private Connection connection =null;
	private String course_name, instructor_name, number_members,member_name,member_id,member_name_booked,member_id_booked, id_for_book;
	private int course_capacity;
	private String[] courseNameArray,memberNameArray,instructorArray,numberBookedArray,idArray,memberIDBookedArray;
	private String[] memberNameBookedArray;
	private int[] capacityArray;
	private int[] course_ids;
	private int max, already_booked;
	private JFrame viewFrame,viewBookedFrame, memberFrame;
	

	
	//Method to connect to my database
	public void connectionCreate() {
		
		String dbname = "m_17_2057536a";
		String username = "m_17_2057536a";
		String password = "2057536a";
				
		try {
			connection =
			DriverManager.getConnection("jdbc:postgresql://yacata.dcs.gla.ac.uk:5432/"  + dbname,username, password);
		}
		
		catch (SQLException e) {
			System.err.println("Connection Failed!");
			e.printStackTrace();
			return;
			}
		
		if (connection != null) {
			System.out.println("Connection successful");
		}
		else {
			System.err.println("Failed to make connection!");
		}
	}
	
	
	
		
	
	
	//Method to terminate my DB connection
	public void connectionClose() {
		try {
			connection.close();
			System.out.println("Connection closed");
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Connection could not be closed â€“ SQL exception");
		}
	}
	
	
	
	
	
	//Query to return ALL member's names 
	// This can be used for the members
	// button to populate the member
	// jtable with all gym members
	public String memberNameQuery(){

		Statement stmt1 = null;
		memberNameArray = new String[19];

		int count1 = 0;

		String query1 = "SELECT name FROM universitygym.member";

		try {
				stmt1 = connection.createStatement();
				ResultSet rs1 = stmt1.executeQuery(query1);
				
				//the next method of ResultSet allows you to iterate through the results	
				while (rs1.next()) {
					member_name = rs1.getString("name");					
					memberNameArray[count1] = member_name;
					count1++;
				}			
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query1);
		}
		return member_name;
	}
	
	
	
	
	
	
	// The JTable with all the Gym members
	// It will be activated by the members button 
	// of the main GUI
	public void memberTable(){

		memberFrame = new JFrame("Gym members");
		memberFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		memberFrame.setLayout(new BorderLayout());
		memberFrame.setLocation(0,500);
		
		int MEM_ROWS = 19;
		int MEM_COLS = 1;


		String[] heads = {"Member name"};

		String[][] memTable = new String[MEM_ROWS][MEM_COLS];

		for (int i = 0 ; i < MEM_ROWS; i++){
			memTable[i][0] = memberNameArray[i];
		}

		//Create the JTable
		JTable membert = new JTable(memTable, heads);

		//add the table to scroll
		JScrollPane scroll = new JScrollPane(membert);

		memberFrame.add(scroll);
		memberFrame.setVisible(true);
		memberFrame.setSize(300,300);
		memberFrame.repaint();

	}

	
	
	
	


	// A method to return the course name, the instructor, and the max capacity
	// The outcome for every column of the ResultSet
	// will populate the relevant arrays that will be used to make a Jtable
	public void viewInforQuery(){
		courseNameArray = new String[10];
		capacityArray = new int[10];
		instructorArray = new String[10];

		int count = 0;

		Statement stmt = null;
		
		//Inner join query to return the values
		String query = "SELECT universitygym.course.courseName, universitygym.instructor.name, universitygym.course.MaxPlaces "
					+ "FROM universitygym.course "
					+ "INNER JOIN universitygym.instructor "
					+ "ON universitygym.course.InstructorNumber = universitygym.instructor.instructornumber "
					+ "ORDER BY courseid";

		try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				
				//the next method of ResultSet allows you to iterate through the results
				while (rs.next()) {
					// the getString method of the ResultSet object allows you to access 
					//the value for the given column name for the current row in the result 
					//set as a String. If the value is an integer you can use getInt(â€œcol_nameâ€)	 
					course_name = rs.getString("coursename");
					courseNameArray[count] = course_name;
					
					course_capacity = rs.getInt("maxplaces");
					capacityArray[count] = course_capacity;

					instructor_name = rs.getString("name");					
					instructorArray[count] = instructor_name;

					count++;
					
				}		
			}
			catch (SQLException e ) {
				e.printStackTrace();
				System.err.println("error executing query ");
			}			

	}
	
	
	

	
	//Method to query the number of members booked for each course
	public String numberMembers() {
		
		int[] course_ids = {5,17,22,31,54,57,78,79,90,123};
		numberBookedArray = new String[10];
		int counts = 0;
		
		
		for(int id_count = 0; id_count < course_ids.length; id_count++) {
			
			Statement stmt = null;
			
			//For each course id, a doulbe inner join will run, to count the
			// members that are currently booked on the course.
			// This will also give the rows that have null values
			// i.e. 0 members for courses that have full availability
			String query = String.format("SELECT COUNT(1)\r\n" + 
					"FROM universitygym.member\r\n" + 
					"INNER JOIN universitygym.membercourse\r\n" + 
					"ON universitygym.member.membershipnumber = universitygym.membercourse.membershipnumber\r\n" + 
					"INNER JOIN universitygym.course\r\n" + 
					"ON universitygym.course.courseid = universitygym.membercourse.courseid\r\n" + 
					"WHERE universitygym.course.courseid = %s",Integer.toString(course_ids[id_count]));
						
					try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						//the next method of ResultSet allows you to iterate through the results
						while (rs.next()) {
							// the getString method of the ResultSet object allows you to access 
							//the value for the given column name for the current row in the result 
							//set as a String. If the value is an integer you can use getInt(â€œcol_nameâ€)	 
							number_members = Integer.toString(rs.getInt("count"));

							numberBookedArray[counts] = number_members;	
							counts++;
						}			
					}
					catch (SQLException e ) {
						e.printStackTrace();
						System.err.println("error executing query " + query);
					}				
		}
		return number_members;
	}
	
	
	
	
	//Create a JTable with the view information, containing
	// information from the previous two methods.
	// It will be triggered by the viewButton of the GUI
	public void viewTable() {
		
		viewFrame = new JFrame("Current view");
		viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		viewFrame.setLayout(new BorderLayout());
				
		int ROWS = 10;
		int COLS = 4;
		
		
		String[] headings = {"Course name", "Instructor name","Maximum places","# members booked"};
		
		String[][] table = new String[ROWS][COLS];
		
		for(int i = 0;i<ROWS;i++) {
			table[i][0] = courseNameArray[i];
			table[i][1] = instructorArray[i];
			table[i][2] = Integer.toString(capacityArray[i]);	
			table[i][3] = numberBookedArray[i];
		}
			
			
		
		//create the JTable with these data
		JTable viewTable = new JTable(table,headings);
		
		//add viewTable to scrollpane
		JScrollPane scrollPane = new JScrollPane(viewTable);
		
		viewFrame.add(scrollPane);
		viewFrame.setVisible(true);
		viewFrame.setSize(650, 250);
		viewFrame.revalidate();
		viewFrame.repaint();
		 
						
	}

		
	
	
	
	//Query to return an id for the member to be booked or deleted
	public String getBookingID(String mem_name){

		Statement stmt = null;
				

		String query = String.format("SELECT membershipnumber FROM universitygym.member WHERE name = '%s';", mem_name );

		try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
						
				//the next method of ResultSet allows you to iterate through the results	
				while (rs.next()) {
					id_for_book = rs.getString("membershipnumber");					
				}			
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		return id_for_book;
	}
	
	

	
	//Method to query a booking with insert 
	public void bookMember(String memNum, int cid, int bookNum ) {
		Statement stmt = null;
		String query = String.format("INSERT INTO universitygym.membercourse (membershipnumber,courseid,bookingnumber) VALUES ('%s',%d,%d)",memNum,cid,bookNum);

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
	
			System.err.println("Member booked succesfully");		
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
	}
	
	
	
	//Method to query a member to be deleted from a course
	public void delMember(String memNum, int cid) {
		Statement stmt = null;
		String query = String.format("DELETE FROM universitygym.membercourse WHERE membershipnumber = '%s' AND courseid = %d;",memNum,cid);
	
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
			
			System.err.println("Member unenrolled succesfully");
		}
		catch(SQLException e) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		

	}
	
	
	
	
	

	//Query to return the member's names for a parameter courseid
	// It will be used by the booking and delete methods to check if 
	// a member is already enrolled or not, to avoid double booking
	// or null deletion. Also for the JCombo box to populate the members
	// that are booked on each course in the list
	public String[] memberNameBooked(int course_number){
			
		Statement stmt = null;
		memberNameBookedArray = new String[19];

		int count = 0;

		String query = String.format("SELECT universitygym.member.name "
				+ "FROM universitygym.member INNER JOIN universitygym.membercourse "
				+ "ON universitygym.member.membershipnumber = universitygym.membercourse.membershipnumber "
				+ "where courseid = %s",course_number);
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
					
			//the next method of ResultSet allows you to iterate through the results	
			while (rs.next()) {
				member_name_booked = rs.getString("name");					
				memberNameBookedArray[count] = member_name_booked;
				count++;			
			}
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		
		for(int i = 0; i <memberNameBookedArray.length; i++) {
			if (memberNameBookedArray[i] == null) {
				memberNameBookedArray[i] ="";
			}
		}	
		return memberNameBookedArray;
		
	}
	
	
	
		
	//Query to return the member's ids for a parameter courseid
	// as it will be used for the jtable showing the members
	// booked per course (i.e. the jcombo box options)
	public String memberIdBooked(int member_id){
					
		Statement stmt = null;
		memberIDBookedArray = new String[10];

		int count = 0;

		String query = String.format("SELECT universitygym.member.membershipnumber\r\n" + 
				"FROM universitygym.member\r\n" + 
				"INNER JOIN universitygym.membercourse\r\n" + 
				"ON universitygym.member.membershipnumber = universitygym.membercourse.membershipnumber\r\n" + 
				"where courseid = %s",member_id);
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
							
			//the next method of ResultSet allows you to iterate through the results	
			while (rs.next()) {
				member_id_booked = rs.getString("membershipnumber");					
				memberIDBookedArray[count] = member_id_booked;
				count++;
			}			
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		return member_id_booked;
	}
	
	
	
	
	
	
	//Create a JTable with the members booked
	// on each course of the combo box.
	public void viewBookedMemberTable(String myCourse) {
			
		viewBookedFrame = new JFrame(myCourse + " : Booked members");
		viewBookedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		viewBookedFrame.setLayout(new BorderLayout());
					
		int ROWS = 10;
		int COLS = 2;
				
		String[] headings = {"Member name", "Member ID"};			
		String[][] table = new String[ROWS][COLS];
			
		for(int i = 0;i<ROWS;i++) {
			table[i][0] = memberNameBookedArray[i];
			table[i][1] = memberIDBookedArray[i];
		}
				
							
		//create the JTable with these data
		JTable viewBookedTable = new JTable(table,headings);
			
		//add viewTable to scrollpane
		JScrollPane scrollPane1 = new JScrollPane(viewBookedTable);
			
		viewBookedFrame.add(scrollPane1);
		viewBookedFrame.setVisible(true);
		viewBookedFrame.setSize(370, 250);
		viewBookedFrame.revalidate();
		viewBookedFrame.repaint();						
	}
	
	
	

	// This method will use a query to determine
	// if a course has any available places left.
	// This will be necessary when a member wants to book
	// a course.
	public boolean areTherePlaces(int id) {
		
		//Returning the maxplaces info
		Statement stmt = null;
		String query = String.format("SELECT universitygym.course.MaxPlaces FROM universitygym.course WHERE courseid = %s",Integer.toString(id));
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
							
			//the next method of ResultSet allows you to iterate through the results	
			while (rs.next()) {
				max = rs.getInt("maxplaces");					
			}			
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		
		
		//Returning the number of members
		// that are already booked on the course
		Statement stmt1 = null;
		String query1 = String.format("SELECT count(*) from universitygym.membercourse where courseid  = %s",Integer.toString(id));
		try {
			stmt1 = connection.createStatement();
			ResultSet rs1 = stmt1.executeQuery(query1);
							
			//the next method of ResultSet allows you to iterate through the results	
			while (rs1.next()) {
				already_booked = rs1.getInt("count");					
			}			
		}
		catch (SQLException e ) {
			e.printStackTrace();
			System.err.println("error executing query " + query1);
		}
		
		
		// Use the two previous results
		// to determine if the is place in the course
		if (already_booked >= max) {
			return false;
		}
		else {
			return true;
		}
		
	}
	
}