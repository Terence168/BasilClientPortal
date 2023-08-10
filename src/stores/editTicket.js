import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";
import {serialNumberUpdateQuery} from "../utils/ticketUtils.js"

const capacity = 10;

export const useEditTicketStore = defineStore("editTicket", {
  state: () => ({
    tickets: [],
  }),
  getters: {
    getSerialsByTicketId: (state) => {
      return (ticketId) => {
        const index = state.tickets.findIndex(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        if (index != -1) {
          const ticket = state.tickets.find(
            (t) => parseInt(t.moOID) === parseInt(ticketId)
          );
          const serials = ticket.serials;
          return serials;
        }
      };
    },
    getTrackingNumsByTicketId: (state) => {
      return (ticketId) => {
        const index = state.tickets.findIndex(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        if (index != -1) {
          const ticket = state.tickets.find(
            (t) => parseInt(t.moOID) === parseInt(ticketId)
          );
          return ticket.trackingNumbers;
        }
      };
    },
  },
  reset() {},
  actions: {
    addTicket(ticketInfo) {
      const key = ticketInfo.mo_oid;
      const index = this.tickets.findIndex((t) => t.mo_oid === key);
      if (index === -1) {
        if (this.tickets.length === capacity) {
          //remove the least recently used
          this.tickets.sort((t1, t2) => t1.timeStamp < t2.timeStamp);
          this.tickets.pop();
        }
        ticketInfo.timeStamp = new Date();
        this.tickets.push(ticketInfo);
      }
    },
    removeTicket(ticketId) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        this.tickets.splice(index, 1);
      }
    },
    async fetchTicket(ticketId) {
      //fetch it from the backend
      const actionURL = "/ticketing/viewEditTicket?id=" + ticketId;
      const vm = this;
      return api
        .get(actionURL, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          const ticketInfo = response.data.data;

          ticketInfo.timeStamp = new Date();
            ticketInfo.trackingNumbers.forEach((t) => {
              const deepcopy = JSON.parse(JSON.stringify(t.num));
              t.oldValue = deepcopy;
            });

            ticketInfo.edit = {
              deleteTracking: [],
              deleteSerial: [],
            };

          this.removeTicket(ticketId);
          this.tickets.push(ticketInfo);
          return ticketInfo;
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
          return null;
        });
    },
    getTicket(ticketId) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        return new Promise((resolve, reject) =>
          resolve(
            this.tickets.find((t) => parseInt(t.moOID) === parseInt(ticketId))
          )
        );
      } else {
        const actionURL = "/ticketing/viewEditTicket?id=" + ticketId;
        const vm = this;
        return api
          .get(actionURL, {
            headers: {
              "Content-Type": "application/json",
            },
          })
          .then((response) => {
            if (response.data.resultCode !== 0) {
              throw new Error(response.data.errorMessage);
            }
            const ticketInfo = response.data.data;
            ticketInfo.timeStamp = new Date();
            ticketInfo.trackingNumbers.forEach((t) => {
              const deepcopy = JSON.parse(JSON.stringify(t.num));
              t.oldValue = deepcopy;
            });

            ticketInfo.edit = {
              deleteTracking: [],
              deleteSerial: [],
            };
            this.tickets.push(ticketInfo);

            return ticketInfo;
          })
          .catch((error) => {
            console.log(error);
            Notify.create({
              type: "negative",
              message: error.message,
            });
            return null;
          });
      }
    },
    /**
     * For serials
     */
    updateSN(ticketId, oldSN, serial) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        serialNumberUpdateQuery(oldSN, serial).then((wrappedSerial) => {
          const oldSerial = ticket.serials.find((s) => s.serialNumber === oldSN);
          if (wrappedSerial != null) {
            oldSerial.serialNumber = serial.serialNumber;
            oldSerial.customerReportedIssue = serial.customerReportedIssue;
            oldSerial.terminalID = serial.terminalID;
            oldSerial.isUpdate = true;
          }
        })
      }
    },
    removeSN(ticketId, sn) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        const serials = ticket.serials;
        const sIndex = serials.findIndex((s) => s.serialNumber === sn);
        const serial = serials[sIndex];
        const xmOID = serial.xmOID;
        if (sIndex != -1) {
          serials.splice(sIndex, 1);
        }
        //todo: touch the ticket
        ticket.edit.deleteSerial.push(xmOID);
      }
    },
    addSN(ticketId, serial) {
      console.log("in add sn");
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        serialNumberUpdateQuery(serial.serialNumber, serial)
          .then((wrappedSerial) => {
              if(wrappedSerial != null){
                ticket.serials.unshift(serial);
              }
        })
      }
    },
    findEditSN(ticketId, sn){
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      const result = {
        addSerial: [],
        updateSerial: [],
        deleteSerial: [],
      };
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        result.deleteSerial = ticket.edit.deleteSerial;
        ticket.serials.forEach((s) => {
          if(s.isUpdate === true){
            result.updateSerial.push({
              xmOID: s.xmOID,
              serialNumber: s.serialNumber,
              customerReportedIssue: s.customerReportedIssue,
              terminalID:s.terminalID
            })
          }
          if(s.xmOID === null){
            //added serial
            result.addSerial.push({
              serialNumber: s.serialNumber,
              customerReportedIssue: s.customerReportedIssue,
              terminalID:s.terminalID
            })
          }
        })
        return result;
      }
    },
    /**
     * For tracking numbers
     */
    addTrackingNum(ticketId) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        ticket.trackingNumbers.push({ num: "", xitOID: null });
      }
    },
    deleteTrackingNum(ticketId, tIndex) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        const { xitOID } = ticket.trackingNumbers[tIndex];
        ticket.trackingNumbers.splice(tIndex, 1);
        if (xitOID != null) {
          ticket.edit.deleteTracking.push(xitOID);
        }
      }
    },
    findEditTrackingNums(ticketId) {
      const result = {
        addTracking: [],
        updateTracking: [],
        deleteTracking: [],
      };
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        console.log(ticket.trackingNumbers);
        ticket.trackingNumbers.forEach((element) => {
          if (element.xitOID === null && element.num != null) {
            //find added tracking numbers
            result.addTracking.push(element.num);
          } else if (
            element.xitOID != null &&
            element.num != element.oldValue
          ) {
            //find updated tracking numbers
            result.updateTracking.push({
              xitOID: element.xitOID,
              newValue: element.num,
            });
          }
          result.deleteTracking = ticket.edit.deleteTracking;
        });
      }
      return result;
    },
  },
  persist: true,
});
