package us.pax.basil.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import us.pax.basil.constant.PrivilegeConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.login.Employee;
import us.pax.basil.mapper.EmployeeMapper;
import us.pax.basil.utils.DateTimeUtil;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@AllArgsConstructor
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> implements IService<Employee> {
    EmployeeMapper employeeMapper;

    private void setQueryOrder(QueryWrapper<Employee> wrapper, String sortColumns) {
        if (null == sortColumns || sortColumns.isEmpty()) {
            return;
        }

        String[] sortCols = sortColumns.split(",");
        for (String col : sortCols) {
            String[] fields = col.split("\\.");
            if (fields.length > 2) {
                log.warn("Ignoring invalid sort field: {}", col);
                continue;
            }

            switch (fields[0]) {
                case "userName":
                    if (fields.length == 2 && fields[1].equalsIgnoreCase("desc")) {
                        wrapper.orderByDesc("NAME");
                    } else {
                        wrapper.orderByAsc("NAME");
                    }
                    break;

                case "email":
                    if (fields.length == 2 && fields[1].equalsIgnoreCase("desc")) {
                        wrapper.orderByDesc("EMAIL");
                    } else {
                        wrapper.orderByAsc("EMAIL");
                    }
                    break;

                case "registerTime":
                    if (fields.length == 2 && fields[1].equalsIgnoreCase("desc")) {
                        wrapper.orderByDesc("CREATE_DATE");
                    } else {
                        wrapper.orderByAsc("CREATE_DATE");
                    }
                    break;

                case "lastLogin":
                    if (fields.length == 2 && fields[1].equalsIgnoreCase("desc")) {
                        wrapper.orderByDesc("LAST_LOGIN_DATE");
                    } else {
                        wrapper.orderByAsc("LAST_LOGIN_DATE");
                    }
                    break;

                default:
                    log.warn("Ignoring invalid sort field: {}", col);
                    break;
            }
        }
    }

    public QueryResultArrayDTO pageQueryUser(int currentPage,
                                             int size,
                                             String sort,
                                             String userName,
                                             String email,
                                             String registerTime,
                                             String lastLogin) {
        try {
            String[] registerDates = new String[2];
            if (null != registerTime && !registerTime.isEmpty()) {
                try {
                    registerDates = DateTimeUtil.
                            getStartEnd(registerTime, DateTimeUtil.PATTERN_YYYYMMDD_WITH_SLASH, "~");
                } catch (DateTimeParseException ex) {
                    log.warn("Parse register date range failed! Ignoring invalid date filter: {}", registerTime);
                }
            }

            String[] loginDates = new String[2];
            if (null != lastLogin && !lastLogin.isEmpty()) {
                try {
                    loginDates = DateTimeUtil.
                            getStartEnd(lastLogin, DateTimeUtil.PATTERN_YYYYMMDD_WITH_SLASH, "~");
                } catch (DateTimeParseException ex) {
                    log.warn("Parse login date range failed! Ignoring invalid date filter: {}", lastLogin);
                }
            }
            
            QueryWrapper<Employee> wrapper = new QueryWrapper<>();
            wrapper.select("EMP_OID", "NAME", "EMAIL", "CREATE_DATE as GMT_CREATE", "LAST_LOGIN_DATE")
                   .like((userName != null && !userName.isEmpty()), "NAME", userName)
                   .like((email != null && !email.isEmpty()), "EMAIL", email)
                   .between((registerDates[0] != null && registerDates[1] != null),
                           "CREATE_DATE", registerDates[0], registerDates[1])
                   .between((loginDates[0] != null && loginDates[1] != null),
                           "CREATE_DATE", loginDates[0], loginDates[1]);

            setQueryOrder(wrapper, sort);

            Page<Employee> page = new Page<>(currentPage, size);
            employeeMapper.selectPage(page, wrapper);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            ArrayList<Map<String, Object>> resultList = new ArrayList<>();
            for (Employee user : page.getRecords()) {
                Map<String, Object> userMap = new HashMap<>();

                userMap.put(PrivilegeConstant.ID, user.getEmpOid());
                userMap.put(PrivilegeConstant.USERNAME, user.getName());
                userMap.put(PrivilegeConstant.EMAIL, user.getEmail());
                userMap.put(PrivilegeConstant.REGISTER_TIME,
                        (null == user.getGmtCreate()) ? "" : dtf.format(user.getGmtCreate()));
                userMap.put(PrivilegeConstant.LAST_LOGIN,
                        (null == user.getLastLoginDate()) ? "" : dtf.format(user.getLastLoginDate()));
                resultList.add(userMap);
            }
            return new QueryResultArrayDTO(resultList, (int)page.getTotal(), 0, "");
        } catch (Exception e) {
            log.error(e);
            log.error("Failed to query user list. currentPage={}, sizePerPage={}", currentPage, size);
            return new QueryResultArrayDTO(null, 0, -1, "System Error!");
        }
    }
}
