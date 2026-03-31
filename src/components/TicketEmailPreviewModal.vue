<template>
  <BaseModal
    :show="show"
    title="Email Details"
    :width="980"
    @update:show="$emit('update:show', $event)"
  >
    <div class="email-preview-modal">
      <div class="email-preview-header">
        <div class="email-preview-meta-row">
          <div class="email-preview-meta-label">RMA #</div>
          <div class="email-preview-meta-value">{{ normalizedTicketId }}</div>
        </div>
        <div class="email-preview-meta-row">
          <div class="email-preview-meta-label">Subject</div>
          <div class="email-preview-meta-value">{{ normalizedSubject }}</div>
        </div>
      </div>

      <q-separator class="q-my-md" />

      <div v-if="hasContent" class="email-preview-content" v-html="emailContent"></div>
      <div v-else class="text-grey-7">No email content available.</div>
    </div>
  </BaseModal>
</template>

<script>
import BaseModal from "src/components/BaseModal.vue";

export default {
  components: {
    BaseModal,
  },
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    ticketId: {
      type: [String, Number],
      default: null,
    },
    emailSubject: {
      type: String,
      default: "",
    },
    emailContent: {
      type: String,
      default: "",
    },
  },
  emits: ["update:show"],
  computed: {
    normalizedTicketId() {
      return this.ticketId == null || this.ticketId === "" ? "N/A" : this.ticketId;
    },
    normalizedSubject() {
      return this.emailSubject && this.emailSubject.trim().length > 0
        ? this.emailSubject
        : "N/A";
    },
    hasContent() {
      return this.emailContent && this.emailContent.trim().length > 0;
    },
  },
};
</script>

<style scoped>
.email-preview-modal {
  max-width: 100%;
  line-height: 1.6;
}

.email-preview-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.email-preview-meta-row {
  display: grid;
  grid-template-columns: 92px 1fr;
  gap: 12px;
  align-items: start;
}

.email-preview-meta-label {
  font-weight: 700;
  color: #4d5b6a;
}

.email-preview-meta-value {
  word-break: break-word;
}

.email-preview-content {
  font-size: 0.95rem;
  color: #1f2d3d;
}

.email-preview-content :deep(p) {
  margin: 0 0 10px;
}

.email-preview-content :deep(strong) {
  color: #12263a;
}

@media (max-width: 768px) {
  .email-preview-meta-row {
    grid-template-columns: 1fr;
    gap: 4px;
  }
}
</style>
