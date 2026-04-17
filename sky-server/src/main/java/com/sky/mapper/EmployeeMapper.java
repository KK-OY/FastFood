package com.sky.mapper;

import com.sky.annotion.AutoFill;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
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

    @AutoFill(value = OperationType.INSERT)
    void add(Employee employee);

    List<Employee> PageGetEmp(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void putStatus(Integer enable,Long id);

    @AutoFill(value = OperationType.UPDATE)
    void PutEmp(Employee employee);

    Employee GetEmpById(Long id);

    String GetPassword(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void PutPassword(Employee employee);
}
