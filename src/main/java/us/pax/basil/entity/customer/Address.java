package us.pax.basil.entity.customer;

import lombok.Data;

@Data
public class Address {
    private int xaOid;
    private int mcOid;
    private String attentionTo;
    private String shipToCompany;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String defaultInd;
    private int version;
    
    @Override
    public String toString() {
        return "XrefAddress{" +
            "xaOid=" + xaOid +
            ", mcOid=" + mcOid +
            ", attentionTo='" + attentionTo + '\'' +
            ", shipToCompany='" + shipToCompany + '\'' +
            ", address='" + address + '\'' +
            ", address2='" + address2 + '\'' +
            ", city='" + city + '\'' +
            ", state='" + state + '\'' +
            ", zipCode='" + zipCode + '\'' +
            ", defaultInd='" + defaultInd + '\'' +
            ", version=" + version +
            '}';
    }
}
