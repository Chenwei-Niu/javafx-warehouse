import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class WarehouseDSC {

	// the date format we will be using across the application
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	/*
		FREEZER, // freezing cold
		MEAT, // MEAT cold
		COOLING, // general Warehousearea
		CRISPER // veg and fruits section

		note: Enums are implicitly public static final
	*/
	public enum SECTION {
		FREEZER,
		MEAT,
		COOLING,
		CRISPER
	};

	private static Connection connection = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;

	public static void connect() throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");


			/* TODO 1-01 - TO COMPLETE ****************************************
			 * change the value of the string for the following 3 lines:
			 * - url
			 * - user
			 * - password
			 */			
			String url = "jdbc:mysql://localhost:3306/warehousedb";
			String user = "root";
			String password = "123456";

			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
  		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new Exception("[ERROR] failed to connect to database");
		}		
	}

	public static void disconnect() throws Exception {
		if(preparedStatement != null) preparedStatement.close();
		if(statement != null) statement.close();
		if(connection != null) connection.close();
	}



	public Item searchItem(String name) throws Exception {
		String queryString = "SELECT * FROM item WHERE name = ?";

		/* TODO 1-02 - TO COMPLETE ****************************************
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */

		Item result = null;

		// Firstly check whether the connection is valid
		if (connection == null) {
			throw new Exception("[ERROR] Null connection");
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(queryString);
				ps.setString(1, name); // fulfill the placeholder in queryString
				rs = ps.executeQuery();
				if (rs.next()) { // i.e. the item exists
					Item item = new Item();
					/* TODO 1-03 - TO COMPLETE ****************************************
					 * - if resultSet has result, get data and create an Item instance
					 */

					item.setName(rs.getString(1));
					item.setExpire(rs.getBoolean(2));
					result = item;
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				ps.close();
				rs.close();
			}

		}
		System.out.println(result);
		return result;
	}

	public Product searchProduct(int id) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM product WHERE id = ?";
		Product result = null;

		/* TODO 1-04 - TO COMPLETE ****************************************
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */
		// Firstly check whether the connection is valid
		if (connection == null) {
			throw new Exception("[ERROR] Null connection");
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(queryString);
				ps.setInt(1, id); // fulfill the placeholder in queryString
				rs = ps.executeQuery();
				if (rs.next()) { // i.e. the product exists

					/* TODO 1-05 - TO COMPLETE ****************************************
					 * - if resultSet has result, get data and create a product instance
					 * - making sure that the item name from product exists in
					 *   item table (use searchItem method)
					 * - pay attention about parsing the date string to LocalDate
					 */
					Product product = new Product();
					Item item = searchItem(rs.getString(2));
					if (item != null) {
						product.setId(rs.getInt(1));
						product.setItem(item);
						product.setQuantity(rs.getInt(4));
						product.setDate(LocalDate.parse(rs.getString(3),dtf));
						product.setSection(SECTION.valueOf(rs.getString(5)));
						result = product;
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				ps.close();
				rs.close();
			}
		}

		System.out.println(result);
		return result;
	}


	public List<Item> getAllItems() throws Exception {
		String queryString = "SELECT * FROM item";
		/* TODO 1-06 - TO COMPLETE ****************************************
		 * - resultSet to execute the statement query
		 */
		List<Item> items = new ArrayList<Item>();

		// Firstly check whether the connection is valid
		if (connection == null) {
			throw new Exception("[ERROR] Null connection");
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(queryString);
				rs = ps.executeQuery();

				/* TODO 1-07 - TO COMPLETE ****************************************
				 * - iterate through the resultSet result, create intance of Item
				 *   and add to list items
				 */
				while (rs.next()) {
					Item item = new Item();
					item.setName(rs.getString(1));
					item.setExpire(rs.getBoolean(2));
					items.add(item);
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				ps.close();
				rs.close();
			}
		}
		System.out.println(Arrays.toString(items.toArray()));
		return items;
	}

	public List<Product> getAllProducts() throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM product";

		/* TODO 1-08 - TO COMPLETE ****************************************
		 * - resultSet to execute the statement query
		 */
		List<Product> products = new ArrayList<Product>();

		if (connection == null){
			throw new Exception("[ERROR] Null connection");
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = connection.prepareStatement(queryString);
				rs = ps.executeQuery();
				/* TODO 1-09 - TO COMPLETE ****************************************
				 * - iterate through the resultSet result, create intance of Item
				 *   and add to list items
				 * - making sure that the item name from each product exists in
				 *   item table (use searchItem method)
				 * - pay attention about parsing the date string to LocalDate
				 */
				while (rs.next()){
					Product product = new Product();
					Item item = searchItem(rs.getString(2));
					if (item != null) {
						product.setId(rs.getInt(1));
						product.setItem(item);
						product.setDate(LocalDate.parse(rs.getString(3),dtf));
						product.setQuantity(rs.getInt(4));
						product.setSection(SECTION.valueOf(rs.getString(5)));
						products.add(product);
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				ps.close();
				rs.close();
			}
		}
		System.out.println(Arrays.toString(products.toArray()));
		return products;
	}


	public int addProduct(String name, int quantity, SECTION section) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate date = LocalDate.now();
		String dateStr = date.format(dtf);
		
		// NOTE: should we check if itemName (argument name) exists in item table?
		//		--> adding a product with a non-existing item name should through an exception

		if (searchItem(name) == null) {
			throw new Exception("[ERROR] The adding product contains non-existing item");
		}

		String command = "INSERT INTO Product VALUES(null, ?, ?, ?, ?)";

		/* TODO 1-10 - TO COMPLETE ****************************************
		 * - preparedStatement to add arguments to the queryString
		 * - resultSet to executeUpdate the preparedStatement query
		 */
		int newId = 0;
		if (connection == null){
			throw new Exception("[ERROR] Null connection");
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = connection.prepareStatement(command);
				ps.setString(1,name);
				ps.setString(2,dateStr);
				ps.setInt(3,quantity);
				ps.setString(4,section.toString());
				ps.execute();
				// retrieving & returning last inserted record id
				rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
				rs.next();
				newId = rs.getInt(1);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				ps.close();
				rs.close();
			}
		}

		System.out.println(newId);
		return newId;		
	}

	public Product useProduct(int id) throws Exception {

		/* TODO 1-11 - TO COMPLETE ****************************************
		 * - search product by id
		 * - check if has quantity is greater one; if not throw exception
		 *   with adequate error message
		 */
		Product product = searchProduct(id);
		if (product == null){
			throw new Exception("[ERROR] The product with id "+ id +" doesn't exits.");
		} else if (product.getQuantity() <= 1){
			throw new Exception("[ERROR] Product quantity value cannot be less than 1");
		}

		String queryString = 
			"UPDATE product " +
			"SET quantity = quantity - 1 " +
			"WHERE quantity > 1 " + 
			"AND id = " + id + ";";


		/* TODO 1-12 - TO COMPLETE ****************************************
		 * - statement execute update on queryString
		 * - should the update affect a row search product by id and
		 *   return it; else throw exception with adequate error message
		 *
		 * NOTE: method should return instance of product
		 */

		if (connection == null){
			throw new Exception("[ERROR] Null Connection");
		} else {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(queryString);
				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("[ERROR] Due to unknown reason, this operation fails.");
			} finally {
				ps.close();
			}
		}
		product.updateQuantity();
		return product;
	}

	public int removeProduct(int id) throws Exception {
		String queryString = "DELETE FROM product WHERE id = " + id + ";";

		/* TODO 1-13 - TO COMPLETE ****************************************
		 * - search product by id
		 * - if product exists, statement execute update on queryString
		 *   return the value value of that statement execute update
		 * - if product does not exist, throw exception with adequate 
		 *   error message
		 *
		 * NOTE: method should return int: the return value of a
		 *		 stetement.executeUpdate(...) on a DELETE query
		 */
		Product product = searchProduct(id);
		if (product == null){
			throw new Exception("[ERROR] The product with id "+ id +" doesn't exits.");
		}

		int influencedRows = 0;
		if (connection == null){
			throw new Exception("[ERROR] Null Connection");
		} else {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(queryString);
				influencedRows = ps.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("[ERROR] Due to unknown reason, this operation fails.");
			} finally {
				ps.close();
			}
		}
		System.out.println(influencedRows);
		return influencedRows;
	}

	// STATIC HELPERS -------------------------------------------------------

	public static long calcDaysAgo(LocalDate date) {
    	return Math.abs(Duration.between(LocalDate.now().atStartOfDay(), date.atStartOfDay()).toDays());
	}

	public static String calcDaysAgoStr(LocalDate date) {
    	String formattedDaysAgo;
    	long diff = calcDaysAgo(date);

    	if (diff == 0)
    		formattedDaysAgo = "today";
    	else if (diff == 1)
    		formattedDaysAgo = "yesterday";
    	else formattedDaysAgo = diff + " days ago";	

    	return formattedDaysAgo;			
	}

	// To perform some quick tests	
	public static void main(String[] args) throws Exception {
		WarehouseDSC myWarehouseDSC = new WarehouseDSC();

//		myWarehouseDSC.connect();
//
//		System.out.println("\nSYSTEM:\n");
//
//		System.out.println("\n\nshowing all of each:");
//		System.out.println(myWarehouseDSC.getAllItems());
//		System.out.println(myWarehouseDSC.getAllProducts());
//
//		int addedId = myWarehouseDSC.addProduct("Milk", 40, SECTION.COOLING);
//		System.out.println("added: " + addedId);
//		System.out.println("deleting " + (addedId - 1) + ": " + (myWarehouseDSC.removeProduct(addedId - 1) > 0 ? "DONE" : "FAILED"));
//		System.out.println("using " + (addedId) + ": " + myWarehouseDSC.useProduct(addedId));
//		System.out.println(myWarehouseDSC.searchProduct(addedId));
//


//		myWarehouseDSC.searchItem("Milk");
//		myWarehouseDSC.searchProduct(5);
//		myWarehouseDSC.getAllItems();
//		myWarehouseDSC.getAllProducts();
//		myWarehouseDSC.addProduct("Tofu",3,SECTION.FREEZER);
//		myWarehouseDSC.useProduct(5);
//		myWarehouseDSC.removeProduct(38);
//		myWarehouseDSC.disconnect();
	}
}