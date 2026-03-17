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
        <div class="col-auto text-accent text-subtitle1 q-mr-md">
          <div class="row items-center">
            <q-btn
              class="col-auto self-center q-mr-md contact-rma-btn"
              label="Contact RMA"
              no-caps
              unelevated
              @click="openContactRmaModal"
            />
            <q-avatar color="primary" text-color="white">{{
              userName ? userName[0] : "G"
            }}</q-avatar>
            <div class="column justify-center q-ml-sm q-mr-md">
              <div class="col-auto">{{ userName }}</div>
              <div
                v-if="companyName !== ''"
                class="col-auto text-subtitle2 text-grey-6"
              >
                {{ companyName }}
              </div>
            </div>
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

    <q-drawer model-value side="left" :width="350" :breakpoint="0">
      <!-- drawer content -->

      <q-toolbar class="justify-center text-primary" style="min-height: 100px">
        <q-toolbar-title shrink>
          <q-img
            src="~assets/pax_logo_small.png"
            spinner-color="white"
            class="q-mr-md"
            style="width: 120px"
          />
          <span class="text-weight-bold">Basil Client Portal</span>
        </q-toolbar-title>
      </q-toolbar>

      <q-separator />

      <div
        v-if="uat"
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
            :rules="[
              (val) => (val && val.trim().length > 0) || 'Ticket ID is required',
            ]"
          />

          <q-input
            class="q-mb-sm"
            outlined
            dense
            v-model="contactRmaSubject"
            label="Subject"
            maxlength="200"
            lazy-rules
            :rules="[
              (val) => (val && val.trim().length > 0) || 'Subject is required',
            ]"
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
            <div><strong>To:</strong> RMAsupport@pax.us</div>
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
const CONTACT_RMA_EMAIL = "RMAsupport@pax.us";
const CONTACT_RMA_LOG_STORAGE_KEY = "contact_rma_submission_logs";

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
      // darkMode: "auto",
    };
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
      formData.append("to", CONTACT_RMA_EMAIL);
      formData.append("subject", this.contactRmaSubject);
      formData.append("message", this.contactRmaMessage);
      formData.append("timestamp", timestamp);
      formData.append("customerName", this.userName);
      formData.append("customerOrganization", this.companyName || "");
      formData.append("customerEmail", this.userEmail);
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

        this.logContactRmaSubmission(submissionLog);
        this.$q.notify({
          type: "positive",
          message: `Message sent to ${CONTACT_RMA_EMAIL}`,
        });
        this.closeContactRmaModal();
      } catch (error) {
        const encodedSubject = encodeURIComponent(
          `${this.contactRmaSubject}${ticketId ? ` | Ticket ${ticketId}` : ""}`
        );
        const encodedBody = encodeURIComponent(
          `Message:\n${this.contactRmaMessage}\n\nTicket ID: ${
            ticketId || "N/A"
          }\nCustomer Name: ${this.userName}\nOrganization: ${
            this.companyName || "N/A"
          }\nEmail: ${this.userEmail}\nTimestamp: ${timestamp}\nScreenshot: ${
            this.contactRmaScreenshot
              ? `${this.contactRmaScreenshot.name} (please attach manually if email client opens)`
              : "N/A"
          }`
        );
        window.location.href = `mailto:${CONTACT_RMA_EMAIL}?subject=${encodedSubject}&body=${encodedBody}`;

        this.logContactRmaSubmission({
          ...submissionLog,
          fallback: "mailto",
        });
        this.$q.notify({
          type: "warning",
          message:
            "Backend email endpoint is unavailable. Your email client has been opened as fallback.",
        });
        this.closeContactRmaModal();
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
    toggleDarkMode(value, evt) {
      Dark.set(value);
      this.userDarkMode = value;
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
