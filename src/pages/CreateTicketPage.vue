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
            <q-input style="min-width: 200px" dense v-model="originalRMA" />
          </div>
        </div>

        <!-- Add ticket  -->
        <div class="row items-center">
          <div class="col-auto q-mr-sm">Customer Organization:&nbsp;</div>
          <div class="col-auto" v-if="clientUser">
            <q-input
              :model-value="companyName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
          <div class="col-auto" v-if="!clientUser">
            <q-select
              style="min-width: 200px"
              label="Please select"
              v-model="custType"
              :options="custTypeOpt"
              @filter="populateCustTypeOpt"
              dense
              emit-value
              map-options
              use-input
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
        <div class="row items-center">
          Encrypt:&nbsp;
          <input
            type="radio"
            v-model="encrypt"
            value="yes"
          />&nbsp;Yes&nbsp;&nbsp;
          <input type="radio" v-model="encrypt" value="no" />&nbsp;No&nbsp;
        </div>
        <div class="row items-center" v-show="isEncrypted">
          <div class="col-auto q-mr-sm">Test Key Type:&nbsp;</div>
          <div class="col-auto">
            <q-select
              ref="testKeyTypeSelect"
              style="min-width: 200px"
              label="Please select"
              v-model="keyType"
              :options="keyTypeOpt"
              @filter="populateKeyTypeOpt"
              @input-value="populateKcvOpt"
              @update:model-value="populateKcvksiOpt"
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
          <div class="col-auto q-mx-sm" v-show="keyType != null">
            KCV - KSI:&nbsp;
          </div>
          <div class="col-auto q-ml-sm" v-show="keyType != null">
            <q-select
              style="min-width: 200px"
              v-model="kcvksi"
              :options="kcvksiOpt"
              label="Please select"
              dense
              clearable
              options-selected-class="text-deep-orange"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps">
                  <q-item-section>
                    <q-item-label>{{ scope.opt.label }}</q-item-label>
                    <q-item-label caption>{{ scope.opt.comment }}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
          <!-- <div class="col-auto q-mr-sm q-ml-sm" v-show="keyType != null">KCV:&nbsp;</div>
          <div class="col-auto" v-show="keyType != null">
            <q-select
              ref="testKeyTypeSelect"
              style="min-width: 200px"
              label="Please select"
              v-model="kcv"
              :options="kcvOpt"
              @input-value="populateKsiOpt"
              @update:model-value="populateKsiOpt"
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
          </div> -->
          <!-- <div class="col-auto q-mr-sm q-ml-sm" v-show="keyType != null && kcv != null">KSI:&nbsp;</div>
          <div class="col-auto"  v-show="keyType != null && kcv != null">
            <q-select
              ref="testKeyTypeSelect"
              style="min-width: 200px"
              label="Please select"
              v-model="ksi"
              :options="ksiOpt"
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
          </div> -->
        </div>
        <div class="q-my-sm">Shipping Address:</div>
        <div class="row">
          <div class="col-auto">
            <AddressBlock :address="address" @click="showAddressGrid" />
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
          <!-- Add Serial Number -->
          <q-btn
            class="col-auto"
            color="primary"
            @click="this.$refs.editTable.handleClickAddUnit()"
          >
            Add Serial Number
          </q-btn>
          <div style="margin-top: 6px" class="q-mx-sm">AND / OR</div>
          <!-- Upload file -->
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

              <q-input
                clearable
                class="q-ml-sm"
                label="Serial Number OR Model OR Reported Issue"
                style="min-width: 350px"
                v-model="inputValue"
                outlined
                dense
              >
                <template v-slot:append>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
          </q-form>
          <q-btn
            class="col-auto q-ml-sm"
            color="primary"
            @click="downloadBlankTemplate"
          >
            Blank Template
          </q-btn>
          <q-btn
            class="q-ml-sm"
            color="primary"
            round
            icon="info"
            size="sm"
            @click="handleClickHelpUnit"
          />
        </div>

        <TicketEditTable
          ref="editTable"
          :isFromMaster="false"
          :orderType="orderType"
          :rows="getSerials"
          :containsXrefMaterials="false"
          :inputValue="inputValue"
          :encrypt="encrypt"
          :showRemoveUnit="true"
          :showUpdateUnit="true"
          :showViewUnit="false"
          @add-sn="handleAddSN"
          @update-sn="handleUpdateSN"
          @remove-sn="handleRemoveSN"
        />
        <!-- Button for submit ticket -->
        <div class="row justify-center">
          <q-btn
            class="col-auto"
            color="primary"
            @click="submitTicket"
            style="min-width: 200px"
            :loading="serialsSubmitting"
          >
            Submit
          </q-btn>
        </div>
        <!-- Button for test email -->
        <!-- <div class="row justify-center">
          <q-btn
            class="col-auto"
            color="primary"
            @click="testEmailSerivce"
            style="min-width: 200px"
          >
            Test Email
          </q-btn>
        </div> -->
      </div>
    </div>
    <BaseModal
      :show="showAddressModal"
      title="Select Shipping Address"
      :width="972"
      @update:show="showAddressModal = false"
    >
      <AddressGrid
        :customer="custType"
        @selectShippingAddress="selectShippingAddress"
      />
    </BaseModal>

    <BaseModal
      :show="showHelpModal"
      title="Mass Upoad Template"
      :width="500"
      @update:show="showHelpModal = false"
    >
      <div class="row justify-center q-mt-md">
        <p class="text-center">
          <strong>
            Please populate as many fields as applicable.<br /><br />
            Note that not all devices have a 2nd device SNs and it may not be
            applicable to all devices. If the Customer ID field does not apply
            to your company, please leave it blank.<br /><br />
            Here is a sample file if you'd like an example:
          </strong>
        </p>
        <q-btn color="primary" @click="downloadSampleFile"
          >Download Sample</q-btn
        >
      </div>
    </BaseModal>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState } from "pinia";
import BaseModal from "src/components/BaseModal.vue";
import TicketEditTable from "src/components/TicketEditTable.vue";

import AddressBlock from "src/components/AddressBlock.vue";
import AddressGrid from "src/components/AddressGrid.vue";
import { Notify } from "quasar";
import { batchSerialNumberQuery } from "../utils/ticketUtils.js";

const user = useUserStore();

export default {
  components: {
    BaseModal,
    TicketEditTable,
    AddressBlock,
    AddressGrid,
    TicketEditTable,
  },
  data() {
    return {
      file: null,
      showModal: false,
      showAddressModal: false,
      fileUploading: false,
      updateOrAddLoading: false, //to control the update/add button's loading
      serialsSubmitting: false,
      showHelpModal: false,
    };
  },

  computed: {
    ...mapWritableState(useCreateTicketStore, [
      "orderType",
      "keyType",
      "kcv",
      "ksi",
      "trackingNums",
      "inputValue",
      "originalRMA",
      "address",
      "encrypt",
      "custType",
      "kcvksi",
    ]),
    ...mapState(useCreateTicketStore, [
      "orderTypeOpt",
      "keyTypeOpt",
      "getSerials",
      "getAllSerials",
      "getTrackingNums",
      "custTypeOpt",
      "kcvOpt",
      "ksiOpt",
      "kcvksiOpt",
    ]),
    ...mapState(useUserStore, ["clientUser", "lastlogin", "email"]),
    isEncrypted() {
      return this.encrypt != null && this.encrypt === "yes";
    },
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
  created() {
    if (this.email != this.lastlogin) {
      //If there's a mismatch, clear the existing createTicket object
      this.resetTicket();
    }
  },
  methods: {
    ...mapActions(useCreateTicketStore, [
      "addTrackingNum",
      "deleteTrackingNum",
      "resetTicket",
      "populateOrderTypeOpt",
      "populateKeyTypeOpt",
      "populateCustTypeOpt",
      "populateKcvOpt",
      "populateKsiOpt",
      "populateKcvksiOpt",
      "addSerial",
      "addSerialList",
      "updateSerial",
      "removeSerial",
    ]),
    downloadBlankTemplate() {
      window.open("/blankFile.xlsx", "_self");
    },
    downloadSampleFile() {
      window.open("/sampleFile.xlsx", "_self");
    },
    onFileSubmit(e) {
      if (!this.file) {
        return;
      }
      this.fileUploading = true;

      const formData = new FormData(e.target);
      formData.append("fileName", this.file ? this.file.name : "");

      batchSerialNumberQuery(formData)
        .then((serials) => {
          this.file = null;
          this.addSerialList(serials);
        })
        .finally(() => {
          this.fileUploading = false;
        });
    },
    showAddressGrid() {
      this.showAddressModal = true;
    },
    handleClickHelpUnit() {
      this.showHelpModal = true;
    },
    submitTicket() {
      this.serialsSubmitting = true;
      const serials = this.getSerials;
      //no serial
      if (serials === undefined || serials.length == 0) {
        Notify.create({
          type: "negative",
          message: "Please Add at Least One SN before Submitting.",
        });
        this.serialsSubmitting = false;
        return;
      }

      //If user didn't choose order type, don't allow user to submit the ticket
      if (this.orderType === null) {
        Notify.create({
          type: "negative",
          message: "Please Select Order Type before Submitting.",
        });
        this.serialsSubmitting = false;
        return;
      }
      //invalid serials
      for (const serial of serials) {
        if (serial.valid === false) {
          Notify.create({
            type: "negative",
            message: "Please Delete Invalid SN before Submiting",
          });
          this.serialsSubmitting = false;
          return;
        }
      }
      //no address
      if (this.address === null) {
        Notify.create({
          type: "negative",
          message: "Please Select Shipping Address before Submitting",
        });
        this.serialsSubmitting = false;
        return;
      }
      //no test key select when user select encrypted
      if (this.isEncrypted && (this.kcvksi === null || this.keyType === null)) {
        Notify.create({
          type: "negative",
          message: "Please Select an Unique Key for Encryption",
        });
        this.serialsSubmitting = false;
        return;
      }

      const trackingNumbers = this.getTrackingNums.filter(
        (t) => t != "" && t.length > 0
      );
      const actionURL = "/ticketing/submitTicket";

      const sNsInsertionObjects = serials.map((serial) => {
        const snObject = {};
        snObject.customerReportedIssueExt = serial.customerReportedIssueExt;
        snObject.customerTerminalID = serial.customerTerminalID;
        snObject.serialNumber = serial.serialNumber;
        snObject.customerRMA = serial.customerRMA;
        snObject.xmOID = serial.xmOID;
        snObject.msnOID = serial.msnOID;
        snObject.cosmetic =
          serial.cosmetic === null || serial.cosmetic === false ? 891 : 890;
        return snObject;
      });

      const payload = {
        encrypt: this.encrypt,
        orderType: this.orderType,
        testKeyType: this.isEncrypted ? this.ksi : null,
        trackingNumbers,
        originalRMA: this.originalRMA,
        serials: sNsInsertionObjects,
        xaOID: this.address.xaOid,
      };

      const vm = this;

      this.$api
        .post(actionURL, payload, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then(function (response) {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          const mo_OID = response.data.data.mo_OID;
          Notify.create({
            type: "positive",
            message: `Thank you for submitting a ticket. Your RMA number is: ${mo_OID}`,
          });
        })
        .catch((e) => {
          this.$q.notify({
            type: "negative",
            message: e.message,
          });
        })
        .finally(() => {
          this.serialsSubmitting = false;
          vm.resetTicket();
        });
    },
    handleAddSN({ serial }) {
      this.addSerial(serial);
    },
    handleUpdateSN({ oldSN, serial }) {
      this.updateSerial(oldSN, serial);
    },
    handleRemoveSN({ sn }) {
      this.removeSerial(sn);
    },
    selectShippingAddress(address) {
      this.address = address;
      this.showAddressModal = false;
    },
    testEmailSerivce() {
      const actionURL = "/aws/email/test";
      const vm = this;
      this.$api
        .get(actionURL)
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
        });
    },
  },
};
</script>

<style></style>
