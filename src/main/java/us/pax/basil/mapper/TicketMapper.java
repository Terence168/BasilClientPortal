package us.pax.basil.mapper;


import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Options;
import us.pax.basil.entity.ticket.*;
public interface TicketMapper extends BaseMapper<Integer> {

    List<Department> queryDepartmentList();
    List<OrderType> queryOrderTypeList();
    List<Status> queryStatusList();
    List<RepairType> queryRepairTypeList();
    Integer getTicketingTotal(String id,
                              String[] ticketId,
                              Integer department,
                              Integer type,
                              Integer status,
                              String responder,
                              String[] serialNumber,
                              String createdFromDate,
                              String createdToDate
                              );
    List<TicketingQueue> getTicketing(Integer offset,
                                      Integer count,
                                      String sortColumns,
                                      String id,
                                      String[] ticketId,
                                      Integer department,
                                      Integer type,
                                      Integer status,
                                      String responder,
                                      String[] serialNumber,
                                      String createdFromDate,
                                      String createdToDate
                                      );

    List<String> findUSBasedDevices(List<String> serialNumbersInFile);
    List<Device> getDeviceInfos(List<String> usBasedDevices, String companyId);

    @Options(useGeneratedKeys = true, keyProperty = "mo_OID")
    Void insertPrep_Master_Order(TicketInsertionObject tio);
}
