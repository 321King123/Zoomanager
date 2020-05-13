package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;

import java.util.List;

public interface EmployeeService  {

    /**
     * Used for creating new employees
     * @param employee to be created
     * */
    public Employee createEmployee(Employee employee);

    public List<Employee> getAll();

    public List<Employee> findByNameAndType(Employee employee);
}
