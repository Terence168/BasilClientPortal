import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";
import { serialNumberUpdateQuery } from "../utils/ticketUtils";

const KEY_CATEGORY_PRODUCTION = "PRODUCTION";
const KEY_CATEGORY_TEST = "TEST";

function normalizeKeyCategory(category) {
  const normalized = String(category || "").trim().toUpperCase();
  if (!normalized) {
    return "";
  }
  if (normalized.includes("PROD")) {
    return KEY_CATEGORY_PRODUCTION;
  }
  if (normalized.includes("TEST")) {
    return KEY_CATEGORY_TEST;
  }
  return normalized;
}

function buildCategoryOptions(keys) {
  const categories = new Set(
    (keys || []).map((key) => normalizeKeyCategory(key?.label?.keyCategory))
  );
  const options = [];
  if (categories.has(KEY_CATEGORY_PRODUCTION)) {
    options.push({ value: KEY_CATEGORY_PRODUCTION, label: "Production" });
  }
  if (categories.has(KEY_CATEGORY_TEST)) {
    options.push({ value: KEY_CATEGORY_TEST, label: "Test" });
  }

  if (options.length > 0) {
    return options;
  }

  return [
    { value: KEY_CATEGORY_PRODUCTION, label: "Production" },
    { value: KEY_CATEGORY_TEST, label: "Test" },
  ];
}

export const useCreateTicketStore = defineStore("createTicket", {
  state: () => ({
    orderDept: null,
    orderDeptOpt: null,
    orderDeptOptSource: null,
    address: null,
    trackingNums: [],
    serials: [],
    inputValue: null,
    keys: null,

    keyType: null,
    keyTypeOpt: null,

    kcv: null,
    kcvOpt: null,

    ksi: null,
    ksiOpt: null,

    kcvksi: null,
    kcvksiOpt: null,

    encrypt: null,
    custType: null,
    custTypeOpt: null,
  }),

  getters: {
    getSerials() {
      return this.serials;
    },
    getTrackingNums() {
      return this.trackingNums;
    },
    getAllSerials() {
      return this.serials;
    },
  },
  reset() {
    this.$refs.state.inputValue.value = "";
  },
  actions: {
    applyOrderDeptOptions(options) {
      const normalizedOptions = Array.isArray(options) ? options : [];
      this.orderDeptOpt = normalizedOptions;
      this.orderDeptOptSource = "department";

      if (
        this.orderDept != null &&
        !normalizedOptions.some(
          (item) => String(item?.value) === String(this.orderDept)
        )
      ) {
        this.orderDept = null;
      }
    },
    /**
     * Tracking number
     */
    addTrackingNum() {
      this.trackingNums.push("");
    },
    deleteTrackingNum(index) {
      this.trackingNums.splice(index, 1);
    },
    populateKeyTypeOpt(_, update) {
      const applyOptions = () => {
        this.keyTypeOpt = buildCategoryOptions(this.keys);
        if (this.keyType != null) {
          this.populateKcvksiOpt();
        }
      };

      if (Array.isArray(this.keys) && this.keys.length > 0) {
        if (typeof update === "function") {
          update(() => {
            applyOptions();
          });
        } else {
          applyOptions();
        }
        return;
      }

      const link = "/ticketing/dropdown/key";
      api
        .get(link)
        .then((response) => {
          const payload = response?.data?.data;
          this.keys = Array.isArray(payload) ? payload : [];

          if (typeof update === "function") {
            update(() => {
              applyOptions();
            });
          } else {
            applyOptions();
          }
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
    populateKcvksiOpt() {
      const previous = this.kcvksi;
      const selectedCategory = normalizeKeyCategory(this.keyType);

      if (!Array.isArray(this.keys) || selectedCategory.length === 0) {
        this.kcvksi = null;
        this.kcvksiOpt = [];
        return;
      }

      const keyOptions = this.keys
        .filter(
          (key) =>
            normalizeKeyCategory(key?.label?.keyCategory) === selectedCategory
        )
        .map((key) => {
          const label = key?.label || {};
          const keyIndex = label.keyIndex;
          const keyId = label.keyId == null ? "" : String(label.keyId).trim();
          const keyType = label.keyType == null ? "" : String(label.keyType);
          const comment = label.comment == null ? "" : String(label.comment);
          const descriptionParts = [keyType, comment].filter(
            (part) => part != null && part.trim().length > 0
          );

          return {
            value: keyIndex,
            label: keyId.length > 0 ? keyId : `KEY-${keyIndex}`,
            comment: descriptionParts.join(" | "),
          };
        })
        .filter((option) => option.value != null)
        .sort((a, b) =>
          String(a.label).localeCompare(String(b.label), undefined, {
            numeric: true,
            sensitivity: "base",
          })
        );

      this.kcvksiOpt = keyOptions;
      this.kcvksi = keyOptions.some((option) => option.value === previous)
        ? previous
        : null;
    },
    populateKcvOpt(_, update) {
      this.populateKcvksiOpt();
      if (typeof update === "function") {
        update(() => {});
      }
    },
    populateKsiOpt(_, update) {
      if (typeof update === "function") {
        update(() => {});
      }
    },
    populateKeyTypeOptOnce() {
      const link = "/ticketing/dropdown/key";
      api
        .get(link)
        .then((response) => {
          const payload = response?.data?.data;
          this.keys = Array.isArray(payload) ? payload : [];
          this.keyTypeOpt = buildCategoryOptions(this.keys);
          this.populateKcvksiOpt();
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
    populateOrderDeptOpt(_, update) {
      if (
        Array.isArray(this.orderDeptOpt) &&
        this.orderDeptOpt.length > 0 &&
        this.orderDeptOptSource === "department"
      ) {
        update();
        return;
      }
      const link = "/ticketing/dropdown/department";
      api
        .get(link)
        .then((response) => {
          if (response?.data?.resultCode !== 0) {
            throw new Error(
              response?.data?.errorMessage ||
                "Order Dept Dropdown cannot be populated"
            );
          }
          update(() => {
            this.applyOrderDeptOptions(response?.data?.data);
          });
        })
        .catch(function (error) {
          // if(!this.loggedIn) return;
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Order Dept Dropdown cannot be populated",
          });
        });
    },
    populateOrderDeptOptOnce() {
      if (
        Array.isArray(this.orderDeptOpt) &&
        this.orderDeptOpt.length > 0 &&
        this.orderDeptOptSource === "department"
      ) {
        return Promise.resolve(this.orderDeptOpt);
      }
      const link = "/ticketing/dropdown/department";
      return api
        .get(link)
        .then((response) => {
          if (response?.data?.resultCode !== 0) {
            throw new Error(
              response?.data?.errorMessage ||
                "Order Dept Dropdown cannot be populated"
            );
          }
          this.applyOrderDeptOptions(response?.data?.data);
          return this.orderDeptOpt;
        })
        .catch(function (error) {
          console.log(error);
          // handle error
          Notify.create({
            type: "negative",
            message: "Order Dept Dropdown cannot be populated",
          });
          return [];
        });
    },

    populateCustTypeOpt(val, update, abort) {
      if (val.length < 3) {
        abort();
        return;
      }

      const link = "user/customers?customerName=" + val;

      api
        .get(link)
        .then((response) => {
          update(() => {
            this.custTypeOpt = response.data.data;
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

    // populateCustTypeOpt(_, update){
    //   if (this.custTypeOpt) {
    //     update();
    //     return;
    //   }
    //   const link = "/ticketing/dropdown/cust_org";
    //   api
    //     .get(link)
    //     .then((response) => {
    //       this.custTypeOpt = response.data.data;
    //     })
    //     .catch(function (error) {
    //       console.log(error);
    //       // handle error
    //       Notify.create({
    //         type: "negative",
    //         message: "Order Type Dropdown cannot be populated",
    //       });
    //     });
    // },
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
          serial.customerRMA = serialData.customerRMA;
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
          serial.customerRMA = serialData.customerRMA;
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

  persist: false,
});
