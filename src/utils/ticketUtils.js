import { api } from "src/boot/axios";
import { Notify } from "quasar";


export const batchSerialNumberQuery = async (formData) => {
  const actionURL = "/ticketing/batchSerialNumberQuery";

  return api
    .post(actionURL, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    .then(function (response) {
      if (response.data.resultCode !== 0) {
        throw new Error(response.data.errorMessage);
      }
      const serials = [...response.data.data];
      const result = [];
      serials.forEach((s) => {
        let sn = validateSerial(s);
        if(sn != null){
          result.push(sn);
        }
      })
      return result;
    })
    .catch((e) => {
      Notify.create({
        type: "negative",
        message: e.message,
      });
      return null;
    });
};

export const serialNumberUpdateQuery = async (sn, mo_OID) => {
  const actionURL = `/ticketing/serialNumberUpdate?serialNumber=${sn}`;
  const vm = this;

  return api
    .get(actionURL)
    .then(function (response) {
      if (response.data.resultCode !== 0) {
        throw new Error(response.data.errorMessage);
      }
      const queryData = response.data.data[0];
      const validSerial = validateSerial(queryData, mo_OID);
      return validSerial;
    })
    .catch((e) => {
      Notify.create({
        type: "negative",
        message: e.message,
      });
      return null;
    })
};

export const validateSerial = (serial, moOID) => {
  const newSerial = {
    ...serial,
    cosmetic: false,
    show: true,
    loading: false,
    valid: true,
    bgColor: null,
    invoiceAmt: 0,
  };
  
  if (newSerial.moOID != moOID && newSerial.existInAnotherTicket === true) {
    Notify.create({
      type: "negative",
      message: `SN ${newSerial.serialNumber} Already in the Warehouse. Can't add to ticket.`,
    });
    return null;
  }
  //If the serial don't have warranty information, hightlight grey and prevent user to submit it
  if(newSerial.warrantyStatus === "N/A" || newSerial.warrantyStatus === null){
    newSerial.bgColor = "bg-grey-5";
    newSerial.valid = false;
    newSerial.warrantyExpDate = "N/A";
  }
  // if(newSerial.warrantyStatus === "Order Date Missing"){
  //   newSerial.bgColor = "bg-grey-5";
  //   newSerial.valid = false;
  // }
  //If it's not a us-based serial, hightlight yellow
  if (newSerial.resultCode === -1) {
    newSerial.bgColor = "bg-warning";
    newSerial.valid = false;
  }
  return newSerial;
}

