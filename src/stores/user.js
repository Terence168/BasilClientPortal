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
    login(username, password) {
      const formData = new FormData();
      formData.append("username", username);
      formData.append("password", password);

      api
        .post("login", formData, {
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
        })
        .then((response) => {
          if (response.data.code === 20000) {
            return api.get("user/detail");
          } else {
            throw new Error(
              "Incorrect username and/or password. Please try again."
            );
          }
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
