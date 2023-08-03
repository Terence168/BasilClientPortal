import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

const capacity = 10;

export const useEditTicketStore = defineStore("editTicket", {
  state: () => ({
    ticketMap: new Map(),
  }),
  getters: {},
  reset() {},
  actions: {
    addTicket(ticketInfo) {
      const key = ticketInfo.mo_oid;
      if (this.ticketMap.has(key)) {
        this.ticketMap.delete(key);
      } else if (this.ticketMap.size >= capacity) {
        const firstKey = this.ticketMap.keys().next().value;
        this.ticketMap.delete(firstKey);
      }
      this.ticketMap.set(key, ticketInfo);
    },
    removeTicket(ticketId) {
      if (this.ticketMap.has(ticketId)) {
        this.ticketMap.delete(ticketId);
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
    fetchTicket(ticketId) {
      if (this.ticketMap.has(ticketId)) {
        const value = this.ticketMap.get(ticketId);
        this.ticketMap.delete(ticketId);
        this.ticketMap.set(ticketId, value);
        return value;
      } else {
        //request from the back end
        const actionURL = "/ticketing/viewEditTicket?id=" + ticketId;
        const vm = this;
        api
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
            this.ticketMap.set(ticketId, ticketInfo);
            console.log(ticketInfo);
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
  },
  persist: true,
});
