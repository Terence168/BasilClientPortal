<template>
  <div
    class="fullscreen bg-blue text-white text-center q-pa-md flex flex-center"
  >
    <div>
      <div style="font-size: 30vh">403</div>

      <div class="text-h2" style="opacity: 0.4">
        You do not have a permission to access this page...
      </div>

      <q-btn
        class="q-mt-xl"
        color="white"
        text-color="blue"
        unelevated
        to="/rma/status"
        label="Go Home"
        no-caps
      />

      <q-btn
        @click="logout"
        class="q-ml-sm q-mt-xl"
        color="white"
        text-color="blue"
        unelevated
        label="Logout"
        no-caps
      />
    </div>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";

const user = useUserStore();

export default {
  name: "error-403",

  methods: {
    logout() {
      user.logout();
    },
  },
};
</script>
