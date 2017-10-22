package com.heapdev.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDBUtil {

	private DataSource dataSource;

	public StudentDBUtil(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<Student> getStudents() throws Exception {

		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRst = null;

		try {

			myConn = dataSource.getConnection();
			String sql = "select * from student order by first_name";
			myStmt = myConn.createStatement();
			myRst = myStmt.executeQuery(sql);

			while (myRst.next()) {
				int id = myRst.getInt("id");
				String firstName = myRst.getString("first_name");
				String lastName = myRst.getString("last_name");
				String email = myRst.getString("email");

				Student tempStudent = new Student(id, firstName, lastName, email);

				students.add(tempStudent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(myConn, myRst, myStmt);
		}

		return students;
	}

	private void close(Connection myConn, ResultSet myRst, Statement myStmt) {

		try {
			if (myRst != null) {
				myRst.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myConn != null) { // people generally doesnt close this as this might be used for connection
									// pooling for new connection
				myConn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method adds a new student object to the database
	 * 
	 * @param tempStudent
	 */
	public void addStudent(Student tempStudent) {

		Connection myConnection = null;
		PreparedStatement myStatement = null;
		ResultSet myResultset = null;

		try {
			myConnection = dataSource.getConnection();
			String sql = "insert into student " + "(first_name, last_name, email) " + "values(?, ?, ?)";

			myStatement = myConnection.prepareStatement(sql);

			myStatement.setString(1, tempStudent.getFirstName());
			myStatement.setString(2, tempStudent.getLastName());
			myStatement.setString(3, tempStudent.getEmail());

			myStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(myConnection, myResultset, myStatement);
		}

	}

	/**
	 * fetches Student object from database using id.
	 * 
	 * @param studentId
	 * @return
	 */
	public Student getStudentById(String theStudentId) {

		Student theStudent = null;
		Connection myConnection = null;
		PreparedStatement myStatement = null;
		ResultSet myResultset = null;
		int studentId;

		try {
			// Convert studentId to int
			studentId = Integer.parseInt(theStudentId);

			myConnection = dataSource.getConnection();
			String sql = "select * from student where id=?";
			myStatement = myConnection.prepareStatement(sql);
			myStatement.setInt(1, studentId);
			myResultset = myStatement.executeQuery();

			if (myResultset.next()) {
				String firstName = myResultset.getString("first_name");
				String lastName = myResultset.getString("last_name");
				String email = myResultset.getString("email");

				theStudent = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Couldn't find any student with id: " + studentId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(myConnection, myResultset, myStatement);
		}

		return theStudent;
	}

	/**
	 * updates the student object
	 * 
	 * @param theStudent
	 */
	public void updateStudent(Student theStudent) throws Exception {

		Connection myConnection = null;
		PreparedStatement myStatement = null;

		try {

			myConnection = dataSource.getConnection();

			String sql = "update student " + "set first_name=?, last_name=?, email=? " + "where id=?";

			myStatement = myConnection.prepareStatement(sql);
			myStatement.setString(1, theStudent.getFirstName());
			myStatement.setString(2, theStudent.getLastName());
			myStatement.setString(3, theStudent.getEmail());
			myStatement.setInt(4, theStudent.getId());

			myStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myConnection.close();
			myStatement.close();
		}
	}

	/**
	 * Delete Student by id
	 * 
	 * @param studentId
	 */
	public void deleteStudentById(String theStudentId) throws Exception {

		int studentid;
		Connection myConnection = null;
		PreparedStatement myStatement = null;

		try {
			studentid = Integer.parseInt(theStudentId);

			myConnection = dataSource.getConnection();
			String sql = "delete from student where id=?";
			myStatement = myConnection.prepareStatement(sql);
			myStatement.setInt(1, studentid);

			myStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(myConnection, null, myStatement);
		}
	}
}
