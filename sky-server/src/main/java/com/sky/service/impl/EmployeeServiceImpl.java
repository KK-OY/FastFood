package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);
        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            log.info("密码{}",employee);
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void add(Employee employee) {
        employeeMapper.add(employee);
    }

    @Override
    public PageResult PageGetEmp(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        List<Employee> employees = employeeMapper.PageGetEmp(employeePageQueryDTO);
        Page<Employee> p = (Page<Employee>) employees;
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void PutStatus(Integer status, Long id) {
        if(status.equals(StatusConstant.DISABLE)){
            employeeMapper.putStatus(StatusConstant.DISABLE,id);
        } else if (status.equals(StatusConstant.ENABLE)) {
            employeeMapper.putStatus(StatusConstant.ENABLE,id);
        }

    }

    @Override
    public void PutEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employeeMapper.PutEmp(employee);
    }

    @Override
    public Employee GetEmpById(Long id) {
        Employee employee = employeeMapper.GetEmpById(id);
        return employee;
    }

    @Override
    public void PutPassword(PasswordEditDTO passwordEditDTO) {
        //库里的密码
        String s = employeeMapper.GetPassword(BaseContext.getCurrentId());
        //前端传过来的老密码
        String oldpassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        if(!oldpassword.equals(s) ){
            throw new PasswordErrorException("旧密码错误");
        }

        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        Employee employee = new Employee();
        employee.setPassword(newPassword);
        employee.setId(BaseContext.getCurrentId());
        employeeMapper.PutPassword(employee);
    }

}
