<template>
  <q-layout view="lhh lpR fFf">
    <q-header class="transparent q-ma-sm" style="min-height: 100px">
      <div class="row items-center justify-between top-header-row">
        <q-tabs
          class="col text-accent text-subtitle1 q-ml-md top-nav-tabs"
          style="min-height: 80px"
          v-model="tab"
          no-caps
          indicator-color="primary"
          align="left"
          outside-arrows
          mobile-arrows
        >
          <q-tab name="RMAStatus" label="Ticket Status" />
          <q-tab
            v-if="checkPermission('ticketing')"
            name="Ticketing"
            label="Ticket Management"
          />
          <q-tab
            v-if="checkPermission('sales')"
            name="Sales"
            label="Sales Management"
          />
          <q-tab
            v-if="checkPermission('privilege')"
            name="privilege"
            label="Privilege"
          />
          <q-tab
            v-if="checkPermission('customer')"
            name="Account"
            label="Account"
          />
        </q-tabs>
        <div class="col-auto text-accent text-subtitle1 q-mr-md top-header-actions">
          <div class="row items-center">
            <q-btn
              class="col-auto self-center q-mr-md contact-rma-btn"
              label="Contact RMA"
              no-caps
              unelevated
              @click="openContactRmaModal"
            />
            <q-avatar class="user-avatar q-mr-md" color="primary" text-color="white">
              {{ userName ? userName[0] : "G" }}
              <q-tooltip anchor="bottom middle" self="top middle">
                <div class="text-body2">{{ userName }}</div>
                <div v-if="companyName !== ''" class="text-caption text-grey-5">
                  {{ companyName }}
                </div>
                <div class="text-caption text-grey-5">{{ userEmail }}</div>
              </q-tooltip>
            </q-avatar>
            <q-btn class="col-auto self-center q-mr-md" @click="changePassword">
              Change Password
            </q-btn>
            <q-btn class="col-auto self-center" @click="logout">Logout</q-btn>
            <q-toggle
              v-model="userDarkMode"
              checked-icon="check"
              color="green"
              unchecked-icon="clear"
              label="Dark Mode"
              @update:model-value="toggleDarkMode"
            ></q-toggle>
          </div>
        </div>
      </div>
    </q-header>

    <q-drawer
      model-value
      side="left"
      :width="260"
      :mini="leftDrawerMini"
      :mini-width="76"
      :breakpoint="0"
      bordered
    >
      <!-- drawer content -->

      <q-toolbar class="justify-between text-primary drawer-toolbar">
        <q-toolbar-title class="drawer-toolbar-title">
          <q-img
            src="~assets/pax_logo_small.png"
            spinner-color="white"
            class="q-mr-md"
            style="width: 72px"
          />
          <span class="text-weight-bold drawer-title-text">Basil Client Portal</span>
        </q-toolbar-title>
        <q-btn
          flat
          round
          dense
          color="primary"
          class="drawer-toggle-btn"
          :icon="leftDrawerMini ? 'chevron_right' : 'chevron_left'"
          @click="toggleDrawerMini"
        >
          <q-tooltip anchor="bottom middle" self="top middle">
            {{ leftDrawerMini ? "Expand sidebar" : "Collapse sidebar" }}
          </q-tooltip>
        </q-btn>
      </q-toolbar>

      <q-separator />

      <div
        v-if="uat"
        class="text-weight-bold text-center text-red drawer-uat-text"
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

      <BaseModal
        v-model:show="showContactRmaModal"
        title="Contact RMA"
        :width="560"
      >
        <q-form ref="contactRmaFormRef" @submit="submitContactRma">
          <q-input
            class="q-mb-sm"
            outlined
            dense
            v-model="contactRmaTicketId"
            label="Ticket ID"
            maxlength="50"
            lazy-rules
            :rules="[]"
          />

          <q-input
            class="q-mb-sm"
            outlined
            dense
            v-model="contactRmaSubject"
            label="Subject"
            maxlength="200"
            lazy-rules
            :rules="[]"
          />

          <q-input
            class="q-mb-sm"
            outlined
            dense
            v-model="contactRmaMessage"
            type="textarea"
            autogrow
            label="Message"
            maxlength="4000"
            lazy-rules
            :rules="[
              (val) => (val && val.trim().length > 0) || 'Message is required',
            ]"
          />

          <q-file
            class="q-mb-sm"
            outlined
            dense
            clearable
            v-model="contactRmaScreenshot"
            label="Screenshot (optional)"
            accept=".png,.jpg,.jpeg,.gif"
            :max-file-size="10485760"
            @rejected="onContactRmaFileRejected"
          >
            <template v-slot:prepend>
              <q-icon name="image" />
            </template>
            <template v-slot:hint>
              Optional image upload. Accepted: PNG/JPG/JPEG/GIF (max 10 MB).
            </template>
          </q-file>

          <div class="q-pa-sm bg-blue-1 text-caption rounded-borders q-mb-md">
            <div><strong>To:</strong> Configured by backend</div>
            <div><strong>Customer:</strong> {{ userName }}</div>
            <div><strong>Organization:</strong> {{ companyName || "N/A" }}</div>
            <div><strong>Email:</strong> {{ userEmail }}</div>
          </div>

          <div class="row justify-center q-mt-md">
            <div class="col-auto">
              <q-btn
                class="q-mr-md"
                type="submit"
                label="Send"
                color="primary"
                :loading="contactRmaSubmitting"
                style="min-width: 140px"
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
                style="min-width: 140px"
                @click="closeContactRmaModal"
              />
            </div>
          </div>
        </q-form>
      </BaseModal>
    </q-page-container>
  </q-layout>
</template>

<script>
import RMAStatusSubMenu from "src/components/RMAStatusSubMenu.vue";
import TicketingSubMenu from "src/components/TicketingSubMenu.vue";
import SalesSubMenu from "src/components/SalesSubMenu.vue";
import PrivilegeSubMenu from "src/components/PrivilegeSubMenu.vue";
import AccountSubMenu from "src/components/AccountSubMenu.vue";
import BaseModal from "src/components/BaseModal.vue";
import { useUserStore } from "stores/user";
import { mapState, mapWritableState } from "pinia";

import { format } from "quasar";
const { capitalize } = format;

import { uat } from "boot/axios";
import { Dark } from "quasar";

import sha256 from "js-sha256";

const user = useUserStore();
const CONTACT_RMA_LOG_STORAGE_KEY = "contact_rma_submission_logs";

// 每个主菜单 Tab 对应的子菜单项（按显示顺序），用于点击 Tab 时跳转到第一个有权限的子页面
const TAB_SUBMENU_CONFIG = {
  RMAStatus: [
    { routeName: "status", permission: "status" },
    { routeName: "shipping", permission: "shipping" },
    { routeName: "quarantine", permission: "quarantine" },
    { routeName: "warranty-check", permission: "warranty_check" },
  ],
  Ticketing: [
    { routeName: "ticketing-queue", permission: "ticketing.queue", requireNonClient: true },
    { routeName: "view-tickets", permission: "ticketing.view" },
    { routeName: "create-ticket", permission: "ticketing.add" },
  ],
  Sales: [
    { routeName: "sales-order", permission: "sales.order" },
  ],
  privilege: [
    { routeName: "role-type", permission: "privilege.role-type.view" },
    { routeName: "role", permission: "privilege.role.view" },
    { routeName: "user", permission: "privilege.user.view" },
  ],
  Account: [
    { routeName: "account-set" },
    { routeName: "customer" },
    { routeName: "notification" },
  ],
};

// 路由名称 → Tab 名称的反向映射，用于根据当前路由同步 Tab 状态
const ROUTE_TAB_MAP = {};
Object.entries(TAB_SUBMENU_CONFIG).forEach(([tabName, items]) => {
  items.forEach((item) => {
    ROUTE_TAB_MAP[item.routeName] = tabName;
  });
});
ROUTE_TAB_MAP["rma"] = "RMAStatus";
ROUTE_TAB_MAP["ticketing"] = "Ticketing";
ROUTE_TAB_MAP["edit-ticket"] = "Ticketing";
ROUTE_TAB_MAP["privilege"] = "privilege";
ROUTE_TAB_MAP["account"] = "Account";

export default {
  name: "MainLayout",

  components: {
    RMAStatusSubMenu,
    TicketingSubMenu,
    SalesSubMenu,
    PrivilegeSubMenu,
    AccountSubMenu,
    BaseModal,
  },

  data() {
    return {
      showModal: false,
      tab: "RMAStatus",
      isPwd: true,
      isPwd2: true,
      newPassword: null,
      currentPassword: null,
      showContactRmaModal: false,
      contactRmaTicketId: "",
      contactRmaSubject: "",
      contactRmaMessage: "",
      contactRmaScreenshot: null,
      contactRmaSubmitting: false,
      uat,
      leftDrawerMini: false,
      // darkMode: "auto",
    };
  },

  watch: {
    tab(newTab, oldTab) {
      if (newTab === oldTab) return;
      if (this.isCurrentRouteUnderTab(newTab)) return;
      this.navigateToFirstPermittedRoute(newTab);
    },
    "$route.name": {
      immediate: true,
      handler(routeName) {
        if (!routeName) return;
        const targetTab = ROUTE_TAB_MAP[routeName];
        if (targetTab && this.tab !== targetTab) {
          this.tab = targetTab;
        }
      },
    },
  },

  computed: {
    ...mapWritableState(useUserStore, ["userDarkMode"]),
    activeSubMenu() {
      return capitalize(this.tab) + "SubMenu";
    },
    userName() {
      return user.username || "Guest";
    },
    companyName() {
      return user.companyName || "";
    },
    userEmail() {
      return user.email || "N/A";
    },
    currentTicketId() {
      return this.$route?.params?.ticketId || this.$route?.query?.ticketId || null;
    },
  },

  methods: {
    logout() {
      user.logout();
    },

    changePassword() {
      this.showModal = true;
    },
    openContactRmaModal() {
      this.contactRmaTicketId = this.currentTicketId ? String(this.currentTicketId) : "";
      this.showContactRmaModal = true;
    },
    closeContactRmaModal() {
      this.showContactRmaModal = false;
      this.resetContactRmaForm();
    },
    resetContactRmaForm() {
      this.contactRmaTicketId = "";
      this.contactRmaSubject = "";
      this.contactRmaMessage = "";
      this.contactRmaScreenshot = null;
    },
    onContactRmaFileRejected() {
      this.$q.notify({
        type: "negative",
        message: "Invalid screenshot file. Please use PNG/JPG/JPEG/GIF under 10 MB.",
      });
    },
    logContactRmaSubmission(logEntry) {
      try {
        const currentLogs = JSON.parse(
          window.localStorage.getItem(CONTACT_RMA_LOG_STORAGE_KEY) || "[]"
        );
        currentLogs.unshift(logEntry);
        window.localStorage.setItem(
          CONTACT_RMA_LOG_STORAGE_KEY,
          JSON.stringify(currentLogs.slice(0, 200))
        );
      } catch (error) {
        console.error("Failed to persist Contact RMA log:", error);
      }
      console.info("[Contact RMA] submission:", logEntry);
    },
    async submitContactRma() {
      const isFormValid = await this.$refs.contactRmaFormRef.validate();
      if (!isFormValid) {
        return;
      }

      this.contactRmaSubmitting = true;

      const timestamp = new Date().toISOString();
      const ticketId = this.contactRmaTicketId ? this.contactRmaTicketId.trim() : "";
      const submissionLog = {
        timestamp,
        user: this.userName,
        organization: this.companyName || "",
        email: this.userEmail,
        ticketId: ticketId || null,
        subject: this.contactRmaSubject,
      };

      const formData = new FormData();
      formData.append("subject", this.contactRmaSubject);
      formData.append("message", this.contactRmaMessage);
      formData.append("ticketId", ticketId || "");
      if (this.contactRmaScreenshot) {
        formData.append("screenshot", this.contactRmaScreenshot);
      }

      try {
        const response = await this.$api.post("/ticketing/contact-rma", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        if (response?.data?.resultCode !== 0) {
          throw new Error(response?.data?.errorMessage || "Failed to send Contact RMA email.");
        }

        this.logContactRmaSubmission({
          ...submissionLog,
          emailMessageId: response?.data?.data?.emailMessageId || null,
          to: response?.data?.data?.to || null,
        });
        this.$q.notify({
          type: "positive",
          message: `Message sent to ${response?.data?.data?.to || "support mailbox"}.`,
        });
        this.closeContactRmaModal();
      } catch (error) {
        this.logContactRmaSubmission({
          ...submissionLog,
          status: "failed",
          error:
            error?.response?.data?.errorMessage ||
            error?.message ||
            "Failed to send Contact RMA email.",
        });
        this.$q.notify({
          type: "negative",
          message:
            error?.response?.data?.errorMessage ||
            error?.message ||
            "Failed to send Contact RMA email.",
        });
      } finally {
        this.contactRmaSubmitting = false;
      }
    },

    onSubmit() {
      const newPassword = sha256(this.newPassword);
      const currentPassword = sha256(this.currentPassword);

      const actionURL = "privilege/user/password-change";

      const vm = this;
      this.$api
        .post(actionURL, { newPassword, currentPassword })
        .then(function (response) {
          if (response.data.resultCode !== -1) {
            vm.showModal = false;
            vm.$q.notify("Password was successfully changed");
          } else {
            vm.$q.notify({
              type: "negative",
              message: response.data.errorMessage || "Password change failed",
            });
          }
        })
        .catch(function (error) {
          vm.$q.notify({
            type: "negative",
            message:
              error?.response?.data?.message ||
              error?.response?.data?.errorMessage ||
              "Password change failed",
          });
        });
    },
    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
    isCurrentRouteUnderTab(tabName) {
      const currentRouteName = this.$route?.name;
      if (!currentRouteName) return false;
      return ROUTE_TAB_MAP[currentRouteName] === tabName;
    },
    navigateToFirstPermittedRoute(tabName) {
      const items = TAB_SUBMENU_CONFIG[tabName];
      if (!items || items.length === 0) return;
      const userStore = useUserStore();
      const firstPermitted = items.find((item) => {
        if (item.requireNonClient && userStore.isClientUser) return false;
        if (item.permission && !userStore.checkPermission(item.permission)) return false;
        return true;
      });
      if (firstPermitted) {
        this.$router.push({ name: firstPermitted.routeName });
      }
    },
    toggleDarkMode(value, evt) {
      Dark.set(value);
      this.userDarkMode = value;
    },
    toggleDrawerMini() {
      this.leftDrawerMini = !this.leftDrawerMini;
    },
  },
};
</script>

<style scoped>
.contact-rma-btn {
  background: #4169e1;
  color: #ffffff;
  border: 1px solid #4169e1;
}

.contact-rma-btn:hover {
  background: #2f56cb;
  border-color: #2f56cb;
}

.top-header-row {
  flex-wrap: nowrap;
  column-gap: 20px;
}

.drawer-toggle-btn {
  background: #e6f2ff;
  border: 1px solid #90caf9;
}

.drawer-toggle-btn :deep(.q-icon) {
  color: #1976d2;
}

.drawer-toolbar {
  min-height: 72px;
  padding: 0 10px;
}

.drawer-toolbar-title {
  display: flex;
  align-items: center;
  min-width: 0;
}

.drawer-title-text {
  font-size: 14px;
  white-space: nowrap;
}

.drawer-uat-text {
  font-size: 18px;
  line-height: 1.1;
  margin: 6px 0 4px;
}

:deep(.q-drawer .q-item) {
  min-height: 42px !important;
}

:deep(.q-drawer .q-item__section--main) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 16px;
}

:deep(.q-drawer .q-item__section--avatar) {
  min-width: 34px;
}

:deep(.q-drawer .q-list) {
  padding-top: 10px !important;
  padding-bottom: 10px !important;
}

:deep(.q-drawer--mini .section-title) {
  display: none;
}

:deep(.q-drawer--mini .q-toolbar-title span) {
  display: none;
}

:deep(.q-drawer--mini .drawer-uat-text) {
  display: none;
}

:deep(.q-drawer--mini .list-item) {
  width: 56px;
  min-height: 56px !important;
  margin: 8px auto;
  padding-left: 0 !important;
  padding-right: 0 !important;
  border-radius: 12px;
  justify-content: center;
}

:deep(.q-drawer--mini .list-item .q-item__section--avatar) {
  min-width: auto;
  justify-content: center;
}

:deep(.q-drawer--mini .list-item .q-item__section--main) {
  display: none;
}

.top-header-actions {
  white-space: nowrap;
}

.top-header-actions .row {
  column-gap: 10px;
}

.top-header-actions .q-btn {
  min-height: 34px;
  padding: 0 12px;
  font-size: 12px;
}

.top-header-actions .q-toggle {
  margin-left: 2px;
  font-size: 13px;
}

.user-avatar {
  cursor: pointer;
}

.top-nav-tabs :deep(.q-tabs__content) {
  flex-wrap: nowrap;
  gap: 6px;
}

.top-nav-tabs :deep(.q-tab),
.top-nav-tabs :deep(.q-tab__label) {
  white-space: nowrap;
}

.top-nav-tabs :deep(.q-tab) {
  min-height: 52px;
  padding: 0 10px;
}

.top-nav-tabs :deep(.q-tab__label) {
  font-size: 15px;
}

.body--dark .q-tabs .q-tab__label {
  color: white;
}

.body--dark .q-btn {
  color: white;
  border: 0.5px solid white;
}

.body--dark div {
  color: white;
}

.body--dark .q-btn.contact-rma-btn {
  color: #ffffff;
  border-color: #4169e1;
  background: #4169e1;
}
</style>
