package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    //
    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department){
                try {
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM employee WHERE department = " + department.getId()
                    );
                    List<Employee> employees = new ArrayList<>();
                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee){
                try{
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM employee WHERE manager = " + employee.getId()
                    );
                    List<Employee> employees = new ArrayList<>();
                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id){
                try {
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM employee WHERE id = " + Id
                    );
                    if (resultSet.next()) {
                        return Optional.of(newEmployee(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException exception){
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll(){
                try {
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM employee"
                    );
                    List<Employee> employees = new ArrayList<>();
                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Employee save(Employee employee){
                try {
                    newStatement().execute(
                    "INSERT INTO employee VALUES ('" +
                        employee.getId()                       + "', '" +
                        employee.getFullName().getFirstName()  + "', '" +
                        employee.getFullName().getLastName()   + "', '" +
                        employee.getFullName().getMiddleName() + "', '" +
                        employee.getPosition()                 + "', '" +
                        employee.getManagerId()                + "', '" +
                        Date.valueOf(employee.getHired())      + "', '" +
                        employee.getSalary()                   + "', '" +
                        employee.getDepartmentId()             + "')"
                    );
                    return employee;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public void delete(Employee employee){
                try {
                    newStatement().execute(
                    "DELETE FROM employee WHERE ID = " + employee.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id){
                try {
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM department WHERE id = " + Id
                    );
                    if (resultSet.next()) {
                        return Optional.of(newDepartment(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException exception){
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll(){
                try {
                    ResultSet resultSet = newStatement().executeQuery(
                    "SELECT * FROM department"
                    );
                    List<Department> departments = new ArrayList<>();
                    while (resultSet.next()) {
                        departments.add(newDepartment(resultSet));
                    }
                    return departments;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Department save(Department department){
                try {
                    if (getById(department.getId()).equals(Optional.empty())) {
                        newStatement().execute(
                        "INSERT INTO department VALUES ('" +
                            department.getId()       + "', '" +
                            department.getName()     + "', '" +
                            department.getLocation() + "')"
                        );
                    } else {
                        newStatement().execute(
                        "UPDATE department SET " +
                             "NAME = '"     + department.getName()     + "', " +
                             "LOCATION = '" + department.getLocation() + "' " +
                             "WHERE ID = '" + department.getId()       + "'"
                        );
                    }
                    return department;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public void delete(Department department){
                try{
                    newStatement().execute(
                    "DELETE FROM department WHERE ID = " + department.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }

    private Employee newEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
            new BigInteger(resultSet.getString("ID")),
            new FullName(
                resultSet.getString("FIRSTNAME"),
                resultSet.getString("LASTNAME"),
                resultSet.getString("MIDDLENAME")
            ),
            Position.valueOf(resultSet.getString("POSITION")),
            LocalDate.parse(resultSet.getString("HIREDATE")),
            resultSet.getBigDecimal("SALARY"),
            BigInteger.valueOf(resultSet.getInt("MANAGER")),
            BigInteger.valueOf(resultSet.getInt("DEPARTMENT"))
        );
    }

    private Department newDepartment(ResultSet resultSet) throws SQLException {
        return new Department(
            new BigInteger(resultSet.getString("ID")),
            resultSet.getString("NAME"),
            resultSet.getString("LOCATION")
        );
    }

    private Statement newStatement() throws SQLException {
        return ConnectionSource.instance().createConnection().createStatement();
    }
}
