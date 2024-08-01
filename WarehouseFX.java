import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.lang.Thread.*;

import java.util.*;
import java.io.*;
import java.util.function.Predicate;

import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;

public class WarehouseFX extends Application {

	// used as ChoiceBox value for filter
	public enum FILTER_COLUMNS {
		ITEM,
		SECTION,
		BOUGHT_DAYS_AGO
	};
	
	// the data source controller
	private WarehouseDSC warehouseDSC;

	public void init() throws Exception {
		// creating an instance of the data source controller to be used
		// in this application
		warehouseDSC = new WarehouseDSC();

		/* TODO 2-01 - TO COMPLETE ****************************************
		 * call the data source controller database connect method
		 * NOTE: that database connect method throws exception
		 */
		try {
			WarehouseDSC.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot connect to database due to unknown reason. Please check the configuration.");
		}
	}

	public void start(Stage stage) throws Exception {

		/* TODO 2-02 - TO COMPLETE ****************************************
		 * - this method is the start method for your application
		 * - set application title
		 * - show the stage
		 */
		stage.setTitle("What's In My Warehouses");
		build(stage);
		stage.show();


		/* TODO 2-03 - TO COMPLETE ****************************************
		 * currentThread uncaught exception handler
		 */

		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println(e);

			}
		});

//		warehouseDSC.useProduct(999);

	}

	public void build(Stage stage) throws Exception {

		// Create table data (an observable list of objects)
		ObservableList<Product> tableData = FXCollections.observableArrayList();

		// Define table columns
		TableColumn<Product, String> idColumn = new TableColumn<Product, String>("Id");
		TableColumn<Product, String> itemNameColumn = new TableColumn<Product, String>("Item");
		TableColumn<Product, Integer> quantityColumn = new TableColumn<Product, Integer>("QTY");
		TableColumn<Product, String> sectionColumn = new TableColumn<Product, String>("Section");
		TableColumn<Product, String> daysAgoColumn = new TableColumn<Product, String>("Bought");
		
		/* TODO 2-04 - TO COMPLETE ****************************************
		 * for each column defined, call their setCellValueFactory method 
		 * using an instance of PropertyValueFactory
		 */
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		sectionColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
		daysAgoColumn.setCellValueFactory(new PropertyValueFactory<>("daysAgo"));



		// Create the table view and add table columns to it
		TableView<Product> tableView = new TableView<Product>();


		/* TODO 2-05 - TO COMPLETE ****************************************
		 * add table columns to the table view create above
		 */
		tableView.getColumns().addAll(idColumn,itemNameColumn,quantityColumn,sectionColumn,daysAgoColumn);


		//	Attach table data to the table view
		tableView.setItems(tableData);


		/* TODO 2-06 - TO COMPLETE ****************************************
		 * set minimum and maximum width to the table view and each columns
		 */
		tableView.setMinWidth(770);

		tableView.getColumns().get(0).setMinWidth(50);
		tableView.getColumns().get(1).setMinWidth(220);
		tableView.getColumns().get(2).setMinWidth(55);
		tableView.getColumns().get(3).setMinWidth(135);
		tableView.getColumns().get(4).setMinWidth(135);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);



		/* TODO 2-07 - TO COMPLETE ****************************************
		 * call data source controller get all products method to add
		 * all products to table data observable list
		 */
		tableData = FXCollections.observableList(warehouseDSC.getAllProducts());
		tableView.setItems(tableData);

		// =====================================================
		// ADD the remaining UI elements
		// NOTE: the order of the following TODO items can be 
		// 		 changed to satisfy your UI implementation goals
		// =====================================================


		// THE ALERTS
		Alert alert = new Alert(Alert.AlertType.NONE); // Added by myself
		Alert errorMessages = new Alert(Alert.AlertType.ERROR);

		/* TODO 2-08 - TO COMPLETE ****************************************
		 * filter container - part 1
		 * add all filter related UI elements you identified
		 */
		TextField textField = new TextField();
		textField.setMaxWidth(170);
		Label label = new Label("Filter By:");
		label.setTextAlignment(TextAlignment.CENTER);
		ChoiceBox choiceBox = new ChoiceBox();
		choiceBox.getItems().add("ITEM");
		choiceBox.getItems().add("SECTION");
		choiceBox.getItems().add("BOUGHT_DAYS_AGO");
		CheckBox checkBox = new CheckBox("Show Expire Only");
		checkBox.setDisable(true);
		Button add = new Button("ADD");
		Button updateOne = new Button("UPDATE ONE");
		Button delete = new Button("DELETE");
		Label label1 = new Label("Item");
		Label label2 = new Label("Section");
		Label label3 = new Label("Quantity");
		ComboBox<Item>comboBox = new ComboBox<>();
		ChoiceBox sectionChoiceBox = new ChoiceBox();
		TextField quantityTextField = new TextField();
		Button clearButton = new Button("CLEAR");
		Button saveButton = new Button("SAVE");
		HBox addDetails = new HBox();
		VBox vBox1 = new VBox(label1,comboBox);
		VBox vBox2 = new VBox(label2,sectionChoiceBox);
		VBox vBox3 = new VBox(label3,quantityTextField);
		addDetails.getChildren().addAll(vBox1,vBox2,vBox3);
		vBox3.setMaxWidth(100);
		HBox clearAndSave = new HBox(clearButton,saveButton);
		clearAndSave.setAlignment(Pos.CENTER);
		addDetails.setVisible(false);
		clearAndSave.setVisible(false);
		add.setMaxWidth(100);
		updateOne.setMaxWidth(200);
		delete.setMaxWidth(100);

		/* TODO 2-09 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear the filter
		 *   text field vlaue and to enable the "Show Expire Only" CheckBox
		 *   if "BOUGHT_DAYS_AGO" is selected
		 */
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
				System.out.println(t1.toString());
				if (t1.intValue() == 2){
					checkBox.setDisable(false);
					textField.setText("");

					/* TODO 2-10 - TO COMPLETE ****************************************
					 * filter container - part 2:
					 * - addListener to the "Filter By" ChoiceBox to clear and set focus
					 *   to the filter text field and to enable the "Show Expire Only"
					 *   CheckBox if "BOUGHT_DAYS_AGO" is selected
					 *
					 * - setOnAction on the "Show Expire Only" Checkbox to clear and
					 *   set focus to the filter text field
					 */

					textField.requestFocus();
				} else {
					checkBox.setSelected(false);
					checkBox.setDisable(true);
				}
			}
		});

		checkBox.setOnAction( e -> {
			textField.setText("");
			textField.requestFocus();
		});


		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
				System.out.println(t1.toString());
				if (t1.intValue() == 2){
					checkBox.setDisable(false);
					textField.setText("");
				}
			}
		});

		/* TODO 2-11 - TO COMPLETE ****************************************
		 * filter container - part 3:
		 * - create a filtered list
		 * - create a sorted list from the filtered list
		 * - bind comparators of sorted list with that of table view
		 * - set items of table view to be sorted list
		 * - set a change listener to text field to set the filter predicate
		 *   of filtered list
		 */
		FilteredList<Product> filteredList = new FilteredList<>(tableData, predicate -> true);

		filteredList.addListener(new ListChangeListener<Product>(){

			@Override
			public void onChanged(Change<? extends Product> change) {
				tableView.refresh();
			}



		});
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				String newLowerCased = t1.toLowerCase();
				if (t1 == null || t1.isEmpty()){
					filteredList.setPredicate(predicate -> true);
				} else if (choiceBox.getSelectionModel().getSelectedIndex() == 0){
					filteredList.setPredicate(predicate -> predicate.getItem().getName().toLowerCase().contains(newLowerCased));
				} else if (choiceBox.getSelectionModel().getSelectedIndex() == 1){
					filteredList.setPredicate(predicate -> predicate.getSection().toString().toLowerCase().contains(newLowerCased));
				} else if (choiceBox.getSelectionModel().getSelectedIndex() == 2){
					if (checkBox.isSelected()){
//						filteredList.predicateProperty().bindBidirectional(Bindings.createObjectBinding( () ->
//									predicate -> predicate.getItem().canExpire() && predicate.getDaysAgo().toLowerCase().contains(newLowerCased)
//								));
						filteredList.setPredicate(
								predicate -> predicate.getItem().canExpire() && predicate.getDaysAgo().toLowerCase().contains(newLowerCased)
						);

					} else {
						filteredList.setPredicate(predicate -> predicate.getDaysAgo().toLowerCase().contains(newLowerCased));
					}
				} else {
					filteredList.setPredicate(predicate -> predicate.toString().toLowerCase().contains(newLowerCased));
				}
			}
		});
		SortedList<Product> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);


		/* TODO 2-12 - TO COMPLETE ****************************************
		 * ACTION buttons: ADD, UPDATE ONE, DELETE
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE ONE and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
		 */
		add.setOnAction(e->{
			addDetails.setVisible(true);
			clearAndSave.setVisible(true);
		});

		updateOne.setOnAction(e -> {
			if (tableView.getSelectionModel().getSelectedItem() == null){
				alert.setAlertType(Alert.AlertType.INFORMATION);
				alert.setContentText("You haven't selected any product yet.");
				alert.show();
			} else {
				alert.setAlertType(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Are you sure you want to consume one product?");
				alert.showAndWait().filter(response -> {
					if (response == ButtonType.OK){
						try {
							Product product = tableView.getSelectionModel().getSelectedItem();
							Product result = warehouseDSC.useProduct(product.getId());
							if (result != null){
								tableView.getSelectionModel().getSelectedItem().updateQuantity();
								tableView.refresh();
							}


						} catch (Exception ex) {
							errorMessages.setContentText(ex.getMessage());
							errorMessages.show();
						}

					}
					return true;


				});
			}
		});

		ObservableList<Product> tempTableData = tableData;
		delete.setOnAction(e -> {
			if (tableView.getSelectionModel().getSelectedItem() == null){
				alert.setAlertType(Alert.AlertType.INFORMATION);
				alert.setContentText("You haven't selected any product yet.");
				alert.show();
			} else {
				alert.setAlertType(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Are you sure you want to delete this product?");
				alert.showAndWait().filter(response -> {
					if (response == ButtonType.OK){
						try {
							Product product = tableView.getSelectionModel().getSelectedItem();
							warehouseDSC.removeProduct(product.getId());
							tempTableData.remove(product);

						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println(ex);
							errorMessages.setContentText(ex.getMessage());
							errorMessages.show();
						}

					}
					return true;


				});
			}
		});

		/* TODO 2-13 - TO COMPLETE ****************************************
		 * add input controls and container(s)
		 * - Item will list item data from the data source controller list
		 *   all items method
		 * - Section will list all sections defined in the data source
		 *   controller SECTION enum
		 * - Quantity: a texf field, self descriptive
		 * - CANCEL button: clears all input controls
		 * - SAVE button: sends the new product information to the data source
		 *   controller add product method; be mindful of exceptions when any
		 *   or all of the input controls are empty upon SAVE button click
		 */
		List<Item> itemList = warehouseDSC.getAllItems();
		for (Item item: itemList){
			comboBox.getItems().add(item);
		}
		for (WarehouseDSC.SECTION section : WarehouseDSC.SECTION.values()){
			sectionChoiceBox.getItems().add(section);
		}

		ObservableList<Product> finalTableData = tableData;
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (comboBox.getSelectionModel().getSelectedItem() == null ||
					sectionChoiceBox.getSelectionModel().getSelectedItem() == null ||
					quantityTextField.getText() == null || quantityTextField.getText().isEmpty()){
					errorMessages.setContentText("Missing compulsory information for a product.");
					errorMessages.show();
				} else if (Integer.valueOf(quantityTextField.getText())<1){
					errorMessages.setContentText("The quantity of product cannot be less than one");
					errorMessages.show();
				} else {
					try {
						int result = warehouseDSC.addProduct(comboBox.getSelectionModel().getSelectedItem().getName(),
								Integer.valueOf(quantityTextField.getText()),
								WarehouseDSC.SECTION.values()[sectionChoiceBox.getSelectionModel().getSelectedIndex()]);
						finalTableData.add(warehouseDSC.searchProduct(result));
					} catch (Exception ex) {
						ex.printStackTrace();
						errorMessages.setContentText(ex.getMessage());
						errorMessages.show();
					} finally{
						comboBox.getSelectionModel().clearSelection();
						sectionChoiceBox.getSelectionModel().clearSelection();
						quantityTextField.clear();
						tableView.refresh();
					}
				}
			}
		});

		clearButton.setOnAction(e->{
			comboBox.getSelectionModel().clearSelection();
			sectionChoiceBox.getSelectionModel().clearSelection();
			quantityTextField.clear();
		});

		// =====================================================================
		// SET UP the Stage
		// =====================================================================
		// Create scene and set stage
		VBox root = new VBox();

		/* TODO 2-14 - TO COMPLETE ****************************************
		 * - add all your containers, controls to the root
		 */
		HBox top = new HBox();
		HBox buttons = new HBox();
		top.getChildren().addAll(textField,label,choiceBox,checkBox);
		top.setSpacing(10);
		top.setAlignment(Pos.CENTER_LEFT);
		buttons.getChildren().addAll(add,updateOne,delete);
		addDetails.setSpacing(10);
		root.getChildren().addAll(top,tableView,buttons,addDetails,clearAndSave);

		root.setStyle(
			"-fx-font-size: 20;" +
			"-fx-alignment: center;"
		);

		Scene scene = new Scene(root);
		stage.setScene(scene);
	}

	public void stop() throws Exception {

		/* TODO 2-15 - TO COMPLETE ****************************************
		 * call the data source controller database disconnect method
		 * NOTE: that database disconnect method throws exception
		 */
		try {
			warehouseDSC.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to disconnect database due to unknown reason. Please retry.");
		}
	}


}
