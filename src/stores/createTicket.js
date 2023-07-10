import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

export const useCreateTicketStore = defineStore("createTicket", {
  state: () => ({ orderType: null, trackingNums: [], serials: [] }),

  getters: {},

  actions: {
    addTrackingNum() {
      this.trackingNums.push("");
    },

    deleteTrackingNum(index) {
      this.trackingNums.splice(index, 1);
    },

    addSerial(serialNumber, customerReportedIssue, terminalID) {
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

    resetTicket() {
      this.$reset();
    },
  },

  persist: true,
});
