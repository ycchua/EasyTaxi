package receipts;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
//import javax.mail.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.FileReader;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Servlet implementation class processLogin
 */
@WebServlet("/processReceiptCSV")
public class processReceiptCSV extends HttpServlet {
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 1024 * 1024 * 1024;
	private int maxMemSize = 1024 * 1024 * 1024;
	private File file;

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getInitParameter("file-upload");
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public processReceiptCSV() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String message = "";
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();
		if (!isMultipart) {
			message = "You have got not uploaded a file";
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		// factory.setRepository(new File("c:\\temp"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);
		
		try {
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			// the hash map key is the driver ID
			
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {

					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					// Write the file
					if (fileName.lastIndexOf("\\") >= 0) {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\")));
					} else {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\") + 1));
					}
					fi.write(file);
					// out.println("Uploaded Filename: " + fileName + "<br>");

					String csvFilename = filePath + fileName;
					CSVReader csvReader = new CSVReader(new FileReader(csvFilename));

					String[] row = null;
					
					// Retrieve date of interest from file name
					// layout is day day month month year year separated by dots
					char day1 = fileName.charAt(0);
					char day2 = fileName.charAt(1);
					String day = "" + day1 + day2;
					char mth1 = fileName.charAt(3);
					char mth2 = fileName.charAt(4);
					String mth = "" + mth1 + mth2;
					char year1 = fileName.charAt(6);
					char year2 = fileName.charAt(7);
					String year = "20" + year1 + year2;
					HashMap<String, ArrayList<Ride>> consolidatedData = new HashMap<String, ArrayList<Ride>>();

					// specify the required parameters and identify their
					// position in CSV
					String driverID = "driverID";
					String driverName = "driver_name";
					String pickUpLocation = "request_formatted_address";
					String dropOffLocation = "request_destination_address";
					String pickUpDate = "date_GMT0";
					String pickUpTime = "boarded_at";
					String rideType = "payment_method";
					String paymentValue = "payment_value_EUR";
					//tester
					String debug = "";
					
					HashMap<String, Integer> positionMap = new HashMap<String, Integer>();

					//load all the headings
					String[] headings = csvReader.readNext();
					
					//compare headings value to get the column position
					for (int x = 0; x <headings.length; x++){
						if (headings[x].equals(driverID)) {
							positionMap.put("driverIDPos", x);
						} else if (headings[x].equals(pickUpLocation)) {
							positionMap.put("pickUpAddPos", x);
							
						} else if (headings[x].equals(dropOffLocation)) {
							positionMap.put("dropOffAddPos",x);
						
						} else if (headings[x].equals(pickUpTime)) {
							positionMap.put("pickUpTimePos", x);
						
						} else if (headings[x].equals(rideType)) {
							positionMap.put("rideTypePos", x);
						
						} else if (headings[x].equals(paymentValue)) {
							positionMap.put("paymentValuePos" ,x);
						
						} else if (headings[x].equals(driverName)) {
							positionMap.put("driverNamePos", x);
						
						} else if (headings[x].equals(pickUpDate)) {
							positionMap.put("pickUpDatePos", x);
						}
					}
					
					while ((row = csvReader.readNext()) != null) {						
						
						//sortingRides
						
						
						if(!row[positionMap.get("driverIDPos")].equals("")){
							driverID = row[positionMap.get("driverIDPos")];
							pickUpLocation = row[positionMap.get("pickUpAddPos")];
							dropOffLocation = row[positionMap.get("dropOffAddPos")];
							pickUpTime = row[positionMap.get("pickUpTimePos")];
							rideType = row[positionMap.get("rideTypePos")];
							paymentValue = row[positionMap.get("paymentValuePos")];
							Ride toAdd = new Ride(driverID, pickUpLocation, pickUpTime, dropOffLocation, rideType, paymentValue);
							
							if(consolidatedData.get(driverID) == null){
								ArrayList<Ride> temp = new ArrayList<Ride>();
								temp.add(toAdd);
								consolidatedData.put(driverID, temp);
								
							}else{
								ArrayList<Ride> temp = consolidatedData.get(driverID);
								temp.add(toAdd);
								consolidatedData.put(driverID, temp);
							}
							
						}
						
						
						
						
						
					}
					final String username = "yichong.chua@easytaxi.com.sg"; 
					final String password = "Eisprior2p";
					  
					  Properties props = new Properties(); props.put("mail.smtp.auth", "true");
					  props.put("mail.smtp.starttls.enable", "true");
					  props.put("mail.smtp.host", "smtp.gmail.com");
					  props.put("mail.smtp.port", "587");
					  
					  
					  Session session = Session.getInstance(props,
							  new javax.mail.Authenticator() {
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication(username, password);
								}
							  });
					 
							try {
								
								//for (String entry : consolidatedData.keySet()){
								//	ArrayList<Ride> byDriver  = consolidatedData.get(entry);
			
							for (Entry<String, ArrayList<Ride>> entry : consolidatedData.entrySet())
								{
								  String key = entry.getKey();
								  ArrayList<Ride> value = entry.getValue();

								
								
									
								Message mailMsg = new MimeMessage(session);
								mailMsg.setFrom(new InternetAddress("yichong.chua@easytaxi.com.sg"));
								mailMsg.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse("chuayichong@gmail.com"));
								mailMsg.setSubject("Your daily rides");
								String emailContent = debug;
								
								for(int a = 0; a < value.size(); a++){
									String pickup =  value.get(a).getPickUpAdd();
									String dropoff = value.get(a).getDropOffAdd();
									
									emailContent = emailContent+ pickup + dropoff +"pickupaddress position" + positionMap.get("pickUpAddPos");
									
								}
								
								
								mailMsg.setText("Hi," +
										
										
										emailContent
										
									+ "\n\n Thank you.");
					 
								Transport.send(mailMsg);
								
								JSONObject json = new JSONObject();
								response.setContentType("application/JSON");
								String output = json.toString();
								//out.println(output);
								//out.close();
								
								}
					 
							} catch (MessagingException e) {
								throw new RuntimeException(e);
							}
					  
					
			

					// ...
					csvReader.close();
				}
			}
			response.sendRedirect("/EasyTaxi/receiptEmailResult.jsp");

		} catch (Exception ex) {
			System.out.println(ex);
		}

		  

	}
	
	  
}
