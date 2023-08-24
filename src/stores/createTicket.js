import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";
import { serialNumberUpdateQuery } from "../utils/ticketUtils";

export const useCreateTicketStore = defineStore("createTicket", {
  state: () => ({
    orderType: null,
    orderTypeOpt: null,
    address: null,
    trackingNums: [],
    serials: [],
    inputValue: null,
  }),

  getters: {
    getSerials() {
      if (this.inputValue != null) {
        return this.serials.filter(
          (t) =>
            (t.serialNumber != null &&
              t.serialNumber.includes(this.inputValue)) ||
            (t.model != null && t.model.includes(this.inputValue)) ||
            (t.customerReportedIssueExt != null &&
              t.customerReportedIssueExt.includes(this.inputValue))
        );
      }
      return this.serials;
    },
  },
  reset() {
    this.$refs.state.inputValue.value = "";
  },
  actions: {
    /**
     * Tracking number
     */
    addTrackingNum() {
      this.trackingNums.push("");
    },
    deleteTrackingNum(index) {
      this.trackingNums.splice(index, 1);
    },
    populateOrderTypeOpt(_, update) {
      if (this.orderType) {
        update();
        return;
      }
      const link = "/ticketing/dropdown/repair_type";
      api
        .get(link)
        .then((response) => {
          update(() => {
            this.orderTypeOpt = response.data.data;
            // console.log(this.orderTypeOpt);
          });
        })
        .catch(function (error) {
          // handle error
          Notify.create({
            type: "negative",
            message: "Order Type Dropdown cannot be populated",
          });
        });
    },
    /**
     * Serial
     */
    addSerialList(list) {
      list.forEach((element) => {
        if (this.isSerialNumberUnqiue(element.serialNumber) === true) {
          this.serials.unshift(element);
        }
      });
    },
    addSerial(serialData) {
      //If the SN is duplicate, show error
      if (this.isSerialNumberUnqiue(serialData.serialNumber) === false) {
        return;
      }
      //go to backend to validate it
      serialNumberUpdateQuery(serialData.serialNumber, null).then((serial) => {
        if (serial != null) {
          serial.customerReportedIssueExt = serialData.customerReportedIssueExt;
          serial.customerTerminalID = serialData.customerTerminalID;
          this.serials.unshift(serial);
        }
      });
    },
    updateSerial(oldSerialNumber, serialData) {
      if (
        oldSerialNumber != serialData.serialNumber &&
        this.isSerialNumberUnqiue(serialData.serialNumber) === false
      ) {
        //If the SN is duplicate, show error
        return;
      }
      serialNumberUpdateQuery(serialData.serialNumber, null).then((serial) => {
        //go to backend to validate it
        if (serial != null) {
          serial.customerReportedIssueExt = serialData.customerReportedIssueExt;
          serial.customerTerminalID = serialData.customerTerminalID;
          const index = this.serials.findIndex(
            (s) => oldSerialNumber === s.serialNumber
          );
          this.serials[index] = serial;
        }
      });
    },
    resetTicket() {
      this.$reset();
    },
    removeSerial(serialNumber) {
      const index = this.serials.findIndex(
        (s) => serialNumber === s.serialNumber
      );
      if (index >= 0) {
        this.serials.splice(index, 1);
      }

      if (this.page > 1) {
        this.page = Math.ceil(this.getTotal / this.perPage);
      }
    },

    isSerialNumberUnqiue(serialNumber) {
      const index = this.serials.findIndex(
        (s) => serialNumber === s.serialNumber
      );
      if (index != -1) {
        Notify.create({
          type: "negative",
          message: `SN ${serialNumber} Already in the Table.`,
        });
        return false;
      }
      return true;
    },
  },

  persist: true,
});
