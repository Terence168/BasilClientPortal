<template>
  <q-layout view="lhh lpR fFf">
    <q-header class="transparent q-ma-sm" style="min-height: 100px">
      <div class="row items-center justify-between">
        <q-tabs
          class="col-auto text-accent text-subtitle1 q-ml-md"
          style="min-height: 80px"
          v-model="tab"
          no-caps
          indicator-color="primary"
          align="left"
        >
          <q-tab name="privilege" label="Privilege" />
        </q-tabs>
        <div class="col-auto text-accent text-subtitle1 q-mr-md">
          <q-avatar color="primary" text-color="white">{{
            userName ? userName[0] : "G"
          }}</q-avatar>
          <span class="q-ml-sm q-mr-md">{{ userName }}</span>
          <q-btn class="q-mr-md" @click="changePassword">Change Password</q-btn>
          <q-btn @click="logout">Logout</q-btn>
        </div>
      </div>
    </q-header>

    <q-drawer model-value side="left" :width="350" :breakpoint="0">
      <!-- drawer content -->

      <q-toolbar class="justify-center text-primary" style="min-height: 100px">
        <q-toolbar-title shrink>
          <q-img
            src="pax_logo_small.png"
            spinner-color="white"
            class="q-mr-md"
            style="width: 120px"
          />
          <span class="text-weight-bold">Basil Client Portal</span>
        </q-toolbar-title>
      </q-toolbar>

      <q-separator />

      <div
        v-if="$uat"
        class="q-mt-md text-h4 text-weight-bold text-center text-red"
      >
        *** UAT Version ***
      </div>

      <component :is="activeSubMenu" />
    </q-drawer>

    <q-page-container>
      <!-- <router-view :key="$route.fullPath" /> -->
      <router-view />

      <BaseModal v-model:show="showModal" title="Change Password" :width="400">
        <q-form ref="modalForm" @submit="onSubmit">
          <q-input
            class="col q-mb-sm"
            outlined
            v-model="newPassword"
            label="New Password"
            :type="isPwd ? 'password' : 'text'"
            hint="New Password must be at least 8 characters long"
            lazy-rules
            dense
            :rules="[
              (val) =>
                (val && val.length > 0) || 'New Password cannot be empty',
              (val) =>
                val.length > 8 ||
                'New Password must contain at least 8 characters',
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="isPwd ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd = !isPwd"
              />
            </template>
          </q-input>

          <q-input
            class="col q-mt-sm q-mb-sm"
            outlined
            v-model="currentPassword"
            label="Current Password"
            :type="isPwd2 ? 'password' : 'text'"
            lazy-rules
            dense
            :rules="[
              (val) =>
                (val && val.length > 0) || 'Current Password cannot be empty',
              (val) =>
                val.length > 8 ||
                'Current Password must contain at least 8 characters',
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="isPwd2 ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd2 = !isPwd2"
              />
            </template>
          </q-input>

          <div class="row justify-center q-mt-md">
            <div class="col-auto">
              <q-btn
                class="q-mr-md"
                type="submit"
                label="Submit"
                color="primary"
                style="min-width: 150px"
              >
                <template v-slot:loading>
                  <q-spinner-facebook />
                </template>
              </q-btn>
            </div>
            <div class="col-auto">
              <q-btn
                label="Cancel"
                color="grey-4"
                text-color="grey-6"
                style="min-width: 150px"
                @click="showModal = false"
              />
            </div>
          </div>
        </q-form>
      </BaseModal>
    </q-page-container>
  </q-layout>
</template>

<script>
import PrivilegeSubMenu from "src/components/PrivilegeSubMenu.vue";
import BaseModal from "src/components/BaseModal.vue";

import { format } from "quasar";
const { capitalize } = format;

import { useUserStore } from "stores/user";

import sha256 from "js-sha256";

const user = useUserStore();

export default {
  name: "MainLayout",

  components: { PrivilegeSubMenu, BaseModal },

  data() {
    return {
      showModal: false,
      tab: "privilege",
      isPwd: true,
      isPwd2: true,
      newPassword: null,
      currentPassword: null,
    };
  },

  computed: {
    activeSubMenu() {
      return capitalize(this.tab) + "SubMenu";
    },
    userName() {
      return user.username || "Guest";
    },
  },

  methods: {
    logout() {
      user.logout();
    },

    changePassword() {
      this.showModal = true;
    },

    onSubmit() {
      const newPassword = sha256(this.newPassword);
      const currentPassword = sha256(this.currentPassword);

      const actionURL = "/basil/privilege/user/password-change";

      const vm = this;
      this.$api
        .post(actionURL, { newPassword, currentPassword })
        .then(function (response) {
          if (response.data.resultCode !== -1) {
            vm.showModal = false;
            vm.$q.notify("Password was successfully changed");
          }
        });
    },
  },
};
</script>
