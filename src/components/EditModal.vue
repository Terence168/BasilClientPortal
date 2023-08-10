<template>
    <BaseModal v-bind:title="title" :width="500" @update:show="resetModal" v-bind:show="withClient">
      <q-form ref="modalForm" @submit.prevent="handleSubmitForm">
        <q-input
          class="col q-mb-sm"
          outlined
          v-model="newSerial.serialNumber"
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
          v-model="newSerial.customerReportedIssue"
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
          v-model="newSerial.terminalID"
          label="Customer Terminal ID"
          dense
        />
        <div class="row justify-center q-mt-md">
          <div class="col-auto">
            <!-- update/add device Button -->
            <q-btn
              class="q-mr-md"
              type="submit"
              v-bind:label="btnLable"
              color="primary"
              style="min-width: 150px"
              :loading="loading"
            >
              <template v-slot:loading>
                <q-spinner-facebook />
              </template>
            </q-btn>
          </div>
          <!-- cancel Button -->
          <div class="col-auto">
            <q-btn
              label="Cancel"
              color="grey-4"
              text-color="grey-6"
              style="min-width: 150px"
              @click="resetModal"
            />
          </div>
        </div>
      </q-form>
    </BaseModal>
</template>

<script>
import BaseModal from "src/components/BaseModal.vue";

export default {
  components: { BaseModal },
  props: ["serial", "title", "btnLable", "action"],
  data() {
    return {
      newSerial: {
        serialNumber: null,
        terminalID: null,
        customerReportedIssue: null,
      },
      loading: false,
      show: true,
      withClient : false,
    };
  },
  updated(){
    let serialDataDeepCopy = JSON.parse(JSON.stringify(this.serial));
    this.newSerial = serialDataDeepCopy;
  },
  methods: {
    resetModal() {
      this.newSerial = {
          serialNumber:null,
          terminalID:null,
          customerReportedIssue:null,
      };
      this.withClient = false;
    },
    displayEditModal(){
      this.withClient = true;
    },
    hideEditModal(){
      this.withClient = false;
    },
    handleSubmitForm() {
      this.$emit(`${this.action}-serial`, this.newSerial)
      this.withClient = false;
    },
  },
};
</script>
