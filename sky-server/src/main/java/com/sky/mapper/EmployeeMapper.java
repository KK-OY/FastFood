package com.sky.mapper;

import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    void add(Employee employee);

    List<Employee> PageGetEmp(EmployeePageQueryDTO employeePageQueryDTO);

    void putStatus(Integer enable,Long id);

    void PutEmp(Employee employee);

    Employee GetEmpById(Long id);
}
