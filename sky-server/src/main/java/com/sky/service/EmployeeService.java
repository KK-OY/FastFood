package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void add(Employee employee);

    PageResult PageGetEmp(EmployeePageQueryDTO employeePageQueryDTO);

    void PutStatus(Integer status, Long id);

    void PutEmp(EmployeeDTO employeeDTO);

    Employee GetEmpById(Long id);

    void PutPassword(PasswordEditDTO passwordEditDTO);
}
