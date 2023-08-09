import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

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
    addSN(ticketId, serial) {
      if (this.ticketMap.has(ticketId)) {
        const ticket = this.ticketMap.get(key);
        ticket.serials.push(serial);
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
              const deepcopy = JSON.parse(
                JSON.stringify(t.num)
              );
              t.oldValue = deepcopy;
            })

            ticketInfo.edit = {
              deleteTracking: [],
              updateTracking: [],
              addTrackingNum: [],

              deleteSerails: [],
              updateSerails: [],
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
    updateSN(tickId, serial) {
      const sn = serial.serialNumber;
      if (this.ticketMap.has(tickId)) {
        const ticket = this.ticketMap.get(key);
        const index = ticket.serials.findIndex(sn);
        ticket.serials[index] = serial;
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
        if (sIndex != -1) {
          serials.splice(sIndex, 1);
        }
        //touch the ticket
        this.tickets[index].timeStamp = new Date();
      }
    },
    addSN(ticketId, serial) {
      if (this.ticketMap.has(ticketId)) {
        const ticket = this.ticketMap.get(key);
        ticket.serials.push(serial);
      }
    },

    getEditInfo(ticketId) {
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1) {
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        return ticket.edit;
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
        ticket.trackingNumbers.push({num: "", xitOID: null});
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
        console.log(ticket.trackingNumbers[tIndex], xitOID);
        ticket.trackingNumbers.splice(tIndex, 1);
        if(xitOID != null){
          ticket.edit.deleteTracking.push(xitOID);
        }
      }
    },
    findEditTrackingNums(ticketId){
      const index = this.tickets.findIndex(
        (t) => parseInt(t.moOID) === parseInt(ticketId)
      );
      if (index != -1){
        const ticket = this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
        ticket.trackingNumbers.forEach(element => {
          if(element.xitOID === null && element.num != null){
            ticket.edit.addTrackingNum.push(element.num);
          }
          else if(element.xitOID != null && element.num != element.oldValue){
            //update 
            ticket.edit.updateTracking.push({xitOID:element.xitOID, newValue:element.num});
      }});
      };
      }
  },
  persist: true,
});
