import { boot } from "quasar/wrappers";
import axios from "axios";
import { useUserStore } from "stores/user";
import { Notify } from "quasar";

const uat = true;

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({
  baseURL:
    process.env.NODE_ENV === "production"
      ? uat
        ? "https://client.basiluat.paxcenters.com:8989/api/v1"
        : "https://client.basil.paxcenters.com/api/v1"
      : "http://localhost:8081/api/v1",
  withCredentials: true,
});

export default boot(({ app, store }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API

  const user = useUserStore(store);

  api.interceptors.response.use(
    function (response) {
      // Any status code that lie within the range of 2xx cause this function to trigger
      // Do something with response data
      if (
        !user.loggedIn &&
        (response.data.code === 40001 ||
          response.data.errorMessage === "User login session expired.")
      ) {
        user.logout();

        Notify.create({
          type: "negative",
          message: "User login session expired.",
        });

        return response;
      }

      if (response.data.resultCode === 100) {
        Notify.create({
          type: "negative",
          message: response.data.errorMessage,
        });
      }

      if (
        response.data.code &&
        response.data.code !== 20000 &&
        response.data.code !== 40001
      ) {
        Notify.create({
          type: "negative",
          message: response.data.message,
        });
      }

      if (response.data.resultCode && response.data.resultCode === -1) {
        Notify.create({
          type: "negative",
          message: response.data.errorMessage,
        });
      }

      return response;
    },
    function (error) {
      // Any status codes that falls outside the range of 2xx cause this function to trigger
      // Do something with response error
      return Promise.reject(error);
    }
  );
});

export { api, uat };
