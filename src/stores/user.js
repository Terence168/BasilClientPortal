import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";

export const useUserStore = defineStore("user", {
  state: () => ({
    username: "",
    email: "",
    company: "",
    permissoins: [],
    sessionStartTime: 0,
  }),
  getters: {
    loggedIn: (state) =>
      !(
        state.sessionStartTime === 0 ||
        state.sessionStartTime +
          import.meta.env.VITE_SESSION_DURATION * 60000 -
          5000 <
          Date.now()
      ),
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
        .then((response) => {
          if (response.data.resultCode === -55) {
            throw new Error(response.data.errorMessage);
          }

          const user = response.data.data[0];

          this.username = user.name;
          this.email = user.email;
          this.company = user.companyName;
          this.permissions = user.permissions;
        })
        .then(() => this.router.push({ name: "user" }))
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },

    logout() {
      this.$reset();
      this.router.push({ name: "login" });
    },
  },
  persist: true,
});
