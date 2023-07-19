<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Create Ticket
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg text-body1">
        <div class="row q-mb-md text-weight-medium">
          <div class="col">Ticket Status: Open</div>
          <div class="col-auto" @click="resetTicket">
            <q-btn color="red">Clear Data</q-btn>
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Order Type:&nbsp;</div>
          <div class="col-auto">
            <q-select
              style="min-width: 200px"
              label="Please select"
              v-model="orderType"
              :options="orderTypeOpt"
              @filter="populateOrderTypeOpt"
              dense
              emit-value
              map-options
            >
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey">
                    No results
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
        </div>

        <div v-if="isReRepair" class="row items-center">
          <div class="col-auto q-mr-sm">Original RMA#:&nbsp;</div>
          <div class="col-auto">
            <q-input style="min-width: 200px" dense />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Customer Organization:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="companyName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Customer Email:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="userEmail"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Ticket Submitter:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="userName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row q-my-sm items-center">
          <div class="col-auto q-mr-sm">Incoming Tracking Number:&nbsp;</div>
          <div class="col">
            <q-btn
              label="Add"
              outline
              rounded
              color="primary"
              @click="addTrackingNum"
            />
          </div>
        </div>
        <div
          v-for="(trackingNum, index) in trackingNums"
          :key="index"
          class="row q-mb-sm items-center"
        >
          <q-input
            class="q-mr-sm"
            v-model="trackingNums[index]"
            style="min-width: 300px"
            dense
            outlined
          />
          <q-btn
            label="Remove"
            outline
            rounded
            color="primary"
            @click="deleteTrackingNum(index)"
          />
        </div>

        <div class="q-py-md text-subtitle1 text-weight-bold">
          Ticket Serial Numbers
        </div>

        <div class="row items-start">
          <q-btn class="col-auto" color="primary" @click="showModal = true">
            Add Serial Number
          </q-btn>
          <div style="margin-top: 6px" class="q-mx-sm">AND / OR</div>
          <q-form class="col-auto" @submit="onFileSubmit">
            <div class="row items-start">
              <q-file
                style="min-width: 250px"
                name="file"
                class="col q-mr-sm"
                clearable
                bottom-slots
                outlined
                v-model="file"
                label="Upload Excel File"
                dense
                counter
                :disable="fileUploading"
              >
                <template v-slot:prepend>
                  <q-icon name="attach_file" />
                </template>

                <template v-slot:hint> Allowed file format: .xlsx </template>
              </q-file>

              <q-btn
                class="col"
                type="submit"
                label="Upload"
                color="primary"
                style="min-width: 150px"
                :loading="fileUploading"
              >
                <template v-slot:loading>
                  <q-spinner-facebook />
                </template>
              </q-btn>

              &nbsp;
              <q-input
                clearable
                class="q-mr-sm"
                label="Serial Number OR Model OR Reported Issue"
                style="min-width: 380px"
                v-model="inputValue"
            />&nbsp;

            </div>
          </q-form>
        </div>

        <!-- add serials here -->
        <TicketSerialsGrid />
      </div>
    </div>

    <BaseModal
      v-model:show="showModal"
      title="Add Device to Ticket"
      :width="500"
    >
      <q-form
        ref="modalForm"
        @submit.prevent="
          addSerial(serialNumber, customerReportedIssue, customerTerminalID)
        "
      >
        <q-input
          class="col q-mb-sm"
          outlined
          v-model="serialNumber"
          label="Serial Number"
          lazy-rules
          dense
          :rules="[
            (val) => (val && val.length > 0) || 'Serial Number cannot be empty',
          ]"
        />

        <q-input
          class="col q-mt-sm q-mb-sm"
          outlined
          autogrow
          v-model="customerReportedIssue"
          label="Customer Reported Issue"
          lazy-rules
          dense
          :rules="[
            (val) =>
              (val && val.length > 0) ||
              'Customer Reported Issue cannot be empty',
          ]"
        />

        <q-input
          class="col q-mt-sm q-mb-sm"
          outlined
          v-model="customerTerminalID"
          label="Customer Terminal ID"
          dense
        />

        <div class="row justify-center q-mt-md">
          <div class="col-auto">
            <q-btn
              class="q-mr-md"
              type="submit"
              label="Add Device"
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
  </div>
</template>

<script>
import { useUserStore } from "stores/user";
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState } from "pinia";

import BaseModal from "src/components/BaseModal.vue";
import TicketSerialsGrid from "src/components/TicketSerialsGrid.vue";

const user = useUserStore();

export default {
  components: { BaseModal, TicketSerialsGrid },

  data() {
    return {
      file: null,
      showModal: false,
      fileUploading: false,

      serialNumber: null,
      customerReportedIssue: null,
      customerTerminalID: null,
    };
  },

  computed: {
    ...mapWritableState(useCreateTicketStore, ["orderType", "trackingNums","inputValue"]),

    ...mapState(useCreateTicketStore, ["orderTypeOpt"]),

    isReRepair() {
      return this.orderType === 4;
    },

    userName() {
      return user.username || "Guest";
    },

    userEmail() {
      return user.email || "N/A";
    },

    companyName() {
      return user.companyName || "";
    },
  },

  created() {},

  methods: {
    ...mapActions(useCreateTicketStore, [
      "addTrackingNum",
      "deleteTrackingNum",
      "resetTicket",
      "populateOrderTypeOpt",
      "addSerial",
    ]),

    onFileSubmit(e) {
      if (!this.file) {
        return;
      }
      this.fileUploading = true;
      const actionURL = "/ticketing/batchSerialNumberQuery";

      const formData = new FormData(e.target);
      formData.append("fileName", this.file ? this.file.name : "");

      const vm = this;
      this.$api
        .post(actionURL, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then(function (response) {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
        })
        .catch((e) => {
          this.$q.notify({
            type: "negative",
            message: e.message,
          });
        })
        .finally(() => {
          this.fileUploading = false;
        });
    },
  },
};
</script>

<style></style>
