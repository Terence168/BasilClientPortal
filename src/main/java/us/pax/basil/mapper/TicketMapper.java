package us.pax.basil.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.actuate.integration.IntegrationGraphEndpoint;
import us.pax.basil.dto.output.SubmitTicketDTO;
import us.pax.basil.entity.customer.Customer;
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


    List<RepairRecord> getRepairDetail(Integer id);
    List<RepairRecord> getPrepRepairDetail(Integer id);
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
    void insertSingleXref_Inbound_Tracking(TrackingNum trackingNum);
    TicketInfo existingMasterOrder(String id);
    TicketInfo existingPREPMasterOrder(String id);

    List<TrackingNum> getTrackingNumber(String id);

    List<SNInfo> getOdsMaterials(@Param("id")String id, @Param("companyId")Integer companyId);

    List<SNInfo> getSecMaterials(@Param("id")String id, @Param("companyId")Integer companyId);

    Integer insertResponse(TicketResponse ticketResponse);

    List<TicketResponse> getResponse(String id);

    void updateXref_Inbound_Tracking(List<TrackingNum> updateTracking);
    void deleteXref_Inbound_Tracking(List<String> deleteTracking);

    void batchInsertXref_Inbound_Tracking(List<TrackingNum> addTracking);

    void deletePrep_Xref_Materials(List<String> deleteTracking);
    void deleteXref_Materials(List<String> deleteTracking);

    void updateXref_Materials(List<SNsInsertionObject> updateSerials);
    void updatePrep_Xref_Materials(List<SNsInsertionObject> updateSerials);

    void updateMasterOrder(Integer typeOfRepair, String originalRMA, String moOID, Integer xaOID, String testKeyType);
//            TicketEditDTO ticketEditDTO, String id);
    void updatePrepMasterOrder(Integer typeOfRepair, String originalRMA,  String moOID, Integer xaOID, String testKeyType);

    List<Key> getAllKeys();
    List<String> getAllKeyType();
    List<Key> getAllKey(String keyType, String kcv);

    void ackMasterTicket(Long moOID);
    void unAckMasterTicket(Long moOID);
    void ackPrepMasterTicket(Long moOID);
    void unAckPrepMasterTicket(Long moOID);

    List<Integer> getTicketAckStatus(Long moOID);

    List<Customer> getAllCustomerOrg();

}
