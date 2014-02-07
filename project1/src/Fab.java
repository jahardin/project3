//Created by:Jacobus Harding, Trevor Miller

import java.util.Scanner;
import java.util.StringTokenizer;
import java.sql.*;

public class Fab 
{
	static Scanner keyboard = new Scanner(System.in);
	static Connection connection;
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		// Incorporate mySQL driver
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		boolean keepGoing = true;
		while(keepGoing)
		{


			boolean res = logIn();

			if(res == true)
			{
				System.out.println("Logged In \n");	
				commandList();
				System.out.println("Logged Out \n");	
			}

			System.out.println("\nKeep Going? If not type 'no'");
			String response = keyboard.nextLine();

			if(response.equalsIgnoreCase("n") || response.equalsIgnoreCase("NO"))
			{
				keepGoing = false;
				System.out.println("Bye");
			}

		}

	}



	public static boolean logIn()
	{
		System.out.println("Username:");
		String uname = keyboard.nextLine();

		System.out.println("Password:");
		String upass = keyboard.nextLine();

		// Connect to the test database
		try {connection = DriverManager.getConnection("jdbc:mysql:///moviedb",uname, upass);} 
		catch (SQLException e) {
			System.out.println("Invalid.");
			return false;
		}
		return true;
	}

	public static void provideMetadata(ResultSet result, String tableName) throws SQLException
	{
		System.out.println("TABLE: " + tableName);
		printAttributes(result);

	}

	public static void printAttributes(ResultSet result) throws SQLException
	{
		ResultSetMetaData metadata = result.getMetaData();
		for (int i = 1; i <= metadata.getColumnCount(); i++)
		{
			System.out.print(metadata.getColumnLabel(i) + "("+metadata.getColumnTypeName(i)+")" + "\t");
		}
		System.out.println("\n");
	}

	public static void printTable(ResultSet result) throws SQLException
	{
		ResultSetMetaData metadata = result.getMetaData();
		for (int i = 1; i <= metadata.getColumnCount(); i++)
		{
			System.out.print(metadata.getColumnLabel(i) + "("+metadata.getColumnTypeName(i)+")" + "\t");
		}
		System.out.println();
		while (result.next())
		{
			for (int i = 1; i <= metadata.getColumnCount(); i++)
				System.out.print(result.getString(i) + "\t");
			System.out.println();
		}
		System.out.println();
	}

	public static void commandList() throws SQLException
	{
		boolean moreCommands = true;
		Boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false, flag5 = false; 
		Statement con = connection.createStatement();
		String[] commands = {"-p", "-s", "-c", "-d", "-m", "-a", "-x", "-xxx", "-proc"};

		while(moreCommands)
		{


			System.out.println("Command List:"
					+ "\n\t-p \tPrint out the movies featuring a given star"
					+ "\n\t-s \tInsert a new star"
					+ "\n\t-c \tInsert a new customer"
					+ "\n\t-d \tDelete a customer"
					+ "\n\t-m \tProvide metadata"
					+ "\n\t-proc \tCall a procedure"
					+ "\n\t-a \tEnter a SQL command"
					+ "\n\t-x \tLogout"
					+ "\n\t-XXX \tClose Program");

			System.out.println("What command do you want to do?");
			String cmd = keyboard.nextLine();
			for(String c : commands)
				if(!cmd.equalsIgnoreCase(c))
					flag5 = false;
				else
				{
					flag5 = true;
					break;
				}
			if(!flag5)
				System.out.println("Invalid command or no command was entered \n   \n");

			ResultSet result = null;
			PreparedStatement ps = null;
			StringTokenizer st = null;

			switch(cmd)
			{
			case "-p":
				System.out.println("Enter star id:");
				String starID = keyboard.nextLine();
				int id = 0;


				if(!starID.equals(""))
				{
					try{id = Integer.parseInt(starID);} catch(NumberFormatException e)
					{System.out.println("An invalid Star ID was entered \n   \n"); break;};

					if(id != 0)
						result = con.executeQuery("Select * from movies as m, stars_in_movies AS SM Where star_id = " + id + " AND m.id = SM.movie_id");
					flag1 = true; 
				}


				else
				{

					System.out.println("What is the first name");
					String F = keyboard.nextLine();

					System.out.println("What is the last name");
					String L = keyboard.nextLine();

					if(!F.equals("") && !L.equals(""))
					{
						result = con.executeQuery("Select  * From movies as m, stars_in_movies as SM Where SM.star_id in " +
								"(Select s.id From stars as s Where s.first_name = '" + F + "' AND s.last_name = '" + L + "') AND m.id = SM.movie_id");
						flag2 = true; 
					}

					else if(!F.equals("") && L.equals(""))
					{
						result = con.executeQuery("Select  * From  movies as m, stars_in_movies as SM Where SM.star_id in " +
								"(Select s.id From stars as s Where s.first_name = '" + F + "') AND m.id = SM.movie_id");
						flag3 = true;
					}

					else if(F.equals("") && !L.equals(""))
					{
						result = con.executeQuery("Select  * From  movies as m, stars_in_movies as SM Where SM.star_id in " +
								"(Select s.id From stars as s Where s.last_name = '" + L + "') AND m.id = SM.movie_id");
						flag4 = true; 
					}

					else
					{
						System.out.println("No search terms were entered \n   \n");
						break;
					}

				}

				if(!result.isBeforeFirst() && flag1)
				{
					System.out.println("The Star Id entered did not return any results \n   \n");
					break;
				}

				else if(!result.isBeforeFirst() && (flag2 || flag3 || flag4))
				{
					System.out.println("The name entered did not return any results \n   \n");
					break;
				}
				// print table's contents, field by field

				try {
					while (result.next())
					{
						System.out.println();
						System.out.println("Movie ID = " + result.getInt(1));
						System.out.println("Movie Title= " + result.getString(2));
						System.out.println("Year= " + result.getInt(3));
						System.out.println("Director= " + result.getString(4));
						System.out.println("Banner URL= " + result.getString(5));
						System.out.println("Trailer URL= " + result.getString(6));
						System.out.println("Star ID= " + result.getInt(7));
						System.out.println();
					}
				}
				catch(Exception e){System.out.println("Invalid Information");}
				break;

			case "-s":
				System.out.println("Enter the Star's name");
				String N = keyboard.nextLine();
				st = new StringTokenizer(N);
				if(N.isEmpty())
				{
					System.out.println("A name was not entered\n ");
					break;
				}

				System.out.println("Enter the Star's Date of Birth: Format(yyyy-mm-dd)");
				String D = keyboard.nextLine();
				if(D.isEmpty())
					D = "0001-01-01";

				System.out.println("Enter the Star's photo URL: Format(http://...)");
				String U = keyboard.nextLine();
				if(U.isEmpty())
					U = "";

				if(st.countTokens() == 1)
				{
					ps = connection.prepareStatement("Insert into stars (first_name, last_name, dob, photo_url) " +
							"Values ('' , '" + N + "', '" + D + "', '" + U + "' )");
				}
				else {
					String FN = st.nextToken();
					String LN = st.nextToken();
					ps = connection.prepareStatement("Insert into stars (first_name, last_name, dob, photo_url) " +
							"Values ( '" + FN + "', '" + LN + "', '" + D + "', '" + U + "' )");
				}
				System.out.println("Star successfully entered into database\n");
				ps.executeUpdate();
				break;

			case "-c":
				System.out.println("All fields are required\n Enter the Customer's name. ");
				String NN = keyboard.nextLine();
				st = new StringTokenizer(NN);
				if(NN.isEmpty())
				{
					System.out.println("A name was not entered");
					break;
				}

				System.out.println("Enter the Customer's Credit Card Number");
				String C = keyboard.nextLine();
				//check if credit card exists, if exists insert customer and all info into table
				result = con.executeQuery("select id from creditcards where id='" + C + "'");
				if(!result.isBeforeFirst())
				{
					System.out.println("Not an authorized credit card\n ");
					break;
				}

				System.out.println("Enter the Customer's address\n");
				String A = keyboard.nextLine();
				System.out.println(A);
				if(A.isEmpty())
				{
					System.out.println("An address was not entered\n ");
					break;
				}

				System.out.println("Enter the Customer's email\n");
				String E = keyboard.nextLine();
				if(E.isEmpty())
				{
					System.out.println("An email was not entered\n");
					break;
				}

				System.out.println("Enter the Customer's password");
				String P = keyboard.nextLine();
				if(P.isEmpty())
				{
					System.out.println("A password was not entered");
					break;
				}


				if(result.isBeforeFirst() && st.countTokens() == 1) //if the result set has something in it and there is only one name entered
				{
					ps = connection.prepareStatement("Insert into customers (first_name, last_name, cc_id, address, email, password) " +
							"Values ('' , '" + NN + "', '" + C + "', '" + A + "', '" + E + "', '" + P + "' )");
				}
				else if(result.isBeforeFirst())
					ps = connection.prepareStatement("Insert into customers (first_name, last_name, cc_id, address, email, password) " +
							"Values ( '" + st.nextToken() + "', '" + st.nextToken() + "', '" + C + "', '" + A + "', '" + E + "', '" + P + "' )");

				ps.executeUpdate();
				break;

			case "-proc":
				//Movie:title, year, director, banner, trailer,
				//Genre: name
				//Star: first_name, last_name, dob, photo_url
				//stars_in_movies: star_id, movie_id
				//genres_in_movies: genre_id, movie_id
				System.out.println("Which procedure would you like to call?(Enter the number)");
				System.out.println("1\t Add Movie\n2\t Add Star");
				int procedureCalled = Integer.parseInt(keyboard.nextLine());
				String proc = "";
				if(procedureCalled == 1)//addmovie
				{

					System.out.println("Adding movie information...");
					System.out.println("Title of movie:");
					String title = keyboard.nextLine();

					System.out.println("Year movie was made:");
					int year = Integer.parseInt(keyboard.nextLine());

					System.out.println("Director of movie:");
					String director = keyboard.nextLine();

					System.out.println("Banner URL of movie:");
					String banner_url = keyboard.nextLine();

					System.out.println("Trailer URL of movie:");
					String trailer_url = keyboard.nextLine();

					System.out.println("Adding genre information...");
					System.out.println("SINGLE genre of movie:");
					String genre = keyboard.nextLine();
					
					proc = "{call insertMovie_Genre_Proc('"+title+"', "+year+", '"+director+"', '"+banner_url +"', '"+ trailer_url +"', '"+ genre + "')}";
					
					
					
				}
				else if(procedureCalled == 2)
				{


					System.out.println("Adding star information...");
					System.out.println("First Name of star");
					String starFirstName = keyboard.nextLine();

					System.out.println("Last Name of star");
					String starLastName = keyboard.nextLine();

					System.out.println("Date of birth of star");
					String dob = keyboard.nextLine();

					System.out.println("Photo URL of star");
					String photo_url = keyboard.nextLine();

					System.out.println("Adding to stars_in_movies");
					System.out.println("The star's ID");
					int IDstar = Integer.parseInt(keyboard.nextLine());
				}
				/*				
				System.out.println("The movie's ID");
				int IDmovie = Integer.parseInt(keyboard.nextLine());

				System.out.println("Adding to genres_in_movies...");
				System.out.println("The genre's ID");
				int IDgenre = Integer.parseInt(keyboard.nextLine());
				 */	


				CallableStatement cs = null;
				System.out.println("Callin a procedure");
				cs = connection.prepareCall(proc);
				cs.execute();




				break;

			case "-d":
				System.out.println("Enter a customer's name to be deleted. \nEnter first and last name of customer to be deleted.\n" +
						"If customer only has a last name enter only the last name");

				String DN = keyboard.nextLine();
				st = new StringTokenizer(DN);
				if(DN.isEmpty())
				{
					System.out.println("A customer's name was not entered\n ");
					break;
				}
				else if(st.countTokens() > 2)
				{
					System.out.println("The input contained more tokens than just a first and last name");
					break;
				}
				else if(st.countTokens() == 2)
				{
					String firstName = st.nextToken();
					String lastName = st.nextToken();
					result = con.executeQuery("select first_name, last_name from customers where first_name='" + firstName + "' AND last_name='" + lastName + "' ");
					if(!result.isBeforeFirst())
					{
						System.out.println("A customer with the name entered is not in the database\n ");
						break;
					}
					System.out.println();
					ps = connection.prepareStatement("Delete from customers where first_name='" + firstName + "' AND last_name='" + lastName + "'");
					ps.executeUpdate();
					System.out.println("Customer deleted successfully\n");
					break;
				}
				else
				{
					String lastName = st.nextToken();
					result = con.executeQuery("select last_name from customers where last_name='" + lastName + "' ");
					boolean isMoreThanOneRow = result.first() && result.next(); // checks if resulting tables has more than 1 row, if it does = true

					if(isMoreThanOneRow)
					{
						System.out.println("There is more than one customer with that last name\nAre you looking for one of these people?");
						result = con.executeQuery("select id, first_name, last_name from customers where last_name='" + lastName + "'");
						while (result.next())
						{
							System.out.println(result.getInt(1) + " " + result.getString(2) + "\t\t" + result.getString(3));
						}
						System.out.println("Which " + lastName + " do you want to delete? (Enter ID only)");
						String customerID = keyboard.nextLine();
						result = con.executeQuery("select first_name, last_name from customers where id='" + customerID + "' AND last_name='"+ lastName + "'");
						if(!result.isBeforeFirst())
						{
							System.out.println("The " + lastName + " with ID " + customerID + " is not in the database so cannot be deleted");
							break;
						}
						else
						{
							ps = connection.prepareStatement("Delete from customers where id='" + customerID + "'");
							ps.executeUpdate();
							System.out.println("Customer was successfully deleted");
							break;
						}
					}
					else if(!result.isBeforeFirst())
					{
						System.out.println("A customer with the last name entered is not in the database\n ");
						break;
					}

					ps = connection.prepareStatement("Delete from customers where last_name='" + lastName + "'");
					ps.executeUpdate();
					System.out.println("Customer successfully deleted");
				}

				break;
			case "-m":
				System.out.println("Providing Metadata of database");
				provideMetadata(con.executeQuery("select * from creditcards"), "creditcards");
				provideMetadata(con.executeQuery("select * from customers"), "customers");
				provideMetadata(con.executeQuery("select * from genres_in_movies"), "genres_in_movies");
				provideMetadata(con.executeQuery("select * from movies"), "movies");
				provideMetadata(con.executeQuery("select * from sales"), "sales");
				provideMetadata(con.executeQuery("select * from stars"), "stars");
				provideMetadata(con.executeQuery("select * from stars_in_movies"), "stars_in_movies");
				/*DatabaseMetaData md = connection.getMetaData();
				System.out.println(md.getSchemas().);*/
				break;

			case "-a":
				System.out.println("Enter a SELECT/UPDATE/DELETE/INSERT SQL command");
				String sqlCommand = keyboard.nextLine();
				if(sqlCommand.isEmpty())
				{
					System.out.println("No command was entered");
					break;
				}
				else
				{
					st = new StringTokenizer(sqlCommand);
					try{
						String token = st.nextToken();
						if(token.equalsIgnoreCase("select"))
						{
							System.out.println("Selecting records");
							result = con.executeQuery(sqlCommand);
							printTable(result);
						}
						else
						{

							ps = connection.prepareStatement(sqlCommand);
							ps.executeUpdate();
							if(token.equalsIgnoreCase("insert"))
								System.out.println("Record successfully inserted");
							else if(token.equalsIgnoreCase("delete"))
								System.out.println("Record successfully deleted");
							else if(token.equalsIgnoreCase("update"))
								System.out.println("Record successfully updated");

							System.out.println(ps.getUpdateCount() + " updates to the database were successful");
						}
					} 

					catch(SQLException e)
					{System.out.println("SQL statement was invalid or either tables/records didn't exist\n" + e.getMessage()); 
					break;}

				}
				break;

			case "-x":
				moreCommands = false;
				break;

			case "-XXX":
				System.out.println("Bye");
				System.exit(0);
			}

		}
	}

}

