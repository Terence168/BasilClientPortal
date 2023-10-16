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
    keys:null,

    keyType:null,
    keyTypeOpt:null,

    kcv:null,
    kcvOpt:null,

    ksi:null,
    ksiOpt:null,

    encrypt:null,
    custType:null,
    custTypeOpt:null,
  }),

  getters: {
    getSerials() {
      return this.serials;
    },
    getTrackingNums(){
      return this.trackingNums;
    },
    getAllSerials(){
      return this.serials;
    }
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
    populateKeyTypeOpt(_, update){
      // if (this.keyTypeOpt) {
      //   update();
      //   return;
      // }
      const link = "/ticketing/dropdown/key";
      api
        .get(link)
        .then((response) => {
          update(() => {
            const keyTypeSet = new Set();
            this.keys = response.data.data;
            this.keys.forEach((key) => {
              keyTypeSet.add(key.label.keyType);
            })

            const keyTypeArray = [];
            var index = 0;
            for(const keyType of keyTypeSet){
              keyTypeArray.push({
                value:keyType,
                label:keyType,
              })
              index += 1
            }

            this.keyTypeOpt = keyTypeArray;
            console.log(this.keyTypeOpt);
          });
        })
        .catch(function (error) {
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Key Type Dropdown cannot be populated",
          });
        });
    },
    populateKcvOpt(_, update){
      this.kcv = null;
      this.ksi = null;
      if(this.keyType != null){
        const kcvs = this.keys.filter((key) => key.label.keyType == this.keyType);
        const kcvArrays = [];
        kcvs.forEach((key) => {
          const data = {
            "value":key.label.kcv,
            "label":key.label.kcv,
          }
          kcvArrays.push(data);
        })

        this.kcvOpt = kcvArrays;
        console.log(this.kcvOpt);
      }
    },
    populateKsiOpt(_, update){
      if(this.keyType != null && this.kcv != null){
        const keys = this.keys.filter((key) => key.label.keyType == this.keyType && key.label.kcv == this.kcv);
        const ksiArray = [];
        keys.forEach((key) => {
          const data = {
            "value":key.value,
            "label":key.label.ksi,
          };
          ksiArray.push(data);
        })
        this.ksiOpt = ksiArray;
        console.log(this.ksiOpt);
      }
    },
    populateKeyTypeOptOnce(){
      const link = "/ticketing/dropdown/key_type";
      api
        .get(link)
        .then((response) => {
            this.keyTypeOpt = response.data.data;
        })
        .catch(function (error) {
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Key Type Dropdown cannot be populated",
          });
        });
    },
    populateOrderTypeOpt(_, update) {
      if (this.orderTypeOpt) {
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
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Order Type Dropdown cannot be populated",
          });
        });
    },
    populateOrderTypeOptOnce() {
      const link = "/ticketing/dropdown/repair_type";
      api
        .get(link)
        .then((response) => {
          this.orderTypeOpt = response.data.data;
          
        })
        .catch(function (error) {
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Order Type Dropdown cannot be populated",
          });
        });
    },
    populateCustTypeOpt(_, update){
      if (this.custTypeOpt) {
        update();
        return;
      }
      const link = "/ticketing/dropdown/cust_org";
      api
        .get(link)
        .then((response) => {
          this.custTypeOpt = response.data.data;
        })
        .catch(function (error) {
          console.log(error);
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
