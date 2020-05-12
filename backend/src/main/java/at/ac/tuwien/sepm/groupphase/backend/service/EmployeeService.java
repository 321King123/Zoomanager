package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;

public interface EmployeeService  {

    /**
     * Used for creating new employees
     * @param employee to be created
     * */
    public Employee createEmployee(Employee employee);
}
