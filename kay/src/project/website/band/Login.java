package project.website.band;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Servlet implementation class DatabaseAccess
 */
@WebServlet("/DatabaseAccess")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Configuration cfg = null;
	
	private String templateDir = "/WEB-INF/templates";
       
	static final String DRIVE_NAME = "com.mysql.jdbc.Driver";
	
	static final String CONNECTION_URL = "jdbc:mysql://nathandharris.com:3306/easygig";
	
	static final String DB_CONNECTION_USERNAME = "username";
	
	static final String DB_CONNECTION_PASSWORD = "password";
	
	public Login() {
		super();
	}
	
	public void init() {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.25) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		cfg = new Configuration(Configuration.VERSION_2_3_25);

		// Specify the source where the template files come from.
		cfg.setServletContextForTemplateLoading(getServletContext(), templateDir);

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		// This handler outputs the stack trace information to the client, formatting it so 
		// that it will be usually well readable in the browser, and then re-throws the exception.
		//		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		// Specifies if TemplateException-s thrown by template processing are logged by FreeMarker or not. 
		//		cfg.setLogTemplateExceptions(false);
	}

	public void runTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// You can use this structure for all of your objects to be sent to browser
		Template template = null;
		DefaultObjectWrapperBuilder df = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
		SimpleHash root = new SimpleHash(df.build());
		
		//Connect to database
		Connection con = null;
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		boolean status = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName(DRIVE_NAME);
			con = DriverManager.getConnection(CONNECTION_URL, DB_CONNECTION_USERNAME, DB_CONNECTION_PASSWORD);
			stmt = con.prepareStatement("SELECT * FROM login WHERE username=" + username + " AND password=" + password);
			rs = stmt.executeQuery();
			status = rs.next();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
	               try {
	                   con.close();
	               } catch (SQLException e) {
	                   e.printStackTrace();
	               }
	        }
			if (stmt != null) {
	               try {
	                   stmt.close();
	               } catch (SQLException e) {
	                   e.printStackTrace();
	               }
	        }
	        if (rs != null) {
	               try {
	                   rs.close();
	               } catch (SQLException e) {
	                   e.printStackTrace();
	               }
	        }
		}

		response.setContentType("text/html");
		Writer out = response.getWriter();
		
        if(status == true){  
        	try { //write out
        		root.put("first_name", username);
        		String templateName = "WelcomePage.ftl";
				template = cfg.getTemplate(templateName );
			
				template.process(root, out);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
        else {  
        	try { //write out
        		String templateName = "Login.ftl";
				template = cfg.getTemplate(templateName );
			
				template.process(root, out);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
        }	
	}
	/**
	 * @see HttpServlet#doGet(ResultSet result, HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		runTemplate(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

