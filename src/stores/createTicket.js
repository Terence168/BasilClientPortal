import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

export const useCreateTicketStore = defineStore("createTicket", {
  state: () => ({
    orderType: null,
    orderTypeOpt: null,
    trackingNums: [],
    serials: [],
  }),

  getters: {
    getSerials() {
      return this.serials.slice().reverse();
    },
  },

  actions: {
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
          });
        })
        .catch(function (error) {
          // handle error
          console.log(error);
          Notify.create({
            type: "negative",
            message: "Order Type Dropdown cannot be populated",
          });
        });
    },

    addSerial(serialData) {
      const { serialNumber, customerReportedIssue, terminalID } = serialData;
      if (this.isSerialNumberUnqiue(serialNumber) === false) {
        return;
      }
      const newSerial = {
        cosmetic: false,
        serialNumber: serialNumber,
        model: null,
        version: null,
        customerReportedIssue: customerReportedIssue,
        terminalID: terminalID,
        warrantyExpDate: null,
        warrantyStatus: null,
        customerRMA: null,
        repairPrice: null,
        show: true,
        loading: false,
      };
      this.serials.push(newSerial);
    },

    updateSerial(serialData, oldSerialNumber) {
      const { serialNumber, customerReportedIssue, terminalID } = serialData;
      if (serialNumber != oldSerialNumber && this.isSerialNumberUnqiue(serialNumber) === false) {
        return;
      }
      const newSerial = {
        cosmetic: false,
        serialNumber: serialNumber,
        model: null,
        version: null,
        customerReportedIssue: customerReportedIssue,
        terminalID: terminalID,
        warrantyExpDate: null,
        warrantyStatus: null,
        customerRMA: null,
        repairPrice: null,
        show: true,
        loading: false,
      };
      const index = this.serials.findIndex(
        (s) => oldSerialNumber === s.serialNumber
      );
      this.serials[index] = newSerial;
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
    },

    isSerialNumberUnqiue(serialNumber) {
      const index = this.serials.findIndex(
        (s) => serialNumber === s.serialNumber
      );
      if (index != -1) {
        Notify.create({
          type: "negative",
          message: "Serial Numbers are not Unique",
        });
        return false;
      }
      return true;
    },
  },

  persist: true,
});
