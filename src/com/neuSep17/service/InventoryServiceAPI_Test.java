package com.neuSep17.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.neuSep17.dto.Vehicle;


public class InventoryServiceAPI_Test {

	private LinkedHashMap<String, Vehicle> vehiclesMap;
	private String fileName;
	
	public InventoryServiceAPI_Test(String file) {
		this.fileName = file;
		try {
			vehiclesMap = getVehiclesMap(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public ArrayList<Vehicle> getVehicles() {
		return new ArrayList<Vehicle>(vehiclesMap.values());
	}
	

	public String getFileName() {
		return fileName;
	}


	public void saveInventoryToFile() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName)));
			writer.println("id~webId~category~year~make~model~trim~type~price~photo");
			for (Vehicle vehicle: vehiclesMap.values()) {
				writer.println(vehicle);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static LinkedHashMap<String, Vehicle> getVehiclesMap(String file) throws IOException {
		File inventoryFile = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(inventoryFile));
		LinkedHashMap<String, Vehicle> vehicles = new LinkedHashMap<String, Vehicle>();		
		String line = null;
		int count = 0;
		while((line=reader.readLine())!=null) {
			count++;
			if(count==1) {continue;}
			String[] vehicleDataArray = line.split("~");
			Vehicle vehicle =new Vehicle(vehicleDataArray);
			vehicles.put(vehicle.getId(), vehicle);
		}
		
		reader.close();
		return vehicles;

	}

	
	public int getTotalVehicleAmount() {
		return vehiclesMap.size();
	}
	
	
	public Vehicle createVehicleFromInput(String[] vehicleData) {
		Vehicle vehicle = new Vehicle(vehicleData);
		return vehicle;
	}

	
	public void addVehicle(Vehicle vehicle) {
		vehiclesMap.put(vehicle.getId(), vehicle);

	}

	
	public void deleteVehicle(String vin) {
		vehiclesMap.remove(vin);	
		
	}
	
	
	public void search(String text, TableRowSorter<TableModel> sorter) { 
		if (text.length() == 0) {
			sorter.setRowFilter(null);
		} 
		else {
			// (?i) regex flag: case-insensitive matching
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
		}
	}
	
	
    public void cancelSearch(TableRowSorter<TableModel> sorter) {
		sorter.setRowFilter(null);
	}
	
	
	public void sortByColumn(int columnIndexToSort, TableRowSorter<TableModel> sorter, int Asc_Desc) {
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		
		switch (Asc_Desc) {
		case 0:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));break;
		case 1:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));break;
		case -1:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.UNSORTED));break;
		}

		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}

	
  // To be continued...

	public Vehicle getVehicleDetails(String id){
		return vehiclesMap.get(id);
	}
	

	/**
	 * Pass in a list of vehicles and different fields to filter and return the filtered result. The 
	 * category filter should seperate different categories with a space, e.g. "new certified". The price 
	 * should be a range and the lower limit (inclusive) and upper limit (exclusive) should be connected 
	 * with a "~". If the field is null, then the filter of that field is not applied.
	 * @param vehicles list to be sorted
	 * @param category desired category of the vehicles
	 * @param year desired year of the vehicles
	 * @param make desired make of the vehicles
	 * @param price desired price range of the vehicles
	 * @param type desired type of the vehicles
	 * @param search search keywords for the vehicles
	 * @return the filtered list of vehicles.
	 */
	public static List<Vehicle> vehiclesSearchAndFilter(List<Vehicle> vehicles,  String category, String year, String make, String price,
	String type, String search){
		List<Vehicle> filteredVehicles = new ArrayList<Vehicle>();
		// if (vehicles == null) {
			
		// }

		for (Vehicle vehicle : vehicles) {
			if (categoryFilter(vehicle, category) && yearFilter(vehicle, year) && makeFilter(vehicle, make) && priceFilter(vehicle, price) && typeFilter(vehicle, type) && searchFilter(vehicle, search)) {
				filteredVehicles.add(vehicle);
			}
		}
		return filteredVehicles;
	}

	private static boolean categoryFilter(Vehicle vehicle, String category) {
		if (category == null)
			return true;
		if (category.contains("new")) {
			if (vehicle.getCategory().toString().equals("NEW"))
				return true;
		}
		if (category.contains("used")) {
			if (vehicle.getCategory().toString().equals("USED"))
				return true;
		}
		if (category.contains("certified")) {
			if (vehicle.getCategory().toString().equals("CERTIFIED"))
				return true;
		}
		return false;
	}

	private static boolean yearFilter(Vehicle vehicle, String year) {
		if (year == null)
			return true;
		return vehicle.getYear() == Integer.parseInt(year);
	}

	private static boolean makeFilter(Vehicle vehicle, String make) {
		if (make == null)
			return true;
		return vehicle.getMake().equals(make);
	}

	private static boolean priceFilter(Vehicle vehicle, String price) {
		if (price == null)
			return true;
		float vehiclePrice = vehicle.getPrice();
		String[] limits = price.split("~");
		float low = Float.parseFloat(limits[0]);
		if (limits.length == 1) {
			return vehiclePrice >= low;
		}
		float high = Float.parseFloat(limits[1]);
		return vehiclePrice >= low && vehiclePrice < high;
	}

	private static boolean typeFilter(Vehicle vehicle, String type) {
		if (type == null)
			return true;
		return vehicle.getBodyType().equals(type);
	}

	private static boolean searchFilter(Vehicle vehicle, String search) {
		if (search == null)
			return true;
		search = search.toLowerCase();
		String vehicleString = vehicle.toString().toLowerCase();
		String [] arr = search.split("\\s+");
		for (String word : arr) {
			if(!vehicleString.contains(word))
				return false;
		}
		return true;
	}

	/**
	 * Take in a list of vehicles and return a map. The keys of the map are "year", "make", "price" and 
	 * "type". The corresponding values are the values contained by the passed in list of vehicles.
	 * @param vehicles list of vehicles passed in
	 * @return the result map
	 */
	public static Map<String, List<String>> getComboBoxItemsMap(List<Vehicle> vehicles) {
		List<String> yearList = new ArrayList<String>();
		List<String> makeList = new ArrayList<String>();
		List<String> priceList = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		TreeSet<String> yearSet = new TreeSet<>();
		TreeSet<String> makeSet = new TreeSet<>();
		TreeSet<String> priceSet = new TreeSet<>();
		TreeSet<String> typeSet = new TreeSet<>();
		for (Vehicle vehicle : vehicles) {
			String year = vehicle.getYear().toString();
			String make = vehicle.getMake();
			String type = vehicle.getBodyType();
			String price = priceToString(vehicle.getPrice());
			yearSet.add(year);
			makeSet.add(make);
			priceSet.add(price);
			typeSet.add(type);
		}
		Map<String, List<String>> map = new HashMap<>();
		yearList.addAll(yearSet);
		makeList.addAll(makeSet);
		priceList.addAll(priceSet);
		typeList.addAll(typeSet);
		map.put("year", yearList);
		map.put("make", makeList);
		map.put("price", priceList);
		map.put("type", typeList);
		return map;
	}

	private static String priceToString(float price) {
		for (int i = 0; i < 10; i++) {
			int low = i * 10000;
			int high = (i + 1) * 10000;
			if (price >= low && price < high) {
				return low + "~" + high;
			}
		}
		return "100000~";
	}

	/**
	 * Take in a list of vehicles and sort it according to the sortType.
	 * @param vehicles list of vehicles passed in
	 * @param sortType the field according to which the list should be sorted
	 * @param isAscending true if the list is to be sorted ascendingly and false if it is to be sorted 
	 * descendingly
	 * @return the sorted list
	 */
	public static List<Vehicle> sortVehicles(List<Vehicle> vehicles, String sortType, boolean isAscending) {
		switch (sortType) {
		case "price":
			vehicles.sort(new Comparator<Vehicle>() {
				@Override
				public int compare(Vehicle v1, Vehicle v2) {
					float diff = v1.getPrice() - v2.getPrice();
					if (diff > 0) {
						return 1;
					} else if (diff == 0) {
						return 0;
					} else {
						return -1;
					}
				}
			});
			break;
		case "year":
			vehicles.sort(new Comparator<Vehicle>() {
				@Override
				public int compare(Vehicle v1, Vehicle v2) {
					return v1.getYear() - v2.getYear();
				}
			});
			break;
		case "make":
			vehicles.sort(new Comparator<Vehicle>() {
				@Override
				public int compare(Vehicle v1, Vehicle v2) {
					return v1.getMake().compareTo(v2.getMake());
				}
			});
			break;
		default:
			break;
		}
		if (isAscending == false)
			Collections.reverse(vehicles);
		return vehicles;
	}
	
}
