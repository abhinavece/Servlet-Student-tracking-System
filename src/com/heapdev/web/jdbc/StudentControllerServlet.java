package com.heapdev.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.activation.CommandMap;
import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */

@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDBUtil studentDBUtil;
	@Resource(name = "jdbc/web_student_tracker")
	private DataSource dataSource;

	@Override
	public void init() throws ServletException {
		super.init();

		try {
			studentDBUtil = new StudentDBUtil(dataSource);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Read the command parameter
			String theCommand = request.getParameter("command");

			if (theCommand == null) {
				theCommand = "LIST";
			}

			// Route the appropriate method
			switch (theCommand) {
			case "LIST":
				listStudents(request, response);
				break;

			case "ADD":
				addStudent(request, response);
				break;

			case "LOAD":
				loadStudent(request, response);
				break;

			case "UPDATE":
				updateStudent(request, response);
				break;
				
			case "DELETE":
				deleteStudent(request, response);
				break;

			default:
				listStudents(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String studentId = request.getParameter("studentId");
		studentDBUtil.deleteStudentById(studentId);
		listStudents(request, response);
	}

	/**
	 * this method updates the student object
	 * 
	 * @param request
	 * @param response
	 */
	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read student information from the form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");

		// create Student object
		Student theStudent = new Student(id ,firstName, lastName, email);

		// perform update on database
		studentDBUtil.updateStudent(theStudent);

		// send user back to the list
		listStudents(request, response);
	}

	/**
	 * This method loads the data from the table.
	 * 
	 * @param request
	 * @param response
	 */
	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// get StudentId from the url
		String studentId = request.getParameter("studentId");

		// get student from the database
		Student theStudent = studentDBUtil.getStudentById(studentId);

		// place student to the request object
		request.setAttribute("theStudent", theStudent);

		// send it to JSP page
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student.jsp");
		dispatcher.forward(request, response);

	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) {
		// get Students from DbUtil

		try {
			List<Student> students = studentDBUtil.getStudents();

			// add students to request
			request.setAttribute("students", students);

			// send it to jsp page
			RequestDispatcher dispatcher = request.getRequestDispatcher("/studentDetails.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method adds student from the form to the database
	 * 
	 * @param request
	 * @param response
	 */
	private void addStudent(HttpServletRequest request, HttpServletResponse response) {

		// Read student info from data

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");

		// create a new student object
		Student tempStudent = new Student(firstName, lastName, email);

		// add student in database
		studentDBUtil.addStudent(tempStudent);

		// send back to the main page
		listStudents(request, response);
	}

}
