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

export const serialNumberUpdateQuery = async (sn) => {
  const actionURL = `/ticketing/serialNumberUpdate?serialNumber=${sn}`;
  const vm = this;

  return api
    .get(actionURL)
    .then(function (response) {
      if (response.data.resultCode !== 0) {
        throw new Error(response.data.errorMessage);
      }
      const queryData = response.data.data[0];
      const validSerial = validateSerial(queryData);
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

export const validateSerial = (serial) => {
  const newSerial = {
    ...serial,
    cosmetic: false,
    show: true,
    loading: false,
    valid: true,
    bgColor: null,
    invoiceAmt: 0,
  };
  if (newSerial.existInAnotherTicket === true) {
    Notify.create({
      type: "negative",
      message: `SN ${newSerial.serialNumber} Already in the Warehouse. Can't add to ticket.`,
    });
    return null;
  }
  //If the serial don't have warranty information, hightlight grey and prevent user to submit it
  if(newSerial.warrantyStatus === "N/A" || newSerial.warrantyStatus === "Order Date Missing" || newSerial.warrantyStatus === null || newSerial.warrantyExpDate === "N/A"){
    newSerial.bgColor = "bg-grey-5";
    newSerial.valie = false;
    newSerial.warrantyExpDate = "N/A";
  }
  else{
    const date = new Date(newSerial.warrantyExpDate);
    newSerial.warrantyExpDate = date.toLocaleDateString("un-US");
  }
  //If it's not a us-based serial, hightlight yellow
  if (newSerial.resultCode === -1) {
    newSerial.bgColor = "bg-warning";
    newSerial.valid = false;
  }
  return newSerial;
}

