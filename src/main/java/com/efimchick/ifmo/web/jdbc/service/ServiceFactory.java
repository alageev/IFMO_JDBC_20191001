package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    private ResultSet newResultSet(String SQLString) throws SQLException {
        return ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQLString);
    }

    private Employee newEmployee (ResultSet resultSet, boolean withChain, int level){
        try {
            if (level > 1) {
                return null;
            }
            Employee manager = null;
            if (resultSet.getString("MANAGER") != null){
                if (withChain) {
                    String manID = resultSet.getString("MANAGER");
                    int rowID = resultSet.getRow();
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        if (resultSet.getString("ID").equals(manID)) {
                            manager = newEmployee(resultSet, true, 0);
                            break;
                        }
                    }
                    resultSet.absolute(rowID);
                } else {
                    ResultSet newResultSet = newResultSet("SELECT * FROM EMPLOYEE");
                    while (newResultSet.next()){
                        if (newResultSet.getString("ID").equals(resultSet.getString("MANAGER"))){
                            manager = newEmployee(newResultSet, false, ++level);
                            break;
                        }
                    }
                }
            }
            return new Employee(
                    new BigInteger(resultSet.getString("id")),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getString("SALARY")),
                    manager,
                    newDepartment(resultSet.getString("DEPARTMENT")));
        } catch (SQLException e) {
            return null;
        }
    }

    private Department newDepartment(String ID) {
        try {
            if (ID == null) {
                return null;
            }
            ResultSet resultSet = newResultSet("SELECT * FROM DEPARTMENT");
            while (resultSet.next()) {
                if (ID.equals(resultSet.getString("ID"))) {
                    return new Department(
                            new BigInteger(resultSet.getString("ID")),
                            resultSet.getString("NAME"),
                            resultSet.getString("LOCATION")
                    );
                }
            }
            return null;
        } catch (SQLException exception){
            return null;
        }
    }

    private List<Employee> newList(Paging paging, String SQLString){
        try {
            ResultSet resultSet = newResultSet(SQLString);
            List<Employee> result = new LinkedList<>();
            int item = (paging.page - 1) * paging.itemPerPage;
            resultSet.absolute(item);
            while (resultSet.next() && item < (paging.page) * paging.itemPerPage) {
                result.add(newEmployee(resultSet, false, 0));
                item++;
            }
            return result;
        } catch (SQLException exception){
            return null;
        }
    }

    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE ORDER BY LASTNAME");
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE ORDER BY SALARY");
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE ORDER BY DEPARTMENT, LASTNAME");
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY");
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY LASTNAME");
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY LASTNAME");
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return newList(paging, "SElECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY HIREDATE");
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                return newList(paging, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY SALARY");
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    ResultSet resultSet = newResultSet("SELECT * FROM EMPLOYEE");
                    while (resultSet.next()) {
                        if (resultSet.getString("ID").equals(String.valueOf(employee.getId()))){
                            return newEmployee(resultSet, true, 0);
                        }
                    }
                    return null;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    ResultSet resultSet = newResultSet("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY DESC");
                    List<Employee> employees = new ArrayList<>();
                    while (resultSet.next()){
                        employees.add(newEmployee(resultSet, false, 0));
                    }
                    return employees.get(salaryRank - 1);
                } catch (SQLException exception) {
                    return null;
                }
            }
        };
    }
}
