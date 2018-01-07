import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;

public class gymGUI extends JFrame implements ActionListener{

	//initialising instance variables
	private JButton bookButton, delButton, viewButton,memberButton;
	private JLabel courseLabel, memberLabel;
	private JPanel viewPanel, comboPanel,bookPanel;
	private JComboBox courseCombo;
	private interactionDB dbObj;
	private String[] courseList;
	private String[] memberNameBookedArray;
	private int id;
	private String nameForBook;
	private JFrame viewFrame,viewBookedFrame;
	

	
	//constructor of the class
	public gymGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350,150);
		setLocation(300,300);
		setTitle("University of Glasgow Gym");
		layoutComponents();
	}


	//Create three components. Top panel with view button
	// middle panel with course combobox and view members button
	// bottom with members combobox and book/ delete buttons.
	private void layoutComponents(){
		layoutTop();
		layoutMiddle();
		layoutBottom();
	}




	//Create top panel and place it at the north side of the frame
	private void layoutTop(){
		
		//The panel where the View and Member button will
		// be placed within the GUI.
		viewPanel = new JPanel();
		
		viewButton = new JButton("VIEW CURRENT");
		viewButton.addActionListener(this);	
		
		memberButton = new JButton("Gym Members");
		memberButton.addActionListener(this);	
				
		viewPanel.add(viewButton);
		viewPanel.add(memberButton);

		add(viewPanel,BorderLayout.NORTH);
			
	}
	

	
	//Create middle panel and place it at the centre
	// of the frame. It contains a combo box with
	// a drop down list of the available courses. Upon
	// selecting a course, the user can access the
	// information on the members booked for this course
	private void layoutMiddle(){
		
		comboPanel = new JPanel();
		
		courseLabel = new JLabel("Courses");
		memberLabel = new JLabel("Members");
		
		
		// New JCombo to contain a list of available courses
		courseCombo = new JComboBox();
		
		//The first entry is just introductory
		courseCombo.addItem("Select");
		
		//An array of the courses. Since the number will
		// remain unchanged, this can be harcoded in the class
		String[] courseList = {"Pilates (id: 90)","Spinning (id: 17)","Swimming Fitness (id: 22)",
				"Aerobics (id: 5)","Riding (id: 31)","Fencing (id: 57)","Aikido (id: 78)",
				"Judo (id: 79)","Lacrosse (id: 54)","Trampoline (id: 123)"};
		
		
		//Populate the drop down menu of the combo box
		for(int i =0; i < courseList.length;i++) {
			courseCombo.addItem(courseList[i]);
		}	
		
		comboPanel.add(courseLabel);
		comboPanel.add(courseCombo);
		
		add(comboPanel,BorderLayout.CENTER);
		
		
		
		//Determine what happens if I click on a course to see the members booked on it
		courseCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				//get the source of the item in combobox
				courseCombo = (JComboBox)event.getSource();
				Object selected = courseCombo.getSelectedItem();
				
				
				//for each course in the courseList
				// I keep the id as an integer by using
				// the lastIndexOf
				// and I pass it to the methods that
				// return the member name and his id
				//for the course id in question
				for(int i = 0; i < 10; i++) {
					//Each of the courses in the list
					String the_course = courseList[i];
					
					//its length
					int length = courseList[i].length();
					
					//The substring that keeps only the id as a string
					String id_str = (the_course.substring(the_course.lastIndexOf(" "), length-1 )).trim();
					
					//Cast it to an integer
					int the_id = Integer.parseInt(id_str);
					
					// for the course that is selected in the combo menu
					// call the necessary methods for the database class
					if (selected.toString().equals(the_course)) {
						dbObj = new interactionDB();
						dbObj.connectionCreate();
						dbObj.memberNameBooked(the_id);
						dbObj.memberIdBooked(the_id);
						dbObj.viewBookedMemberTable(the_course);
						dbObj.connectionClose();
					}
					
				}
						
			}
		});	
		
	}
		
		
			

	//Create bottom panel and place it at S
	private void layoutBottom(){
			
		bookPanel = new JPanel();
		
		//The book and the delete buttons
		bookButton = new JButton("BOOK MEMBER");
		bookButton.addActionListener(this);	
		delButton = new JButton("DELETE BOOKING");
		delButton.addActionListener(this);	

		bookPanel.add(bookButton);
		bookPanel.add(delButton);

		add(bookPanel,BorderLayout.SOUTH);
	}
		
		
		

	// Event handling for the buttons
	public void actionPerformed(ActionEvent e) {
		
		//Create a new DB object and make a connection
		dbObj = new interactionDB();
		dbObj.connectionCreate();
		
		
		
		// Get the JTable with the
		//course names, the instructor
		// running them, the maximum places
		//available and the number of members on these courses
		if(e.getSource() == viewButton) {
			dbObj.viewInforQuery();
			dbObj.numberMembers();
			dbObj.viewTable();
		}
		
		
		//A list of all gym members
		// This will be valuable when
		// booking or deleting a member from
		// a course
		if(e.getSource() == memberButton) {
			dbObj.memberNameQuery();
			dbObj.memberTable();
		}
		
		

		// Handling the booking functionality
		if(e.getSource() == bookButton) {
			
			//If the user wants to book a member to a course
			// a window is shown for the course id input
			// If the course has no more spaces left, the user will be notified
		
			String input_id = JOptionPane.showInputDialog("Enter course id: ");
			
			if (input_id == null || input_id.equals("")) {
				JOptionPane.showMessageDialog(null, "Please enter a course id","Message",JOptionPane.ERROR_MESSAGE);
			}
			else {				
				try {
					int id = Integer.parseInt(input_id);

					dbObj.areTherePlaces(id);
						
					if (dbObj.areTherePlaces(id) == false) {
						JOptionPane.showMessageDialog(null,"NO AVAILABLE PLACES","Message",JOptionPane.ERROR_MESSAGE);							
					}
					else {
						System.out.println("PLACES AVAILABLE");

				
						//If there are available places, proceed to member name input
						String[] memberNameBookedArray = dbObj.memberNameBooked(id);
				
			
						//if the name is already booked, notify user
						for (int k = 0; k < memberNameBookedArray.length; k++) {
							String nameForBook = JOptionPane.showInputDialog("Enter member name: ");
							if(Arrays.asList(memberNameBookedArray).contains(nameForBook)) {
								JOptionPane.showMessageDialog(null,"MEMBER ALREADY BOOKED ON THIS COURSE","Message",JOptionPane.ERROR_MESSAGE);
							}
							else {
								System.out.println("Proceed to Booking");	
						
								// Calling the method from the DB class
								// that returns the  membershio number
								// for the member name. This will be needed
								// when a booking is made
								String memID = dbObj.getBookingID(nameForBook);
						
								
								//Generate a random number for the booking id
								Random rand = new Random();
								int bookingNumber = rand.nextInt(100000);
							
								//Call the book method that executes the insert query
								dbObj.bookMember(memID,id,bookingNumber);
							}
							break;
						}
					}
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "WRONG","Message",JOptionPane.ERROR_MESSAGE);
				}

			}
		}
	
	
	

		// Handling the delete member functionality
		if (e.getSource() == delButton){

			String input_id = JOptionPane.showInputDialog("Enter course id to delete member: ");
			
			//If input in null, start over
			if (input_id == null || input_id.equals("")) {
				JOptionPane.showMessageDialog(null, "Please enter a course id","Message",JOptionPane.ERROR_MESSAGE);
			}
			
			
			else {
				try {
					int id = Integer.parseInt(input_id);

					String nameForDel = JOptionPane.showInputDialog("Enter member name to delete: ");
					String[] memberNameBookedArray = dbObj.memberNameBooked(id);

					
					for(int k = 0 ; k < memberNameBookedArray.length; k++){
					
						//If the name is already booked , then it can 
						// be deleted from it. Otherwise the user will be
						// notified that no such member is on the course
						if(Arrays.asList(memberNameBookedArray).contains(nameForDel)){

							System.out.println("Preraring deleting member");
							
							String memID = dbObj.getBookingID(nameForDel);
							
							// The method from the database class that
							// executes the delete query
							dbObj.delMember(memID,id);										
						}
						else{
							JOptionPane.showMessageDialog(null,"Member is not currently booked on this course","Message",JOptionPane.ERROR_MESSAGE);
							break;
						}
					}
				}
				catch(Exception ef) {
					JOptionPane.showMessageDialog(null, "WRONG","Message",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		dbObj.connectionClose();

	}
	
}