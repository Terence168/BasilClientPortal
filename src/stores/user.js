import { defineStore } from "pinia";
import { api } from "boot/axios";

export const useUserStore = defineStore("user", {
  state: () => ({
    username: null,
    email: null,
    sessionStartTime: null,
  }),
  getters: {
    loggedIn: (state) =>
      state.sessionStartTime === null ||
      state.sessionStartTime +
        import.meta.env.VITE_SESSION_DURATION * 60000 -
        5000 <
        Date.now(),
  },
  actions: {
    login(payload) {
      api
        .post("login", payload)
        .then((res) => {
          console.log(res);
        })
        .catch(() => {
          console.log("error");
        });
    },

    logout() {
      this.$reset();
    },
  },
});
