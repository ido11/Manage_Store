package logic;

import logic.employees.models.Employee;

public interface Interactor {

    void start(Employee connected);
    String getMenuDescription();
}
