import { defineStore } from "pinia";
import { api } from "boot/axios";
import { Notify } from "quasar";
import { throttleFilter } from "@vueuse/core";

export const useUserStore = defineStore("user", {
  state: () => ({
    username: "",
    email: "",
    companyId: 0,
    companyName: "",
    permissions: [],
    sessionStartTime: 0,
    clientUser: null,
  }),
  getters: {
    loggedIn: (state) => state.email !== "",

    sessionTimeLeft: (state) =>
      state.sessionStartTime +
      import.meta.env.VITE_SESSION_DURATION * 60000 -
      Date.now(),

    isClientUser: (state) => state.clientUser === 1,
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
            return this.getUserDetails();
          } else {
            throw new Error(
              "Incorrect username and/or password. Please try again."
            );
          }
        })
        .then(() => this.router.push({ name: "status" }))
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },

    logout() {
      this.$reset();
      api.get("logout").then(() => {
        this.router.push({ name: "login" });
      });
    },

    checkPermission(permission) {
      if (this.permissions) {
        return this.permissions.includes(permission);
      } else {
        return false;
      }
    },

    getUserDetails() {
      return api
        .get("user/detail")
        .then((response) => {
          if (response.data.resultCode === -55) {
            throw new Error(response.data.errorMessage);
          }

          const user = response.data.data[0];

          this.username = user.name;
          this.email = user.email;
          this.companyId = user.companyId;
          this.companyName = user.companyName;
          this.clientUser = user.clientUser;
          this.permissions = user.permissions;

          this.sessionStartTime = Date.now();
        })
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },

    toError401(){
      this.router.push({ name: "error-401" });
    }
  },

  persist: true,
});
