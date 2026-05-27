<template>
  <div class="view-ticket-detail-page q-mx-lg">
    <div class="generic-container">
      <div
        class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header view-ticket-title-bar"
      >
        <span>Ticket Details {{ ticketId }}</span>
        <div class="row items-center q-gutter-sm">
          <q-btn
            flat
            color="primary"
            icon="arrow_back"
            label="Back to list"
            no-caps
            :to="{ name: 'view-tickets' }"
          />
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
    </div>

    <div v-if="isLoading" class="q-mt-lg text-center q-pa-xl text-grey-7">
      Loading ticket details...
    </div>

    <template v-else>
      <div class="q-mt-lg generic-container view-ticket-surface">
        <div class="q-px-lg q-pt-md q-mb-md q-pb-lg view-ticket-body">
          <div class="row q-mb-md text-weight-medium view-ticket-status-bar">
            <div class="col">Ticket Status: {{ ticketStatusLabel }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-section-title">Ticket Information</div>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Order Dept</div>
                  <div class="detail-value">{{ selectedOrderDeptLabel || "—" }}</div>
                </div>
              </div>
              <div v-if="isReRepair" class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Original RMA#</div>
                  <div class="detail-value">{{ displayText(ticketInfo.originalRMA) }}</div>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Ticket Submitter</div>
                  <div class="detail-value">{{ displayText(ticketInfo.submitterName) }}</div>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Submitter Organization</div>
                  <div class="detail-value">{{ displayText(ticketInfo.submitterOrg) }}</div>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Submitter Email</div>
                  <div class="detail-value">{{ displayText(ticketInfo.submitterEmail) }}</div>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Encrypt</div>
                  <div class="detail-value">{{ encryptDisplay }}</div>
                </div>
              </div>
              <div v-if="showKeyCategorySelection" class="col-12 col-md-6">
                <div class="detail-field">
                  <div class="detail-label">Key</div>
                  <div class="detail-value">{{ keyCategoryDisplay }}</div>
                </div>
              </div>
              <div v-if="showCreditDebitKeySelection" class="col-12">
                <div class="detail-field">
                  <div class="detail-label">Credit/Debit Key</div>
                  <div class="detail-value detail-value-multiline">
                    {{ creditDebitKeysDisplay }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-section q-mt-md">
            <div class="detail-section-title">Shipping Address</div>
            <div class="row">
              <div class="col-auto">
                <AddressBlock :address="address" />
              </div>
            </div>
          </div>

          <div class="detail-section q-mt-md">
            <div class="detail-section-title">Incoming Tracking Number</div>
            <div class="detail-value">
              {{ trackingNumbersDisplay }}
            </div>
          </div>

          <div class="q-mt-lg">
            <TicketEditTable
              :isFromMaster="ticketInfo.isFromMaster"
              :containsXrefMaterials="ticketInfo.containsXrefMaterials"
              :orderDept="orderDept"
              :rows="getSerialsByTicketId(ticketId)"
              :encrypt="ticketInfo.encrypt"
              :showRemoveUnit="false"
              :showUpdateUnit="false"
              :showViewUnit="true"
            />
          </div>

          <div class="q-mt-lg attachment-section">
            <div class="text-subtitle1 text-weight-bold q-mb-sm">Attachments</div>

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
                    {{ attachment.type || "Unknown" }} •
                    {{ formatFileSize(attachment.size) }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-btn
                    flat
                    color="primary"
                    label="Download"
                    :loading="downloadingAttachmentId === attachment.fileId"
                    @click="downloadAttachment(attachment)"
                  />
                </q-item-section>
              </q-item>
            </q-list>

            <div class="q-mt-md detail-field">
              <div class="detail-label">Remark</div>
              <div class="detail-value detail-value-multiline">
                {{ displayText(ticketInfo.description) }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row justify-between comment-header q-mt-md">
        <div class="col-auto text-weight-bold text-subtitle1 comment-title">
          Comments
        </div>
      </div>
      <div class="generic-container comments-readonly q-pa-md">
        <div v-if="comments.length === 0" class="text-grey-7 text-center q-py-lg">
          No comments yet.
        </div>
        <q-list v-else separator>
          <q-item
            v-for="(comment, index) in comments"
            :key="index"
            :class="comment.bgColor || ''"
            class="comment-item"
          >
            <q-item-section>
              <q-item-label class="row justify-between items-center">
                <span class="text-weight-bold">{{ comment.responseBy }}</span>
                <span class="text-caption text-grey-7">
                  {{ formatCommentDate(comment.responseDate) }}
                </span>
              </q-item-label>
              <q-item-label class="q-mt-xs">
                <div v-html="comment.content" class="comment-box"></div>
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </template>

    <TicketEmailPreviewModal
      :show="showEmailPreviewModal"
      :ticket-id="emailPreview.ticketId"
      :email-subject="emailPreview.subject"
      :email-content="emailPreview.content"
      @update:show="showEmailPreviewModal = $event"
    />
  </div>
</template>

<script>
import { mapActions, mapState, mapWritableState } from "pinia";
import TicketEditTable from "src/components/TicketEditTable.vue";
import TicketEmailPreviewModal from "src/components/TicketEmailPreviewModal.vue";
import AddressBlock from "src/components/AddressBlock.vue";
import { Notify } from "quasar";
import { api } from "src/boot/axios";
import { useEditTicketStore } from "src/stores/editTicket";
import { useCreateTicketStore } from "src/stores/createTicket";
import { useUserStore } from "stores/user";
import { parseDateTime } from "src/utils/timeUtils";

const KEY_CATEGORY_PRODUCTION = "PRODUCTION";
const KEY_CATEGORY_TEST = "TEST";
const TICKET_STATUS_OPEN = "12";
const TICKET_STATUS_CLOSED = "13";

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

function normalizeEncryptFlag(encrypt) {
  if (encrypt == null || String(encrypt).trim() === "") {
    return null;
  }
  const normalized = String(encrypt).trim().toLowerCase();
  if (normalized === "yes" || normalized === "y" || normalized === "1") {
    return "yes";
  }
  if (normalized === "no" || normalized === "n" || normalized === "0") {
    return "no";
  }
  return normalized;
}

export default {
  components: {
    TicketEditTable,
    TicketEmailPreviewModal,
    AddressBlock,
  },
  data() {
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
        encrypt: null,
        keyIndex: null,
        keyIndexes: [],
        description: null,
        containsXrefMaterials: null,
      },
      comments: [],
      isLoading: false,
      keys: null,
      kcvksiOpt: [],
      keyTypeOpt: null,
      keyType: null,
      selectedKeyIndexes: [null],
      isHydratingTicket: false,
      attachments: [],
      attachmentsLoading: false,
      downloadingAttachmentId: null,
      showEmailPreviewModal: false,
      emailPreviewLoading: false,
      emailPreview: {
        ticketId: null,
        subject: "",
        content: "",
      },
    };
  },
  computed: {
    ...mapState(useUserStore, ["loggedIn"]),
    ...mapState(useCreateTicketStore, ["orderDeptOpt"]),
    ...mapWritableState(useEditTicketStore, [
      "getTrackingNumsByTicketId",
      "getSerialsByTicketId",
    ]),
    ticketId() {
      return this.$route.params.ticketId;
    },
    orderDept() {
      return this.ticketInfo.typeOfRepair;
    },
    address() {
      return this.ticketInfo.address;
    },
    isReRepair() {
      return this.orderDept === 4;
    },
    isEncrypted() {
      return this.ticketInfo.encrypt === "yes";
    },
    isTicketClosed() {
      return String(this.ticketInfo.orderStatus) === TICKET_STATUS_CLOSED;
    },
    ticketStatusLabel() {
      const s = String(this.ticketInfo.orderStatus);
      if (s === TICKET_STATUS_CLOSED) return "Closed";
      if (s === TICKET_STATUS_OPEN) return "Open";
      return "Open";
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
          requiresKeySelection: false,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: null,
        };
      }

      if (normalized.includes("debug")) {
        return {
          requiresKeySelection: true,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: KEY_CATEGORY_TEST,
        };
      }

      if (normalized.includes("rerepair") || normalized === "repair") {
        return {
          requiresKeySelection: true,
          allowKeyCategoryChoice: false,
          fixedKeyCategory: KEY_CATEGORY_PRODUCTION,
        };
      }

      if (normalized.includes("rework")) {
        return {
          requiresKeySelection: true,
          allowKeyCategoryChoice: true,
          fixedKeyCategory: null,
        };
      }

      return {
        requiresKeySelection: true,
        allowKeyCategoryChoice: true,
        fixedKeyCategory: null,
      };
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
    encryptDisplay() {
      const value = normalizeEncryptFlag(this.ticketInfo.encrypt);
      if (value === "yes") return "Yes";
      if (value === "no") return "No";
      return "—";
    },
    keyCategoryDisplay() {
      if (this.fixedKeyCategory != null) {
        return this.fixedKeyCategoryLabel;
      }
      if (this.keyType === KEY_CATEGORY_PRODUCTION) return "Production";
      if (this.keyType === KEY_CATEGORY_TEST) return "Test";
      return "—";
    },
    creditDebitKeysDisplay() {
      if (!this.showCreditDebitKeySelection) {
        return "—";
      }
      const labels = this.selectedKeyIndexes
        .filter((keyIndex) => keyIndex != null && keyIndex !== "")
        .map((keyIndex) => {
          const option = this.kcvksiOpt.find(
            (item) => Number(item.value) === Number(keyIndex)
          );
          if (!option) {
            return String(keyIndex);
          }
          const comment = option.comment ? ` (${option.comment})` : "";
          return `${option.label}${comment}`;
        });
      return labels.length > 0 ? labels.join("; ") : "—";
    },
    trackingNumbersDisplay() {
      const trackingNums = this.getTrackingNumsByTicketId(this.ticketId) || [];
      const values = trackingNums
        .map((item) => String(item?.num || "").trim())
        .filter((num) => num.length > 0);
      return values.length > 0 ? values.join(", ") : "—";
    },
  },
  created() {
    this.$watch(
      () => this.$route.params,
      () => {
        if (this.$route.name !== "view-ticket-detail") {
          return;
        }
        this.loadTicketData();
      },
      { immediate: true }
    );
  },
  methods: {
    ...mapActions(useCreateTicketStore, ["populateOrderDeptOptOnce"]),
    ...mapActions(useEditTicketStore, ["getTicket"]),
    displayText(value) {
      const text = value == null ? "" : String(value).trim();
      return text.length > 0 ? text : "—";
    },
    formatCommentDate(dateValue) {
      return parseDateTime(dateValue) || "—";
    },
    loadTicketData() {
      this.isLoading = true;
      Promise.all([
        this.getTicket(this.ticketId),
        this.fetchComments(this.ticketId),
        this.fetchAttachments(this.ticketId),
        this.populateKeysOptOnce(),
        this.populateOrderDeptOptOnce(),
      ])
        .then((values) => {
          const ticketInfo = values[0];
          const comments = values[1];
          if (ticketInfo != null) {
            this.syncTicketKeyUiFromLoadedData(ticketInfo);
          }
          if (comments != null) {
            this.comments = comments;
            this.comments.forEach((comment) => {
              if (comment.email === useUserStore().email) {
                comment.bgColor = "bg-green-3";
              }
            });
          }
        })
        .catch((error) => {
          console.log(error);
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    syncTicketKeyUiFromLoadedData(ticketInfo) {
      this.isHydratingTicket = true;
      this.ticketInfo = ticketInfo;
      this.ticketInfo.encrypt = normalizeEncryptFlag(this.ticketInfo.encrypt);
      this.applyOrderDeptRule({ preserveSelection: true });
      this.initializeSelectedKeyIndexes();
      this.$nextTick(() => {
        this.isHydratingTicket = false;
      });
    },
    applyOrderDeptRule({ preserveSelection = false } = {}) {
      if (this.ticketInfo.encrypt !== "yes") {
        this.keyType = null;
        this.kcvksiOpt = [];
        return;
      }

      if (this.fixedKeyCategory != null) {
        this.keyType = this.fixedKeyCategory;
      } else if (this.allowKeyCategoryChoice) {
        const isValid = (this.keyTypeOpt || []).some(
          (option) => option.value === this.keyType
        );
        if (!isValid) {
          const defaultOption =
            (this.keyTypeOpt || []).find(
              (option) => option.value === KEY_CATEGORY_PRODUCTION
            ) || (this.keyTypeOpt || [])[0];
          this.keyType = defaultOption ? defaultOption.value : null;
        }
      } else {
        this.keyType = null;
      }

      this.populateKcvksiOpt();
      if (!preserveSelection) {
        this.selectedKeyIndexes = [null];
      }
    },
    initializeSelectedKeyIndexes() {
      const incomingKeyIndexes = Array.isArray(this.ticketInfo.keyIndexes)
        ? this.ticketInfo.keyIndexes
        : [];
      const normalized = incomingKeyIndexes
        .map((item) => Number(item))
        .filter((item) => !Number.isNaN(item) && item > 0);

      if (normalized.length > 0 && Array.isArray(this.keys)) {
        const firstKey = this.keys.find(
          (item) => Number(item?.label?.keyIndex) === normalized[0]
        );
        const category = normalizeKeyCategory(firstKey?.label?.keyCategory);
        if (category && this.allowKeyCategoryChoice) {
          this.keyType = category;
        }
      }

      if (normalized.length > 0) {
        this.selectedKeyIndexes = [...new Set(normalized)].map((item) =>
          Number(item)
        );
      } else if (
        this.ticketInfo.keyIndex != null &&
        String(this.ticketInfo.keyIndex).trim() !== ""
      ) {
        const fallback = Number(this.ticketInfo.keyIndex);
        this.selectedKeyIndexes =
          !Number.isNaN(fallback) && fallback > 0 ? [fallback] : [null];
      } else {
        this.selectedKeyIndexes = [null];
      }

      this.populateKcvksiOpt();
    },
    populateKeysOptOnce() {
      const link = "/ticketing/dropdown/key";
      return api
        .get(link)
        .then((response) => {
          const payload = response?.data?.data;
          this.keys = Array.isArray(payload) ? payload : [];
          this.keyTypeOpt = this.buildCategoryOptions(this.keys);
          if (this.ticketInfo?.moOID != null) {
            this.applyOrderDeptRule({ preserveSelection: true });
            this.initializeSelectedKeyIndexes();
          }
          return this.keys;
        })
        .catch((error) => {
          if (!this.loggedIn) return [];
          console.log(error);
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
      this.kcvksiOpt = this.keys
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
    },
    fetchComments(ticketId) {
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
          if (!this.loggedIn) return [];
          Notify.create({
            type: "negative",
            message: error.message,
          });
          return [];
        });
    },
    fetchAttachments(ticketId) {
      this.attachmentsLoading = true;
      const link = `/ticketing/${ticketId}/attachments`;
      return api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(
              response.data.errorMessage || "Failed to fetch attachments."
            );
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
    downloadAttachment(attachment) {
      this.downloadingAttachmentId = attachment.fileId;
      const link = `/ticketing/${this.ticketId}/attachments/${attachment.fileId}/download-url`;
      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(
              response.data.errorMessage || "Failed to generate download URL."
            );
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
    openEmailPreview() {
      this.emailPreviewLoading = true;
      const link = `/ticketing/${this.ticketId}/email-preview`;
      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(
              response.data.errorMessage || "Failed to load email details."
            );
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
  },
};
</script>

<style scoped>
.view-ticket-detail-page {
  padding: 16px 8px 28px;
}

.view-ticket-surface {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(30, 55, 90, 0.06);
}

.view-ticket-title-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.view-ticket-body {
  line-height: 1.55;
}

.view-ticket-status-bar {
  padding-bottom: 8px;
  border-bottom: 1px solid #e8edf3;
}

.detail-section {
  padding-top: 4px;
}

.detail-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #4d5b6a;
  margin-bottom: 12px;
}

.detail-field {
  margin-bottom: 14px;
}

.detail-label {
  font-size: 13px;
  font-weight: 600;
  color: #6b7c8f;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 15px;
  color: #1f2a37;
  word-break: break-word;
}

.detail-value-multiline {
  white-space: pre-wrap;
}

.attachment-section {
  padding-top: 12px;
  border-top: 1px solid #e8edf3;
}

.comment-header {
  align-items: center;
}

.comment-title {
  text-decoration-line: underline;
}

.comments-readonly {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(30, 55, 90, 0.06);
}

.comment-item {
  border-radius: 8px;
  margin-bottom: 4px;
}

.comment-box :deep(p) {
  margin: 0 0 0.5em;
}

@media (max-width: 1023px) {
  .view-ticket-detail-page {
    padding: 12px 0 22px;
  }
}
</style>
