package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    String select01 = "SELECT *" +
            "FROM EMPLOYEE " +
            "ORDER BY LASTNAME";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    String select02 = "SELECT *" +
            "FROM EMPLOYEE " +
            "WHERE LENGTH(LASTNAME) < 6 " +
            "ORDER BY LASTNAME";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    String select03 = "SELECT *" +
            "FROM EMPLOYEE " +
            "WHERE SALARY BETWEEN 2000 AND 3000 ";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    String select04 = "SELECT *" +
            "FROM EMPLOYEE " +
            "WHERE SALARY NOT BETWEEN 2001 AND 2999";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    String select05 = "SELECT lastname, salary, DEP.NAME " +
            "FROM EMPLOYEE EMP, DEPARTMENT DEP " +
            " WHERE EMP.DEPARTMENT = DEP.ID";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select06 = "SELECT LASTNAME, SALARY, DEP.NAME as depname " +
            "FROM EMPLOYEE EMP " +
            "LEFT JOIN DEPARTMENT DEP ON DEP.ID = EMP.DEPARTMENT";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    String select07 = "SELECT SUM(SALARY) AS TOTAL " +
            "FROM EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    String select08 = "SELECT DEP.NAME AS DEPNAME, COUNT(EMP.DEPARTMENT) AS STAFF_SIZE " +
            "FROM DEPARTMENT DEP " +
            "INNER JOIN EMPLOYEE EMP ON DEP.ID = EMP.DEPARTMENT " +
            "GROUP BY DEP.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select09 = "SELECT DEP.NAME AS DEPNAME, SUM(SALARY) AS TOTAL, AVG(SALARY) AS AVERAGE " +
            "FROM DEPARTMENT DEP " +
            "INNER JOIN EMPLOYEE EMP ON DEP.ID = EMP.DEPARTMENT " +
            "GROUP BY DEP.NAME";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    String select10 = "SELECT EMP.LASTNAME AS EMPLOYEE, MAN.LASTNAME AS MANAGER " +
            "FROM EMPLOYEE EMP " +
            "LEFT JOIN EMPLOYEE MAN ON EMP.MANAGER = MAN.ID ";


}
