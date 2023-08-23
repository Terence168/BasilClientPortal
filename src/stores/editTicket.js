import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";
import {
  serialNumberUpdateQuery,
  validateSerial
} from "src/utils/ticketUtils";

const capacity = 10;

export const useEditTicketStore = defineStore("editTicket", {
  state: () => ({
    tickets: [],
  }),
  getters: {
    getSerialsByTicketId: (state) => {
      return (ticketId, query) => {
        const index = state.tickets.findIndex(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        if (index != -1) {
          const ticket = state.tickets.find((t) => 
                (parseInt(t.moOID) === parseInt(ticketId)
              ));
          let serials = ticket.serials;
          if(query != null){
            serials = ticket.serials.filter((t) => 
            (t.serialNumber != null && t.serialNumber.includes(query)) ||
            (t.model != null && t.model.includes(query)) ||
            (t.customerReportedIssueExt!= null && t.customerReportedIssueExt.includes(query)))
          }
          return serials;
        }

        return [];
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
    getTicketbyId: (state) => {
      return (ticketId) => {
        const index = state.tickets.findIndex((t) => {
          return parseInt(t.moOID) === parseInt(ticketId);
        });
        if (index != -1) {
          return state.tickets.find(
            (t) => parseInt(t.moOID) === parseInt(ticketId)
          );
        }
        return null;
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
    fetchTicket(ticketId) {
      //fetch it from the backend
      const actionURL = "/ticketing/" + ticketId;

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

          ticketInfo.edit = {
            deleteSerial: [],
            deleteTracking: [],
          };
          
          this.removeTicket(ticketId);
          this.tickets.push(ticketInfo);
          ticketInfo.serials = ticketInfo.serials.map((s) => validateSerial(s));
          
          return ticketInfo;
        });
    },

    getTicket(ticketId) {
      const index = this.tickets.findIndex((t) => {
        return parseInt(t.moOID) === parseInt(ticketId);
      });
      if (index != -1) {
        return this.tickets[index];
      }
      return this.fetchTicket(ticketId);
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
        if(serial.serialNumber != oldSN){
          const sIndex = ticket.serials.findIndex((s) => s.serialNumber === serial.serialNumber);
          if(sIndex != -1){
            Notify.create({
              type: "negative",
              message: `SN ${serial.serialNumber} Already in the Table.`,
            })
            return;
          }
        }

        serialNumberUpdateQuery(oldSN, ticket.moOID).then((wrappedSerial) => {
          const oldSerial = ticket.serials.find(
            (s) => s.serialNumber === oldSN
          );
          if (wrappedSerial != null) {
            oldSerial.customerReportedIssueExt =
              serial.customerReportedIssueExt;
            oldSerial.customerTerminalID = serial.customerTerminalID;
            oldSerial.isUpdate = true;
          }
        });
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
        ticket.edit.deleteSerial.push(xmOID);
      }
    },
    addSN(ticketId, serial) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        const sIndex = ticket.serials.findIndex((s) => s.serialNumber === serial.serialNumber);
        if(sIndex != -1){
          Notify.create({
            type: "negative",
            message: `SN ${serial.serialNumber} Already in the Table.`,
          })
          return;
        }
        serialNumberUpdateQuery(serial.serialNumber, ticket.moOID).then(
          (wrappedSerial) => {
            wrappedSerial.customerReportedIssueExt =
              serial.customerReportedIssueExt;
            wrappedSerial.customerTerminalID = serial.customerTerminalID;
            if (wrappedSerial != null) {
              ticket.serials.unshift(wrappedSerial);
            }
          }
        );
      }
    },
  
    findEditSN(ticketId, sn) {
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
          if (s.isUpdate === true) {
            result.updateSerial.push({
              xmOID: s.xmOID === null? s.pxmOID:s.xmOID,
              serialNumber: s.serialNumber,
              customerReportedIssueExt: s.customerReportedIssueExt,
              customerTerminalID: s.customerTerminalID,
              msnOID: s.msnOID,
              cosmetic: (s.cosmetic === null || s.cosmetic === false)?891:890
            });
          }
          //cosmetic: null, 891 -> false, 
          //cosmetic: 890 -> true


          if (s.xmOID === null && s.pxmOID === null) {
            //added serial
            result.addSerial.push({
              xmOID: s.xmOID,
              serialNumber: s.serialNumber,
              customerReportedIssueExt: s.customerReportedIssueExt,
              customerTerminalID: s.customerTerminalID,
              msnOID: s.msnOID,
              cosmetic: (s.cosmetic === null || s.cosmetic === false)?891:890
            });
          }
        });
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
        ticket.trackingNumbers.forEach((element) => {
          if (element.xitOID === null && element.num != null) {
            //find added tracking numbers
            result.addTracking.push({
              moOID: ticketId,
              num: element.num,
              xitOID: null,
            });
          } else if (
            element.xitOID != null &&
            element.num != element.oldValue
          ) {
            //find updated tracking numbers
            result.updateTracking.push({
              xitOID: element.xitOID,
              num: element.num,
              moOID: ticketId,
            });
          }
          result.deleteTracking = ticket.edit.deleteTracking;
        });
      }
      return result;
    },
    /**
     * For address
     */
    updateAddress(newAddress, ticketId) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        ticket.address = newAddress;
      }
    },
  },
  persist: true,
});
