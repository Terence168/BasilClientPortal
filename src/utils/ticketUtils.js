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
      let serials = [...response.data.data];
      return serials;
    })
    .catch((e) => {
      Notify.create({
        type: "negative",
        message: e.message,
      });
      return null;
    });
};

export const serialNumberUpdateQuery = async (sn, serialData) => {
  const actionURL = `/ticketing/serialNumberUpdate?serialNumber=${sn}`;
  const vm = this;

  return api
    .get(actionURL)
    .then(function (response) {
      if (response.data.resultCode !== 0) {
        throw new Error(response.data.errorMessage);
      }
      const queryData = response.data.data[0];
      const serial = {
        ...queryData,
        customerReportedIssue: serialData.customerReportedIssue,
        terminalID: serialData.terminalID,
      };
      const validSerial = validateSerial(serial);
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
  //If the SN is duplicate, show error
  // if (this.isSerialNumberUnqiue(newSerial.serialNumber) === false) {
  //   return;
  // }
  if (newSerial.existInAnotherTicket === true) {
    throw new Error(`SN ${newSerial.serialNumber} Already in the Warehouse. Can't add to ticket.`);
    // Notify.create({
    //   type: "negative",
    //   message: `SN ${newSerial.serialNumber} Already in the Warehouse. Can't add to ticket.`,
    // });
    // return null;
  }
  //If the serial don't have warranty information, hightlight grey
  if (
    newSerial.warrantyExpDate === null ||
    newSerial.warrantyStatus === null ||
    newSerial.warrantyStatus === "N/A" ||
    newSerial.warrantyExpDate === "N/A"
  ) {
    newSerial.bgColor = "bg-grey-5";
    newSerial.warrantyExpDate = "N/A";
    newSerial.warrantyStatus = "N/A";
  }
  if (
    newSerial.warrantyExpDate != null &&
    newSerial.warrantyExpDate != "N/A"
  ) {
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

