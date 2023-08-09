import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

const capacity = 10;

export const useEditTicketStore = defineStore("editTicket", {
  state: () => ({
    tickets: [],
  }),
  getters: {},
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
    fetchTicket(ticketId) {
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
    async getTicket(ticketId) {
      const index = this.tickets.findIndex((t) => {
        return parseInt(t.moOID) === parseInt(ticketId);
      });
      if (index != -1) {
        return this.tickets.find(
          (t) => parseInt(t.moOID) === parseInt(ticketId)
        );
      } else {
        const actionURL = "/ticketing/viewEditTicket?id=" + ticketId;

        try {
          const response = await api.get(actionURL, {
            headers: {
              "Content-Type": "application/json",
            },
          });

          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          const ticketInfo = response.data.data;
          ticketInfo.timeStamp = new Date();
          this.tickets.push(ticketInfo);

          return ticketInfo;
        } catch (error) {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });

          return null;
        }
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
    removeSN(ticketId, serial) {
      const sn = serial.serialNumber;
      if (this.ticketMap.has(ticketId)) {
        const ticket = this.ticketMap.get(key);
        const index = ticket.serials.findIndex(sn);
        if (index > -1) {
          serials.slice(index, 1);
        }
      }
    },
    addSN(ticketId, serial) {
      if (this.ticketMap.has(ticketId)) {
        const ticket = this.ticketMap.get(key);
        ticket.serials.push(serial);
      }
    },
  },
  persist: true,
});
