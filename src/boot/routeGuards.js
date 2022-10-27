import { boot } from "quasar/wrappers";
import { useUserStore } from "stores/user";
import { Notify } from "quasar";

// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(({ router, store }) => {
  const user = useUserStore(store);

  router.beforeEach(async (to, from) => {
    if (to.meta.requiresAuth && !user.loggedIn && to.name !== "login") {
      // redirect the user to the login page
      return { name: "login" };
    }

    if (to.name === "login" && user.loggedIn) {
      return { name: "status" };
    }
  });
});
