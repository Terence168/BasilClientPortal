<template>
  <div class="edit-ticket-page q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header edit-ticket-title-bar">
        Edit Ticket {{ ticketId }}
        <q-btn
          class="email-detail-btn"
          color="primary"
          flat
          icon="mail"
          label="View email details"
          :loading="emailPreviewLoading"
          @click="openEmailPreview"
        />
      </div>
    </div>
    <div class="q-mt-lg generic-container edit-ticket-surface">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg text-body1 edit-ticket-body">
        <div class="row q-mb-md text-weight-medium edit-ticket-status-bar">
          <div class="col">Ticket Status: Open</div>
          <div class="col-auto" @click="resetTicket">
            <q-btn color="red">Refresh Data</q-btn>
          </div>
        </div>

        <div class="row items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Order Dept:&nbsp;</div>
          <div class="col-auto">
            <q-select
              ref="orderTypeSelect"
              class="field-input-md"
              label="Please select"
              v-model="orderDept"
              :options="orderDeptOpt"
              :disable="true"
              @filter="populateOrderDeptOpt"
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

        <div v-if="isReRepair" class="row items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Original RMA#:&nbsp;</div>
          <div class="col-auto">
            <q-input
              class="field-input-md"
              dense
              v-model="ticketInfo.originalRMA"
            />
          </div>
        </div>
        <div class="row items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Ticket Submitter:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="ticketInfo.submitterName"
              disable
              class="field-input-md"
              dense
            />
          </div>
        </div>
        <div class="row items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Submitter Organization:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="ticketInfo.submitterOrg"
              disable
              class="field-input-md"
              dense
            />
          </div>
        </div>
        <div class="row items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Submitter Email:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="ticketInfo.submitterEmail"
              disable
              class="field-input-md"
              dense
            />
          </div>
        </div>
        <div class="row items-center ticket-info-row">
          Encrypt:&nbsp;
          <input
            type="radio"
            v-model="ticketInfo.encrypt"
            value="yes"
            :disabled="forceEncryptNo"
          />&nbsp;Yes&nbsp;&nbsp;
          <input
            type="radio"
            v-model="ticketInfo.encrypt"
            value="no"
            disabled
          />&nbsp;No&nbsp;
        </div>
        <div class="row items-center q-mt-sm ticket-info-row" v-show="showKeyCategorySelection">
          <div class="col-auto q-mr-sm">Key:&nbsp;</div>
          <div class="col">
            <q-option-group
              v-if="allowKeyCategoryChoice"
              v-model="keyType"
              :options="availableKeyTypeOpt"
              color="primary"
              type="radio"
              inline
            />
            <div v-else class="text-weight-medium">{{ fixedKeyCategoryLabel }}</div>
          </div>
        </div>

        <div class="row items-start q-mt-sm ticket-info-row" v-show="showCreditDebitKeySelection">
          <div class="col-auto q-mr-sm q-pt-sm">Credit/Debit Key:&nbsp;</div>
          <div class="col">
            <div
              v-for="(selectedKeyIndex, index) in selectedKeyIndexes"
              :key="`edit-ticket-key-row-${index}`"
              class="row items-center q-col-gutter-sm q-mb-sm"
            >
              <div class="col-12 col-md-6">
                <q-select
                  v-model="selectedKeyIndexes[index]"
                  :options="kcvksiOpt"
                  label="Please select"
                  dense
                  clearable
                  emit-value
                  map-options
                >
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">No results</q-item-section>
                    </q-item>
                  </template>
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
              <div class="col-12 col-md-auto">
                <q-btn
                  v-if="index === selectedKeyIndexes.length - 1"
                  flat
                  color="primary"
                  label="+ ADD KEY"
                  @click="addKeyRow"
                />
                <q-btn
                  v-if="selectedKeyIndexes.length > 1"
                  flat
                  color="negative"
                  icon="remove"
                  @click="removeKeyRow(index)"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="q-my-sm section-caption">Shipping Address:</div>
        <div class="row">
          <div class="col-auto">
            <AddressBlock :address="address" @click="showAddressGrid" />
          </div>
        </div>

        <!-- tracking number section -->
        <div class="row q-my-sm items-center ticket-info-row">
          <div class="col-auto q-mr-sm">Incoming Tracking Number:&nbsp;</div>
          <div class="col">
            <q-btn
              label="Add"
              outline
              rounded
              color="primary"
              @click="addTrackingNum(ticketId)"
            />
          </div>
        </div>
        <div
          v-for="(trackingNum, index) in getTrackingNumsByTicketId(ticketId)"
          :key="index"
          class="row q-mb-sm items-center tracking-row"
        >
          <q-input
            class="q-mr-sm tracking-input"
            v-model="trackingNum.num"
            dense
            outlined
            @update:model-value="trackingNum.isUpdate = true"
          />
          <q-btn
            label="Remove"
            outline
            rounded
            color="primary"
            @click="deleteTrackingNum(ticketId, index)"
          />
        </div>
        <!-- Ticket serials -->
        <TicketEditTable
          ref="editTable"
          :isFromMaster="ticketInfo.isFromMaster"
          :containsXrefMaterials="ticketInfo.containsXrefMaterials"
          :orderType="orderDept"
          :rows="getSerialsByTicketId(ticketId)"
          :inputValue="inputValue"
          :encrypt="ticketInfo.encrypt"
          :showRemoveUnit="false"
          :showUpdateUnit="false"
          :showViewUnit="true"
          @add-sn="handleAddSN"
          @update-sn="handleUpdateSN"
          @remove-sn="handleRemoveSN"
        />
        <!-- Button for submit ticket -->
        <div class="row justify-center q-mt-md">
          <q-btn
            class="col-auto submit-ticket-btn"
            color="primary"
            @click="handleEditTicket"
            :loading="ticketEditing"
          >
            Submit Ticket
          </q-btn>
        </div>

        <div class="q-mt-lg attachment-section">
          <div class="row items-center justify-between q-mb-sm">
            <div class="text-subtitle1 text-weight-bold">Attachments</div>
            <q-btn
              flat
              color="primary"
              icon="refresh"
              label="Refresh"
              :loading="attachmentsLoading"
              @click="fetchAttachments(ticketId)"
            />
          </div>

          <div v-if="attachmentsLoading" class="text-grey-7 q-py-md">
            Loading attachments...
          </div>

          <q-banner
            v-else-if="attachments.length === 0"
            dense
            rounded
            class="bg-grey-2 text-grey-8"
          >
            No attachments found for this ticket.
          </q-banner>

          <q-list v-else bordered separator>
            <q-item
              v-for="attachment in attachments"
              :key="attachment.fileId"
            >
              <q-item-section>
                <q-item-label>{{ attachment.fileName }}</q-item-label>
                <q-item-label caption>
                  {{ attachment.type || "Unknown" }} • {{ formatFileSize(attachment.size) }}
                </q-item-label>
              </q-item-section>

              <q-item-section side>
                <div class="row items-center no-wrap q-gutter-sm">
                  <q-btn
                    flat
                    color="primary"
                    label="Download"
                    :loading="downloadingAttachmentId === attachment.fileId"
                    @click="downloadAttachment(attachment)"
                  />
                  <q-btn
                    v-if="canDeleteAttachment"
                    flat
                    color="negative"
                    label="Delete"
                    :loading="deletingAttachmentId === attachment.fileId"
                    @click="confirmDeleteAttachment(attachment)"
                  />
                </div>
              </q-item-section>
            </q-item>
          </q-list>

          <div class="q-mt-md">
            <div class="text-subtitle2 text-weight-medium q-mb-xs">Remark</div>
            <q-banner dense rounded class="bg-grey-1 text-grey-9">
              {{ ticketInfo.description || "No remark" }}
            </q-banner>
          </div>
        </div>
      </div>
    </div>
    <div class="row justify-between comment-header">
      <div
        class="col-auto text-weight-bold text-subtitle1 comment-title"
      >
        Comments
      </div>
      <div class="col-auto text-weight-bold q-mb-sm q-mt-sm ack-control">
        Acknowledged:&nbsp;&nbsp;
        <input
          type="radio"
          v-model="ticketInfo.acknowledged"
          value="0"
          @update:model-value="unackComment()"
          :disabled="!ackPermission"
        />&nbsp;Yes&nbsp;&nbsp;
        <input
          type="radio"
          v-model="ticketInfo.acknowledged"
          value="2"
          @update:model-value="ackComment()"
          :disabled="!ackPermission"
        />&nbsp;No&nbsp;
      </div>
    </div>
    <MessageBoard
      :ticketId="ticketId"
      :comments="comments"
      @add-comment="addComment"
      ref="messageBoard"
    />
    <TicketEmailPreviewModal
      :show="showEmailPreviewModal"
      :ticket-id="emailPreview.ticketId"
      :email-subject="emailPreview.subject"
      :email-content="emailPreview.content"
      @update:show="showEmailPreviewModal = $event"
    />
    <BaseModal
      :show="showAddressModal"
      title="Select Shipping Address"
      :width="972"
      @update:show="showAddressModal = false"
    >
      <AddressGrid @selectShippingAddress="selectShippingAddress" />
    </BaseModal>
  </div>
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState, mapStores } from "pinia";
import MessageBoard from "src/components/MessageBoard.vue";
import TicketEditTable from "src/components/TicketEditTable.vue";
import TicketEmailPreviewModal from "src/components/TicketEmailPreviewModal.vue";
import AddressBlock from "src/components/AddressBlock.vue";
import AddressGrid from "src/components/AddressGrid.vue";
import BaseModal from "src/components/BaseModal.vue";
import { Notify, TouchSwipe } from "quasar";
import { api } from "src/boot/axios";
import { useEditTicketStore } from "src/stores/editTicket";
import { useUserStore } from "stores/user";
import { batchSerialNumberQuery } from "src/utils/ticketUtils";

const KEY_CATEGORY_PRODUCTION = "PRODUCTION";
const KEY_CATEGORY_TEST = "TEST";

function normalizeOrderDeptLabel(label) {
  return String(label || "")
    .toLowerCase()
    .replace(/[^a-z]/g, "");
}

function normalizeKeyCategory(category) {
  const normalized = String(category || "").trim().toUpperCase();
  if (!normalized) {
    return "";
  }
  if (normalized.includes("PROD")) {
    return KEY_CATEGORY_PRODUCTION;
  }
  if (normalized.includes("TEST")) {
    return KEY_CATEGORY_TEST;
  }
  return normalized;
}

export default {
  components: {
    MessageBoard,
    TicketEditTable,
    TicketEmailPreviewModal,
    AddressBlock,
    AddressGrid,
    BaseModal,
  },
  data: () => {
    return {
      ticketInfo: {
        address: null,
        typeOfRepair: null,
        originalRMA: null,
        isFromMaster: false,
        orderStatus: null,
        submitterOrg: null,
        submitterName: null,
        submitterEmail: null,
        serials: [],
        trackingNumbers: [],
        encrypt: null,
        testKeyType: null,
        acknowledged: null,
        kcv: null,
        ksi: null,
        keyType: null,
        keyIndex: null,
        keyIndexes: [],
        description: null,
      },
      comments: [],
      editInfo: {
        updateTrackingNums: [],
        removeTrackingNums: [],
        removeSerials: [],
        updateSerials: [],
      },
      isLoading: false,
      ticketEditing: false,

      file: null,
      fileUploading: false,
      inputValue: null,

      showAddressModal: false,

      keys: null,
      kcvksiOpt: [],
      keyTypeOpt: null,
      keyType: null,
      selectedKeyIndexes: [null],

      attachments: [],
      attachmentsLoading: false,
      downloadingAttachmentId: null,
      deletingAttachmentId: null,
      showEmailPreviewModal: false,
      emailPreviewLoading: false,
      emailPreview: {
        ticketId: null,
        subject: "",
        content: "",
      },
    };
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
      () => this.$route.params,
      () => {
        if (this.$route.name !== "edit-ticket") {
          return;
        }
        this.isLoading = true;
        Promise.all([
          this.getTicket(this.ticketId),
          this.fetchComments(this.ticketId),
          this.fetchAckStatus(this.ticketId),
          this.fetchAttachments(this.ticketId),
          this.populateKeysOptOnce(),
          this.populateOrderDeptOptOnce(),
          //ensure it been populated
        ])
          .then((values) => {
            const ticketInfo = values[0];
            const comments = values[1];
            const ack = values[2];

            if (ticketInfo != null) {
              this.ticketInfo = ticketInfo;
              this.applyOrderDeptRule({ preserveSelection: true });
              this.initializeSelectedKeyIndexes();
            }
            if (comments != null) {
              this.comments = comments;
            }
            if (ack != null) {
              this.ticketInfo.acknowledged = String(ack);
            }
            this.comments.forEach((c) => {
              //is the replyer == current user, mark it as green
              if (c.email == this.email) {
                c.bgColor = "bg-green-3";
              }
            });
            this.$nextTick(() => this.$refs.messageBoard.scrollToBottom());
          })
          .catch((e) => {
            console.log(e);
          })
          .finally(() => {
            this.isLoading = false;
          });
      },
      // fetch the data when the view is created and the data is
      // already being observed
      { immediate: true }
    );
    // this.populateKeysOptOnce();
    // this.populateOrderTypeOptOnce();
    // this.populateKeyTypeOptOnce();
  },
  mounted() {},
  computed: {
    ...mapState(useUserStore, ["email", "loggedIn", "sessionTimeLeft"]),
    ...mapState(useCreateTicketStore, ["orderTypeOpt"]),
    ...mapWritableState(useEditTicketStore, [
      "getTrackingNumsByTicketId",
      "getSerialsByTicketId",
      "getTicketbyId",
    ]),
    isEncrypted() {
      return (
        this.ticketInfo.encrypt != null && this.ticketInfo.encrypt === "yes"
      );
    },
    orderDept: {
      get() {
        return this.ticketInfo.typeOfRepair;
      },
      set(value) {
        this.ticketInfo.typeOfRepair = value;
      },
    },
    orderDeptOpt() {
      return this.orderTypeOpt;
    },
    isReRepair() {
      return this.orderDept === 4;
    },
    ticketId() {
      return this.$route.params.ticketId;
    },
    address() {
      return this.ticketInfo.address;
    },
    ackPermission() {
      return (
        useUserStore().checkPermission("ticketing.ack") ||
        useUserStore().checkPermission("ticketing.edit.acknowledge")
      );
    },
    canDeleteAttachment() {
      return useUserStore().checkPermission("ticketing.update");
    },
    selectedOrderDeptLabel() {
      if (!Array.isArray(this.orderDeptOpt) || this.orderDept == null) {
        return "";
      }
      const option = this.orderDeptOpt.find(
        (item) => String(item.value) === String(this.orderDept)
      );
      return option ? option.label : "";
    },
    orderDeptRule() {
      const normalized = normalizeOrderDeptLabel(this.selectedOrderDeptLabel);

      if (
        normalized.includes("decommission") ||
        normalized.includes("return") ||
        normalized.includes("diagnostic")
      ) {
        return {
          forceEncryptNo: true,
          requiresKeySelection: false,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: null,
        };
      }

      if (normalized.includes("debug")) {
        return {
          forceEncryptNo: false,
          requiresKeySelection: true,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: KEY_CATEGORY_TEST,
        };
      }

      if (normalized.includes("rerepair") || normalized === "repair") {
        return {
          forceEncryptNo: false,
          requiresKeySelection: true,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: KEY_CATEGORY_PRODUCTION,
        };
      }

      if (normalized.includes("rework")) {
        return {
          forceEncryptNo: false,
          requiresKeySelection: true,
          allowKeyCategoryChoice: true,
          fixedKeyCategory: null,
        };
      }

      return {
        forceEncryptNo: false,
        requiresKeySelection: true,
        allowKeyCategoryChoice: true,
        fixedKeyCategory: null,
      };
    },
    forceEncryptNo() {
      return this.orderDeptRule.forceEncryptNo;
    },
    requiresKeySelection() {
      return this.orderDeptRule.requiresKeySelection;
    },
    allowKeyCategoryChoice() {
      return this.orderDeptRule.allowKeyCategoryChoice;
    },
    fixedKeyCategory() {
      return this.orderDeptRule.fixedKeyCategory;
    },
    availableKeyTypeOpt() {
      if (!Array.isArray(this.keyTypeOpt)) {
        return [];
      }
      if (this.fixedKeyCategory) {
        return this.keyTypeOpt.filter(
          (option) => option.value === this.fixedKeyCategory
        );
      }
      return this.allowKeyCategoryChoice ? this.keyTypeOpt : [];
    },
    fixedKeyCategoryLabel() {
      if (this.fixedKeyCategory === KEY_CATEGORY_PRODUCTION) {
        return "Production";
      }
      if (this.fixedKeyCategory === KEY_CATEGORY_TEST) {
        return "Test";
      }
      return "N/A";
    },
    showKeyCategorySelection() {
      return (
        this.isEncrypted &&
        this.requiresKeySelection &&
        (this.allowKeyCategoryChoice || this.fixedKeyCategory != null)
      );
    },
    showCreditDebitKeySelection() {
      return (
        this.isEncrypted &&
        this.requiresKeySelection &&
        this.keyType != null &&
        this.keyType !== ""
      );
    },
  },
  watch: {
    keyType(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.populateKcvksiOpt();
        this.resetKeyRows();
      }
    },
  },
  methods: {
    ...mapActions(useUserStore, ["logout"]),
    ...mapActions(useCreateTicketStore, [
      "populateOrderTypeOpt",
      "populateOrderTypeOptOnce",
    ]),
    ...mapActions(useEditTicketStore, [
      "fetchTicket",
      "getTicket",
      "addTrackingNum",
      "deleteTrackingNum",
      "getEditInfo",
      "findEditTrackingNums",
      "findEditSN",
      "addSN",
      "addSnList",
      "updateSN",
      "removeSN",
      "updateAddress",
    ]),
    populateOrderDeptOpt(...args) {
      return this.populateOrderTypeOpt(...args);
    },
    populateOrderDeptOptOnce(...args) {
      return this.populateOrderTypeOptOnce(...args);
    },
    resetKeyRows() {
      this.selectedKeyIndexes = [null];
    },
    addKeyRow() {
      this.selectedKeyIndexes.push(null);
    },
    removeKeyRow(index) {
      if (this.selectedKeyIndexes.length <= 1) {
        this.resetKeyRows();
        return;
      }
      this.selectedKeyIndexes.splice(index, 1);
    },
    collectSelectedKeyIndexes() {
      if (!this.isEncrypted || !this.requiresKeySelection) {
        return { keyIndexes: [], errorMessage: null };
      }

      if (!Array.isArray(this.selectedKeyIndexes) || this.selectedKeyIndexes.length === 0) {
        return {
          keyIndexes: [],
          errorMessage: "Please add at least one Credit/Debit Key",
        };
      }

      if (this.selectedKeyIndexes.some((keyIndex) => keyIndex == null || keyIndex === "")) {
        return {
          keyIndexes: [],
          errorMessage: "Please complete all Credit/Debit Key rows",
        };
      }

      const keyIndexes = this.selectedKeyIndexes.map((keyIndex) => Number(keyIndex));
      const hasInvalid = keyIndexes.some((keyIndex) => Number.isNaN(keyIndex) || keyIndex <= 0);
      if (hasInvalid) {
        return {
          keyIndexes: [],
          errorMessage: "Invalid Credit/Debit Key selection",
        };
      }

      const uniqueCount = new Set(keyIndexes).size;
      if (uniqueCount !== keyIndexes.length) {
        return {
          keyIndexes: [],
          errorMessage: "Credit/Debit Key cannot be duplicated",
        };
      }

      return { keyIndexes, errorMessage: null };
    },
    applyOrderDeptRule({ preserveSelection = false } = {}) {
      if (this.forceEncryptNo) {
        this.ticketInfo.encrypt = "no";
        this.keyType = null;
        this.resetKeyRows();
        this.kcvksiOpt = [];
        return;
      }

      if (this.ticketInfo.encrypt !== "yes") {
        this.resetKeyRows();
        this.kcvksiOpt = [];
        return;
      }

      if (this.fixedKeyCategory != null) {
        this.keyType = this.fixedKeyCategory;
      } else if (this.allowKeyCategoryChoice) {
        const isValid = this.availableKeyTypeOpt.some(
          (option) => option.value === this.keyType
        );
        if (!isValid) {
          const defaultOption =
            this.availableKeyTypeOpt.find(
              (option) => option.value === KEY_CATEGORY_PRODUCTION
            ) || this.availableKeyTypeOpt[0];
          this.keyType = defaultOption ? defaultOption.value : null;
        }
      } else {
        this.keyType = null;
      }

      this.populateKcvksiOpt();
      if (!preserveSelection) {
        this.resetKeyRows();
      }
    },
    initializeSelectedKeyIndexes() {
      const incomingKeyIndexes = Array.isArray(this.ticketInfo.keyIndexes)
        ? this.ticketInfo.keyIndexes
        : [];
      const normalized = incomingKeyIndexes
        .map((item) => Number(item))
        .filter((item) => !Number.isNaN(item) && item > 0);

      if (
        normalized.length > 0 &&
        this.allowKeyCategoryChoice &&
        Array.isArray(this.keys)
      ) {
        const firstKey = this.keys.find(
          (item) => Number(item?.label?.keyIndex) === normalized[0]
        );
        const category = normalizeKeyCategory(firstKey?.label?.keyCategory);
        if (category) {
          this.keyType = category;
          this.populateKcvksiOpt();
        }
      }

      if (normalized.length > 0) {
        this.selectedKeyIndexes = [...new Set(normalized)];
      } else if (this.ticketInfo.keyIndex != null) {
        const fallback = Number(this.ticketInfo.keyIndex);
        this.selectedKeyIndexes = !Number.isNaN(fallback) && fallback > 0 ? [fallback] : [null];
      } else {
        this.resetKeyRows();
      }
    },
    handleEditTicket() {
      //valid serials and update it
      this.ticketEditing = true;
      const editTracking = this.findEditTrackingNums(this.ticketId);
      const editSerial = this.findEditSN(this.ticketId);

      //If user didn't choose order dept, don't allow user to submit the ticket
      if (this.orderDept === null) {
        this.$q.notify({
          type: "negative",
          message: "Please Select Order Dept before Submitting.",
        });
        this.ticketEditing = false;
        return;
      }

      //if SN isn't found. Don't let customer submit ticket before Remove the record.
      for (const serial of this.getSerialsByTicketId(this.ticketId)) {
        if (serial.valid === false) {
          this.$q.notify({
            type: "negative",
            message: "Please Delete Invalid SN before Submiting",
          });
          this.ticketEditing = false;
          return;
        }
      }

      const { keyIndexes: selectedKeyIndexes, errorMessage: keyValidationError } =
        this.collectSelectedKeyIndexes();
      if (keyValidationError) {
        Notify.create({
          type: "negative",
          message: keyValidationError,
        });
        this.ticketEditing = false;
        return;
      }

      const payload = {
        ...editTracking,
        ...editSerial,
        isFromMaster: this.ticketInfo.isFromMaster,
        orderType: this.orderDept,
        address: this.ticketInfo.address,
        typeOfRepair: this.orderDept,
        originalRMA: this.ticketInfo.originalRMA,
        clientGroup: this.clientGroup,
        mcOID: this.ticketInfo.mcOID,
        encrypt: this.ticketInfo.encrypt,
        testKeyType:
          this.isEncrypted && this.requiresKeySelection && selectedKeyIndexes.length > 0
            ? String(selectedKeyIndexes[0])
            : null,
        keyIndexes:
          this.isEncrypted && this.requiresKeySelection ? selectedKeyIndexes : [],
      };
      if (payload.orderType === 3 || payload.orderType === 7) {
        payload.originalRMA = null;
      }
      const actionURL = "/ticketing/" + this.ticketId;

      api
        .put(actionURL, payload, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then(function (response) {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          Notify.create({
            type: "positive",
            message: "Update Ticket Successfully",
          });
        })
        .catch((e) => {
          Notify.create({
            type: "negative",
            message: e.message,
          });
        })
        .finally(() => {
          this.resetTicket();
          this.ticketEditing = false;
        });
    },
    resetTicket() {
      this.isLoading = true;
      Promise.all([this.fetchTicket(this.ticketId), this.fetchAttachments(this.ticketId)])
        .then(([ticket]) => {
          this.ticketInfo = ticket;
          this.applyOrderDeptRule({ preserveSelection: true });
          this.initializeSelectedKeyIndexes();
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    handleAddSN({ serial }) {
      this.addSN(this.ticketId, serial);
    },
    handleUpdateSN({ oldSN, serial }) {
      this.updateSN(this.ticketId, oldSN, serial);
    },
    handleRemoveSN({ sn }) {
      this.removeSN(this.ticketId, sn);
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
          this.addSnList(this.ticketId, serials);
        })
        .finally(() => {
          this.fileUploading = false;
        });
    },
    formatFileSize(size) {
      const bytes = Number(size || 0);
      if (Number.isNaN(bytes) || bytes <= 0) {
        return "0 B";
      }
      const units = ["B", "KB", "MB", "GB"];
      let value = bytes;
      let unitIndex = 0;
      while (value >= 1024 && unitIndex < units.length - 1) {
        value /= 1024;
        unitIndex += 1;
      }
      return `${value.toFixed(unitIndex === 0 ? 0 : 2)} ${units[unitIndex]}`;
    },
    fetchAttachments(ticketId) {
      this.attachmentsLoading = true;
      const link = `/ticketing/${ticketId}/attachments`;
      return api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage || "Failed to fetch attachments.");
          }
          this.attachments = response.data.data || [];
          return this.attachments;
        })
        .catch((error) => {
          if (!this.loggedIn) return [];
          Notify.create({
            type: "negative",
            message: error.message,
          });
          this.attachments = [];
          return [];
        })
        .finally(() => {
          this.attachmentsLoading = false;
        });
    },
    downloadAttachment(attachment) {
      this.downloadingAttachmentId = attachment.fileId;
      const link = `/ticketing/${this.ticketId}/attachments/${attachment.fileId}/download-url`;
      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage || "Failed to generate download URL.");
          }
          const downloadUrl = response.data?.data?.downloadUrl;
          if (!downloadUrl) {
            throw new Error("Download URL is empty.");
          }
          window.open(downloadUrl, "_blank");
        })
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        })
        .finally(() => {
          this.downloadingAttachmentId = null;
        });
    },
    confirmDeleteAttachment(attachment) {
      this.$q
        .dialog({
          title: "Delete Attachment",
          message: `Delete ${attachment.fileName}?`,
          cancel: true,
          persistent: true,
        })
        .onOk(() => {
          this.deleteAttachment(attachment);
        });
    },
    deleteAttachment(attachment) {
      this.deletingAttachmentId = attachment.fileId;
      const link = `/ticketing/${this.ticketId}/attachments/${attachment.fileId}`;
      api
        .delete(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage || "Failed to delete attachment.");
          }
          this.attachments = this.attachments.filter(
            (file) => file.fileId !== attachment.fileId
          );
          Notify.create({
            type: "positive",
            message: "Attachment deleted successfully.",
          });
        })
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        })
        .finally(() => {
          this.deletingAttachmentId = null;
        });
    },
    /**
     *
     * For Message board
     */
    fetchComments(ticketId) {
      const vm = this;
      const link = "/ticketing/" + ticketId + "/response";
      return api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          return response.data.data;
        })
        .catch((error) => {
          if(!this.loggedIn) return;
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    addComment(comment) {
      const user = useUserStore();
      const { username } = user;
      //call backend api to update it
      const link = `/ticketing/${this.ticketId}/response`;
      api
        .post(link, comment)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          const newComment = response.data.data.response;
          newComment.responseBy = username;
          newComment.bgColor = "bg-green-3";
          this.comments.push(newComment);
          if (this.ackPermission) {
            // customer service replied: waiting customer reply
            this.ackComment();
            this.ticketInfo.acknowledged = "2";
          } else {
            // customer replied: waiting customer service reply
            this.unackComment();
            this.ticketInfo.acknowledged = "1";
          }
          this.$nextTick(() => this.$refs.messageBoard.scrollToBottom());
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    ackComment() {
      //can backend to ack the ticket
      const link = `/ticketing/${this.ticketId}/acknowledged`;
      const vm = this;
      api
        .put(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    unackComment() {
      //can backend to unack the ticket
      const link = `/ticketing/${this.ticketId}/unacknowledged`;
      const vm = this;
      api
        .put(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    fetchAckStatus() {
      const link = `/ticketing/${this.ticketId}/acknowledged`;
      const vm = this;
      return api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          return String(response.data.data.acknowledged);
        })
        .catch((error) => {
          if(!this.loggedIn)return;
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    selectShippingAddress(address) {
      this.updateAddress(address, this.ticketId);
      this.showAddressModal = false;
    },
    openEmailPreview() {
      this.emailPreviewLoading = true;
      const link = `/ticketing/${this.ticketId}/email-preview`;
      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage || "Failed to load email details.");
          }
          const payload = response?.data?.data || {};
          this.emailPreview = {
            ticketId: payload.emailTicketId ?? this.ticketId,
            subject: payload.emailSubject || `RMA #${this.ticketId} Confirmation`,
            content: payload.emailContent || "",
          };
          this.showEmailPreviewModal = true;
        })
        .catch((error) => {
          Notify.create({
            type: "negative",
            message: error.message,
          });
        })
        .finally(() => {
          this.emailPreviewLoading = false;
        });
    },
    showAddressGrid() {
      this.showAddressModal = true;
    },
    populateKeysOptOnce() {
      const link = "/ticketing/dropdown/key";
      return api
        .get(link)
        .then((response) => {
          const payload = response?.data?.data;
          this.keys = Array.isArray(payload) ? payload : [];
          this.keyTypeOpt = this.buildCategoryOptions(this.keys);
          this.applyOrderDeptRule({ preserveSelection: true });
          this.initializeSelectedKeyIndexes();
          return this.keys;
        })
        .catch((error) => {
          if (!this.loggedIn) return [];
          console.log(error);
          Notify.create({
            type: "negative",
            message: "Key Type Dropdown cannot be populated",
          });
          return [];
        });
    },
    buildCategoryOptions(keys) {
      const categories = new Set(
        (keys || []).map((key) => normalizeKeyCategory(key?.label?.keyCategory))
      );
      const options = [];
      if (categories.has(KEY_CATEGORY_PRODUCTION)) {
        options.push({ value: KEY_CATEGORY_PRODUCTION, label: "Production" });
      }
      if (categories.has(KEY_CATEGORY_TEST)) {
        options.push({ value: KEY_CATEGORY_TEST, label: "Test" });
      }
      if (options.length > 0) return options;
      return [
        { value: KEY_CATEGORY_PRODUCTION, label: "Production" },
        { value: KEY_CATEGORY_TEST, label: "Test" },
      ];
    },
    populateKcvksiOpt() {
      const selectedCategory = normalizeKeyCategory(this.keyType);
      if (!Array.isArray(this.keys) || selectedCategory.length === 0) {
        this.kcvksiOpt = [];
        return;
      }
      const options = this.keys
        .filter(
          (key) =>
            normalizeKeyCategory(key?.label?.keyCategory) === selectedCategory
        )
        .map((key) => {
          const label = key?.label || {};
          const keyId = label.keyId == null ? "" : String(label.keyId).trim();
          const keyIndex = label.keyIndex;
          const keyType = label.keyType == null ? "" : String(label.keyType);
          const comment = label.comment == null ? "" : String(label.comment);
          const desc = [keyType, comment]
            .filter((part) => part != null && part.trim().length > 0)
            .join(" | ");
          return {
            value: keyIndex,
            label: keyId.length > 0 ? keyId : `KEY-${keyIndex}`,
            comment: desc,
          };
        })
        .filter((item) => item.value != null)
        .sort((a, b) =>
          String(a.label).localeCompare(String(b.label), undefined, {
            numeric: true,
            sensitivity: "base",
          })
        );
      this.kcvksiOpt = options;
    },
  },
};
</script>
<style scoped>
.edit-ticket-page {
  padding: 16px 8px 28px;
}

.edit-ticket-surface {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(30, 55, 90, 0.06);
}

.edit-ticket-title-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.edit-ticket-title-text {
  line-height: 1.3;
}

.email-detail-btn {
  border-radius: 8px;
}

.edit-ticket-body {
  line-height: 1.55;
}

.edit-ticket-status-bar {
  padding-bottom: 8px;
  border-bottom: 1px solid #e8edf3;
}

.ticket-info-row {
  margin-bottom: 10px;
}

.field-input-md {
  min-width: 220px;
  max-width: 360px;
}

.section-caption {
  font-weight: 600;
  color: #4d5b6a;
}

.tracking-row .tracking-input {
  min-width: 320px;
}

.submit-ticket-btn {
  min-width: 220px;
  border-radius: 10px;
}

.attachment-section {
  padding-top: 12px;
  border-top: 1px solid #e8edf3;
}

.comment-header {
  margin-top: 8px;
  align-items: center;
}

.comment-title {
  text-decoration-line: underline;
}

.ack-control {
  white-space: nowrap;
}

@media (max-width: 1023px) {
  .edit-ticket-page {
    padding: 12px 0 22px;
  }

  .field-input-md,
  .tracking-row .tracking-input {
    min-width: 0;
    max-width: none;
    width: 100%;
  }

  .ack-control {
    width: 100%;
    margin-top: 6px;
  }
}

@media (max-width: 599px) {
  .submit-ticket-btn {
    min-width: 0;
    width: 100%;
  }
}
</style>
