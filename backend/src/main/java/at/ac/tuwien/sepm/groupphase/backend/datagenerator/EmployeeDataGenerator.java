package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Date;

public class EmployeeDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmployeeRepository employeeRepository;

    public EmployeeDataGenerator(EmployeeRepository employeeRepository){
        this.employeeRepository=employeeRepository;
    }

    private void generateEmployee(){
        if(employeeRepository.findAll().size()>0){
            LOGGER.debug("Employee already generated.");
        }else{
            LOGGER.debug("Generating 1 employees");
            Employee employee = Employee.EmployeeBuilder.anEmployee().withName("employee").withUsername("user")
                .withBirthday(new Date()).withEmail("user@email.com").withType(EmployeeType.ANIMAL_CARE).build();
            LOGGER.debug("Saving employee");
            employeeRepository.save(employee);
        }
    }
}
