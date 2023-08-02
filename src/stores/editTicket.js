import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

const capacity = 10;

export const useEditTicketStore = defineStore("editTicket", {
  state: () => ({
    ticketMap: [],
  }),
  getters: {
    async getTicket(ticketId) {
      const index = this.ticketMap.findIndex((t) => t.mo_oid === ticketId);
      if (index > 0) {
        this.ticketMap[index].timeStamp = new Date();
        return this.ticketMap[index];
      } else {
        //request from the back end
        const actionURL = "/ticketing/viewEditTicket?id=" + ticketId;
        const vm = this;

        this.$api
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
            return ticketInfo;
          })
          .catch((error) => {
            console.log(error);
            this.$q.notify({
              type: "negative",
              message: error.message,
            });
          });
      }
    },
  },
  reset() {},
  actions: {
    addTicket(ticketInfo) {
      if (this.ticketMap.length == capacity) {
        removeTicket();
      }
      ticketInfo.timeStamp = new Date();
      this.ticketMap.push(ticketInfo);
    },
    removeTicket() {
      this.ticketMap.sort(
        (t1, t2) => new Date(t1).getTime() - new Date(t2).getTime()
      );
      this.ticketMap.pop();
    },
    removeTicket(ticketId) {
      const index = this.ticketMap.findIndex((t) => t.mo_oid === ticketId);
      if (index > 0) {
        this.ticketMap.slice(index, 1);
      }
    },
  },
  persist: true,
});
