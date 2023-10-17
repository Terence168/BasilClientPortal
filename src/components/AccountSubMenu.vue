<template>
    <q-list class="q-pa-lg text-secondary text-subtitle1">
      <div class="q-ml-md q-mb-sm section-title">Account Preferences</div>

      <q-item
        class="list-item"
        active-class="active-link"
        :to="{ name: 'account-set' }"
        style="height: 50px"
      >
        <q-item-section avatar>
          <q-icon name="local_shipping" />
        </q-item-section>
        <q-item-section> Account Settings </q-item-section>
      </q-item>

      <q-item
        class="list-item"
        active-class="active-link"
        :to="{ name: 'customer' }"
        style="height: 50px"
      >
        <q-item-section avatar>
          <q-icon name="local_shipping" />
        </q-item-section>
        <q-item-section> Customer Settings </q-item-section>
      </q-item>
  
      <q-item
        class="list-item"
        active-class="active-link"
        :to="{ name: 'notification' }"
        style="height: 50px"
      >
        <q-item-section avatar>
          <q-icon name="hourglass_empty" />
        </q-item-section>
        <q-item-section> Notification Settings </q-item-section>
      </q-item>
    </q-list>
  </template>


<script>
import { useUserStore } from "src/stores/user";

export default {
  name: "PrivilegeSubMenu",

  methods: {
    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>
  