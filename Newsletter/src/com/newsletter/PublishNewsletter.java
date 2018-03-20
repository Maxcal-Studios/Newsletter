package com.newsletter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.database.DBConnector;
import com.email.MailUtils;

/**
 * Servlet implementation class publishNewsletter
 */
@WebServlet("/PublishNewsletter")
public class PublishNewsletter extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//login check
		if(!DBConnector.isLoggedIn(request.getSession())) {
			response.sendRedirect("../../login.jsp");
		}

		System.out.println(request.getParameter("id"));
		
		if(request.getParameter("method").equals("schedule")) {
			//creating a connection to the DB
	    	Connection con = DBConnector.getConnection();
	    	PreparedStatement st = null;
	    	String sql = "INSERT INTO schedule(newsletterID, creator, sendDate, krit, elements) VALUES(?, ?, ?, ?, ?)";
			
			try {
				
				//getting the parameters
				
				String creator = request.getSession().getAttribute("user").toString();
				String date = request.getParameter("date");
				String time = request.getParameter("time");
				
				String[] dates = time.split("/");
				
				SimpleDateFormat s12 = new SimpleDateFormat("hh:mm a");
				SimpleDateFormat s24 = new SimpleDateFormat("hh:mm");
				
				String[] times = new String[2];
				
				time = s24.format(s12.parse(time));
				
				times = time.split(":");
				
				System.out.println(" " + dates[1] + " " + dates[0] + " " + times[0] + " " + times[1]);
				
				Timestamp t = new Timestamp(Integer.parseInt(dates[2]), Integer.parseInt(dates[1]), Integer.parseInt(dates[0]), Integer.parseInt(times[0]), Integer.parseInt(times[1]), 0, 0);
				
				String krit = request.getParameter("krit");
				String[] element = request.getParameterValues("elements");
				
				String elements = "";
				for(int i = 0; i < element.length; i++) {
					elements += element[i] + " ";
				}
				elements = elements.substring(0, elements.length() - 1);
				
				int newsletterID = Integer.parseInt(request.getParameter("id"));
				
				//prepare statement
				st = con.prepareStatement(sql);
				st.setInt(1, newsletterID);
				st.setString(2, creator);
				st.setString(4, krit);
				st.setString(5, elements);
				
			} catch(Exception e) {e.printStackTrace();}
			finally {
				try { st.close(); } catch (Exception e) { }
			    try { con.close(); } catch (Exception e) { }
			}
			
		} else {
			
			//getting the parameters
			int newsletterID = Integer.parseInt(request.getParameter("id"));
			String krit = request.getParameter("krit");
			String elements = request.getParameter("elements");
			
			NewsletterSender.sendNewsletter(newsletterID, krit, elements);
			
			response.sendRedirect("../admin/publish.jsp");
		
		}
	}
}
