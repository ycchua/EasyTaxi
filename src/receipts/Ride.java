package receipts;

public class Ride {
	
	String driverID = "";
	String pickupAdd = "";
	String pickupTime = "";
	String dropoffAdd = "";
	String rideType = "";
	String rideValue = "";
	
	public Ride(String driverID, String pickupAdd, String pickupTime, String dropoffAdd, String rideType, String rideValue){
		this.driverID = driverID;
		this.pickupAdd = pickupAdd;
		this.pickupTime = pickupTime;
		this.dropoffAdd = dropoffAdd;
		this.rideType = rideType;
		this.rideValue = "";
	}
	
	public String getRideID(){
		return driverID;
	}
	
	public String getPickUpAdd(){
		return pickupAdd;
	}
	
	public String getDropOffAdd(){
		return dropoffAdd;
	}
	
	public String getPickUpTime(){
		return pickupTime;
	}
	
	public String getRideType(){
		return rideType;
	}
	
	public String getRideValue(){
		return rideValue;
	}
	
}
