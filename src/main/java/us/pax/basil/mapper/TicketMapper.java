package us.pax.basil.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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

    Integer getTicketingViewsTotal(String id,
                              String[] ticketId,
                              Integer department,
                              Integer type,
                              Integer status,
                              String responder,
                              String[] serialNumber,
                              String createdFromDate,
                              String createdToDate, String lastResponse,String customerOrganization, String customerId
    );

    List<TicketView> getTicketingViews(Integer offset,
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
                      String createdToDate, String lastResponse,String customerOrganization, String customerId
    );

    List<TicketViewDetail> getTicketingViewsDetail(String id);

    /*
    queryDeviceBase is used to query if this device is U.S. based. If not, alert the front-end, otherwise, check if within warranty or another ticket.
     */
    Device queryDevice(String serialNumber);
    Device queryDeviceDuplicate(String serialNumber);

    List<String> findUSBasedDevices(List<String> serialNumbersInFile);
    List<Device> getDeviceInfos(List<String> usBasedDevices, String companyId);

//    @Options(useGeneratedKeys = true, keyProperty = "moOID", keyColumn = "moOID")
    void insertPrep_Master_Order(TicketInsertionObject tio);

    void insertPrep_Xref_Materials(List<SNsInsertionObject> sNsInsertionObjectList);
    void insertXref_Inbound_Tracking(@Param("trackingNumbers")List<String> trackingNumbers, @Param("mo_OID")Integer mo_OID);


    TicketInfo existingMasterOrder(String id);

    TicketInfo existingPREPMasterOrder(String id);

    List<String> getTrackingNumber(String id);

    List<String> getSerialNumber(String id);

    List<SNInfo> getOdsMaterials(String id);

    List<SNInfo> getSecMaterials(String id);
}
