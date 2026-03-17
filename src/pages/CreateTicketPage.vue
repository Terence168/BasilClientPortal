<template>
  <div class="create-ticket-page">
    <div class="generic-container create-ticket-header">
      <div class="q-px-lg q-py-md filtering-header">
        <div class="text-h6 text-weight-bold">Create Ticket</div>
        <div class="text-caption text-grey-7">
          Fill in ticket details, serials, and optional attachments before submit.
        </div>
      </div>
    </div>

    <div class="q-mt-lg generic-container create-ticket-surface">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg text-body1">
        <div class="row q-mb-md text-weight-medium items-center q-col-gutter-sm">
          <div class="col">Ticket Status: Open</div>
          <div class="col-auto" @click="handleResetTicket">
            <q-btn color="red" unelevated>Clear Data</q-btn>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">Ticket Information</div>

          <div class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Order Type</div>
            <div class="col-12 col-md-9">
            <q-select
              class="field-input-sm"
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

          <div v-if="isReRepair" class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Original RMA#</div>
            <div class="col-12 col-md-9">
              <q-input class="field-input-sm" dense v-model="originalRMA" />
            </div>
          </div>

          <!-- Add ticket  -->
          <div class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Customer Organization</div>
            <div class="col-12 col-md-9" v-if="clientUser">
            <q-input
              :model-value="companyName"
              disable
              class="field-input-sm"
              dense
            />
            </div>
            <div class="col-12 col-md-9" v-if="!clientUser">
            <q-select
              class="field-input-sm"
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

          <div class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Customer Email</div>
            <div class="col-12 col-md-9">
            <q-input
              :model-value="userEmail"
              disable
              class="field-input-sm"
              dense
            />
            </div>
          </div>

          <div class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Ticket Submitter</div>
            <div class="col-12 col-md-9">
            <q-input
              :model-value="userName"
              disable
              class="field-input-sm"
              dense
            />
            </div>
          </div>

          <div class="row items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Encrypt</div>
            <div class="col-12 col-md-9 encrypt-toggle">
              <input
                type="radio"
                v-model="encrypt"
                value="yes"
              />&nbsp;Yes&nbsp;&nbsp;
              <input type="radio" v-model="encrypt" value="no" />&nbsp;No&nbsp;
            </div>
          </div>

          <div class="row items-center field-row q-col-gutter-sm" v-show="isEncrypted">
            <div class="col-12 col-md-3 field-label">Test Key Type</div>
            <div class="col-12 col-md-4">
            <q-select
              ref="testKeyTypeSelect"
              class="field-input-sm"
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
            <div class="col-12 col-md-auto field-label-inline" v-show="keyType != null">
              KCV - KSI
            </div>
            <div class="col-12 col-md-4" v-show="keyType != null">
            <q-select
              class="field-input-sm"
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

        <div class="form-section">
          <div class="section-title">Shipping</div>
          <div class="q-mb-sm field-label">Shipping Address</div>
          <div class="row">
            <div class="col-auto">
            <AddressBlock :address="address" @click="showAddressGrid" />
            </div>
          </div>

          <div class="row q-my-sm items-center field-row q-col-gutter-sm">
            <div class="col-12 col-md-3 field-label">Incoming Tracking Number</div>
            <div class="col-12 col-md-9">
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
            class="row q-mb-sm items-center q-col-gutter-sm tracking-row"
          >
            <div class="col-12 col-md-auto">
              <q-input
                class="tracking-input"
                v-model="trackingNums[index]"
                dense
                outlined
              />
            </div>
            <div class="col-12 col-md-auto">
              <q-btn
                label="Remove"
                outline
                rounded
                color="primary"
                @click="deleteTrackingNum(index)"
              />
            </div>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">Ticket Serial Numbers</div>
          <div class="row items-start q-col-gutter-sm serial-toolbar">
            <!-- Add Serial Number -->
            <div class="col-auto">
              <q-btn
                color="primary"
                @click="this.$refs.editTable.handleClickAddUnit()"
              >
                Add Serial Number
              </q-btn>
            </div>
            <div class="col-auto serial-separator">AND / OR</div>
            <!-- Upload file -->
            <q-form class="col-grow serial-upload-form" @submit="onFileSubmit">
              <div class="row items-start q-col-gutter-sm">
                <div class="col-12 col-md-5">
                  <q-file
                    name="file"
                    class="full-width"
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
                </div>

                <div class="col-12 col-md-auto">
                  <q-btn
                    type="submit"
                    label="Upload"
                    color="primary"
                    class="upload-btn"
                    :loading="fileUploading"
                  >
                    <template v-slot:loading>
                      <q-spinner-facebook />
                    </template>
                  </q-btn>
                </div>

                <div class="col-12 col-md">
                  <q-input
                    clearable
                    label="Serial Number OR Model OR Reported Issue"
                    class="full-width"
                    v-model="inputValue"
                    outlined
                    dense
                  >
                    <template v-slot:append>
                      <q-icon name="search" />
                    </template>
                  </q-input>
                </div>
              </div>
            </q-form>
            <div class="col-auto">
              <q-btn
                color="primary"
                @click="downloadBlankTemplate"
              >
                Blank Template
              </q-btn>
            </div>
            <div class="col-auto">
              <q-btn
                color="primary"
                round
                icon="info"
                size="sm"
                @click="handleClickHelpUnit"
              />
            </div>
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
        </div>

        <div class="form-section">
          <div class="section-title">Attachments and Remark</div>
          <q-file
            v-model="attachments"
            outlined
            dense
            clearable
            multiple
            use-chips
            counter
            label="Upload attachments"
            :accept="attachmentAccept"
            @update:model-value="onAttachmentChange"
          >
            <template v-slot:prepend>
              <q-icon name="attach_file" />
            </template>
            <template v-slot:hint>
              Supported: PDF, DOC/DOCX, XLS/XLSX, CSV, TXT, JPG/JPEG, PNG, GIF,
              MP4, MOV, AVI
            </template>
          </q-file>
          <div class="text-caption text-grey-7 q-mt-xs">
            Size limit: 10 MB for documents/images, 500 MB for videos.
          </div>

          <q-banner
            v-if="attachmentErrors.length > 0"
            dense
            rounded
            class="bg-red-1 text-negative q-mt-sm"
          >
            <div
              v-for="(error, index) in attachmentErrors"
              :key="`attachment-error-${index}`"
            >
              {{ error }}
            </div>
          </q-banner>

          <q-input
            v-model="remark"
            type="textarea"
            autogrow
            outlined
            dense
            maxlength="1000"
            counter
            class="q-mt-md"
            label="Remark"
            placeholder="Please enter remarks (optional)"
          />
        </div>
        <!-- Button for submit ticket -->
        <div class="row justify-center q-mt-lg">
          <q-btn
            class="submit-btn"
            color="primary"
            @click="submitTicket"
            :loading="serialsSubmitting"
            unelevated
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
const DOC_EXTENSIONS = new Set(["pdf", "doc", "docx", "xls", "xlsx", "csv", "txt"]);
const IMAGE_EXTENSIONS = new Set(["jpg", "jpeg", "png", "gif"]);
const VIDEO_EXTENSIONS = new Set(["mp4", "mov", "avi"]);
const ALL_ALLOWED_EXTENSIONS = new Set([
  ...DOC_EXTENSIONS,
  ...IMAGE_EXTENSIONS,
  ...VIDEO_EXTENSIONS,
]);
const MAX_DOC_IMAGE_FILE_SIZE = 10 * 1024 * 1024;
const MAX_VIDEO_FILE_SIZE = 500 * 1024 * 1024;
const ATTACHMENT_ACCEPT = Array.from(ALL_ALLOWED_EXTENSIONS)
  .map((ext) => `.${ext}`)
  .join(",");

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
      attachments: [],
      attachmentErrors: [],
      remark: "",
      attachmentAccept: ATTACHMENT_ACCEPT,
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
    ...mapState(useUserStore, ["clientUser"]),
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
  created() {},
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
    getFileExtension(fileName) {
      if (!fileName || fileName.lastIndexOf(".") < 0) {
        return "";
      }
      return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    },
    validateAttachment(file) {
      const extension = this.getFileExtension(file.name);
      if (!ALL_ALLOWED_EXTENSIONS.has(extension)) {
        return `Unsupported file type: ${file.name}`;
      }

      const isVideo = VIDEO_EXTENSIONS.has(extension);
      const maxSize = isVideo ? MAX_VIDEO_FILE_SIZE : MAX_DOC_IMAGE_FILE_SIZE;
      if (file.size > maxSize) {
        const limitText = isVideo ? "500 MB" : "10 MB";
        return `File exceeds ${limitText}: ${file.name}`;
      }

      return null;
    },
    validateAttachments(files) {
      const normalizedFiles = Array.isArray(files) ? files : files ? [files] : [];
      const validFiles = [];
      const errors = [];

      normalizedFiles.forEach((file) => {
        const error = this.validateAttachment(file);
        if (error) {
          errors.push(error);
        } else {
          validFiles.push(file);
        }
      });

      return { validFiles, errors };
    },
    onAttachmentChange(files) {
      const { validFiles, errors } = this.validateAttachments(files);
      this.attachmentErrors = errors;

      if (errors.length > 0) {
        this.attachments = validFiles;
        errors.forEach((message) => {
          Notify.create({
            type: "negative",
            message,
          });
        });
      }
    },
    handleResetTicket() {
      this.resetTicket();
      this.file = null;
      this.attachments = [];
      this.attachmentErrors = [];
      this.remark = "";
    },
    handleClickHelpUnit() {
      this.showHelpModal = true;
    },
    submitTicketAttachments(ticketId) {
      const hasAttachments = Array.isArray(this.attachments) && this.attachments.length > 0;
      const hasRemark = this.remark && this.remark.trim().length > 0;

      if (!hasAttachments && !hasRemark) {
        return Promise.resolve();
      }

      const formData = new FormData();
      if (hasRemark) {
        formData.append("remark", this.remark.trim());
      }
      if (hasAttachments) {
        this.attachments.forEach((file) => {
          formData.append("files", file);
        });
      }

      const actionURL = `/ticketing/${ticketId}/attachments`;
      return this.$api
        .post(actionURL, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage || "Failed to upload attachments.");
          }
        });
    },
    submitTicket() {
      this.serialsSubmitting = true;

      const { validFiles, errors } = this.validateAttachments(this.attachments);
      this.attachmentErrors = errors;
      if (errors.length > 0) {
        this.attachments = validFiles;
        this.serialsSubmitting = false;
        return;
      }

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
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          const mo_OID = response.data.data.mo_OID;

          return vm.submitTicketAttachments(mo_OID).then(() => {
            Notify.create({
              type: "positive",
              message: `Thank you for submitting a ticket. Your RMA number is: ${mo_OID}`,
            });
          }).catch((uploadError) => {
            Notify.create({
              type: "warning",
              message: `Ticket ${mo_OID} created, but attachment/remark upload failed: ${uploadError.message}`,
            });
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
          vm.handleResetTicket();
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

<style scoped>
.create-ticket-page {
  padding: 16px 24px 28px;
}

.create-ticket-header,
.create-ticket-surface {
  border-radius: 12px;
}

.create-ticket-surface {
  box-shadow: 0 8px 24px rgba(30, 55, 90, 0.06);
}

.form-section {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e8edf3;
}

.form-section:first-of-type {
  margin-top: 0;
  padding-top: 0;
  border-top: none;
}

.section-title {
  margin-bottom: 14px;
  font-size: 1rem;
  font-weight: 700;
  color: #1f2d3d;
}

.field-row {
  margin-bottom: 10px;
}

.field-label {
  font-weight: 600;
  color: #4d5b6a;
}

.field-label-inline {
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #4d5b6a;
}

.field-input-sm {
  width: 100%;
  min-width: 220px;
  max-width: 360px;
}

.encrypt-toggle {
  display: flex;
  align-items: center;
  min-height: 40px;
}

.tracking-row .tracking-input {
  min-width: 320px;
}

.serial-toolbar {
  margin-bottom: 12px;
}

.serial-separator {
  display: flex;
  align-items: center;
  padding-top: 8px;
  color: #5f6b7a;
  font-weight: 600;
}

.serial-upload-form {
  min-width: 0;
}

.upload-btn {
  min-width: 140px;
}

.submit-btn {
  min-width: 220px;
  border-radius: 10px;
}

@media (max-width: 1023px) {
  .create-ticket-page {
    padding: 12px;
  }

  .field-input-sm,
  .tracking-row .tracking-input {
    max-width: none;
    min-width: 0;
  }
}

@media (max-width: 599px) {
  .serial-separator {
    padding-top: 0;
  }

  .upload-btn,
  .submit-btn {
    width: 100%;
    min-width: 0;
  }
}
</style>
