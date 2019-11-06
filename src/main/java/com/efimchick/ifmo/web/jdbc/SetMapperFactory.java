package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                Set<Employee> employees = new HashSet<>();
                try {
                    while (resultSet.next()) {
                        employees.add(employee(resultSet));
                    }
                } catch (SQLException exception){
                    return null;
                }
                return employees;
            }
        };
    }
    public Employee manager(ResultSet resultSet, int managerID) {
        try {
            int rowID = resultSet.getRow();
            resultSet.beforeFirst();
            Employee emp = null;
            while (resultSet.next()){
                if (Integer.parseInt(resultSet.getString("ID")) == managerID){
                    emp = employee(resultSet);
                    break;
                }
            }
            resultSet.absolute(rowID);
            return emp;
        } catch (SQLException exception) {
            return null;
        }
    }

    private Employee employee(ResultSet resultSet){
        try {
         return new Employee(
                new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                new FullName(
                        resultSet.getString("FIRSTNAME"),
                        resultSet.getString("LASTNAME"),
                        resultSet.getString("MIDDLENAME")
                ),
                Position.valueOf(resultSet.getString("POSITION")),
                LocalDate.parse(resultSet.getString("HIREDATE")),
                new BigDecimal(resultSet.getInt("SALARY")),
                manager(resultSet, Integer.parseInt(resultSet.getString("MANAGER"))));
        } catch (SQLException exception) {
            return null;
        }
    }
}
