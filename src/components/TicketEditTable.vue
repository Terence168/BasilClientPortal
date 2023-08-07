<template>
  <div class="q-mx-lg">
    <div class="q-pa-md">
      <q-table
        title="Ticket Serial Numbers"
        :rows="serials"
        :columns="columns"
        row-key="serialNumber"
        @row-click="handleRowClick"
      ></q-table>

      <!-- Pop-up window: add or Update Device to Ticket Window -->
    </div>
    <BaseModal
      v-model:show="showModal"
      v-bind:title="modalState.title"
      :width="500"
      @update:show="resetModalState"
    >
      <q-form ref="modalForm" @submit.prevent="handleUpdateSerial">
        <q-input
          class="col q-mb-sm"
          outlined
          v-model="modalState.serialData.serialNumber"
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
          v-model="modalState.serialData.customerReportedIssue"
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
          v-model="modalState.serialData.terminalID"
          label="Customer Terminal ID"
          dense
        />
        <div class="row justify-center q-mt-md">
          <div class="col-auto">
            <!-- update/add device Button -->
            <q-btn
              class="q-mr-md"
              type="submit"
              v-bind:label="modalState.btnLable"
              color="primary"
              style="min-width: 150px"
              :loading="updateLoading"
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
              @click="resetModalState"
            />
          </div>
        </div>
      </q-form>
    </BaseModal>
    <PopUpBtns
      ref="popupBtns"
      :showBtns="showBtns"
      @popup-remove-sn="handleClickRemoveUnit"
      @popup-update-sn="handleClickUpdateUnit"
      @popup-view-sn="handleClickViewUnit"
    />

    <!-- View Serial Details -->
    <BaseModal
      v-model:show="showDetailModal"
      title="View repair details"
      :width="800"
    >
      <div class="row q-col-gutter-x-md text-body1">
        <div class="col-6 q-gutter-y-md">
          <div class="row">
            <div class="text-grey-6">Department:&nbsp;</div>
            <div>{{ details.department }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">RMA Customer Name:&nbsp;</div>
            <div>{{ details.customerName }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">RMA Number:&nbsp;</div>
            <div>{{ details.rmaNumber }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Part Number:&nbsp;</div>
            <div>{{ details.partNumber }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Serial Number:&nbsp;</div>
            <div>{{ details.serialNumber }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Version Number:&nbsp;</div>
            <div>{{ details.versionNumber }}</div>
          </div>

          <template v-if="details.partNumber2">
            <div class="row">
              <div class="text-grey-6">Part Number 2:&nbsp;</div>
              <div>{{ details.partNumber2 }}</div>
            </div>

            <div class="row">
              <div class="text-grey-6">Serial Number 2:&nbsp;</div>
              <div>{{ details.serialNumber2 }}</div>
            </div>

            <div class="row">
              <div class="text-grey-6">Version Number 2:&nbsp;</div>
              <div>{{ details.versionNumber2 }}</div>
            </div>
          </template>

          <div class="row">
            <div class="text-grey-6">Date Received:&nbsp;</div>
            <div>{{ details.receivedDate }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Warranty Status:&nbsp;</div>
            <div>{{ details.warrantyStatus }}</div>
          </div>
        </div>
        <div class="col-6 q-gutter-y-md">
          <div class="row">
            <div class="text-grey-6">Repaired by:&nbsp;</div>
            <div>{{ details.assignee }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">ESD Kit Included:&nbsp;</div>
            <div>{{ details.esdKit }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Tamper Log Interpretation:&nbsp;</div>
            <div>{{ details.tamperLog }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Battery Voltage:&nbsp;</div>
            <div>{{ details.batteryVoltage }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Error Message:&nbsp;</div>
            <div>{{ details.errorMessage }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Physical Damage Present:&nbsp;</div>
            <div>{{ details.physicalDamagePresent }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Warranty Voided Date:&nbsp;</div>
            <div>{{ details.warrantyVoidedDate }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Quarantine Date:&nbsp;</div>
            <div>{{ details.quarantineDate }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Repair Date:&nbsp;</div>
            <div>{{ details.repairDate }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Date Shipped:&nbsp;</div>
            <div>{{ details.shipDate }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Tracking Number:&nbsp;</div>
            <div>{{ details.trackingNumber }}</div>
          </div>
        </div>
      </div>

      <div class="row q-mt-lg text-body1">
        <div class="text-grey-6">Customer Reported Issue Reproduced:&nbsp;</div>
        <div>{{ details.customerIssueReproduced }}</div>
      </div>

      <div class="row q-mt-md text-body1">
        <div class="text-grey-6">Customer Reported Issue:&nbsp;</div>
        <div>{{ details.customerReportedIssue }}</div>
      </div>

      <div class="row q-mt-md text-body1">
        <div class="text-grey-6">Customer Reported Issue External:&nbsp;</div>
        <div>{{ details.reportedIssueExt }}</div>
      </div>

      <div class="row q-mt-md text-body1">
        <div class="text-grey-6">Technician Notes:&nbsp;</div>
        <div>{{ details.techNotes }}</div>
      </div>

      <div class="row justify-center q-mt-md">
        <div class="col-auto">
          <q-btn
            label="Close"
            color="primary"
            style="min-width: 150px"
            @click="showDetailModal = false"
          />
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script>
import { mapActions } from "pinia";
import { useEditTicketStore } from "src/stores/editTicket";
import PopUpBtns from "./PopUpBtns.vue";
import BaseModal from "./BaseModal.vue";
import { Notify } from "quasar";

export default {
  props: ["ticketId", "serials", "isFromMaster"],
  components: { BaseModal, PopUpBtns },
  emits: ["clickOnSerial"],
  data() {
    return {
      columns: [
        {
          name: "SN",
          align: "center",
          label: "Serial Number",
          field: "serialNumber",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "Model",
          align: "center",
          label: "Model",
          field: "model",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "Version",
          align: "center",
          label: "Version",
          field: "version",
          sortable: false,
        },
        {
          name: "Reported Issue",
          align: "center",
          label: "Reported Issue",
          field: "customerReportedIssue",
          sortable: false,
        },
        {
          name: "Customer ID",
          align: "center",
          label: "Customer ID",
          field: "terminalID",
          sortable: false,
        },
        {
          name: "Warranty Status",
          align: "center",
          label: "Warranty Status",
          field: "warrantyStatus",
          sortable: false,
        },
        {
          name: "Warranty Exp. Date",
          align: "center",
          label: "Warranty Expire Date",
          field: "warrantyExpDate",
          sortable: false,
        },
      ],
      showModal: false,
      updateLoading: false,
      showDetailModal: false,
      //current state of Modal
      modalState: {
        title: "Update Device to Ticket",
        btnLable: "Update Device",
        serialData: null,
        oldSerialData: null,
        serialNumber: null,
      },
      showBtns: {
        showRemoveUnit: true,
        showViewUnit: true,
        showUpdateUnit: true,
      },
      details: {},
    };
  },
  mounted() {
    if (this.isFromMaster === true) {
      this.showBtns.showRemoveUnit = false;
      this.showBtns.showUpdateUnit = false;
      this.showBtns.showViewUnit = true;
    } else {
      this.showBtns.showRemoveUnit = true;
      this.showBtns.showUpdateUnit = true;
      this.showBtns.showViewUnit = false;
    }
  },
  computed() {},
  methods: {
    ...mapActions(useEditTicketStore, [
      "removeTicket",
      "addTicket",
      "updateSN",
      "removeSN",
      "addSN",
    ]),
    resetModalState() {
      this.modalState.serialData = null;
      this.modalState.oldSerialData = null;
      this.serialNumber = null;
      this.showModal = false;
    },
    handleRowClick(evt, row, index) {
      //display popup buttons
      this.$refs.popupBtns.addPopupBtns(evt);
      this.modalState.serialData = row;
    },
    handleClickUpdateUnit() {
      let serialDataDeepCopy = JSON.parse(
        JSON.stringify(this.modalState.serialData)
      );
      this.modalState.serialData = serialDataDeepCopy;
      this.modalState.oldSerialData = this.modalState.serialData;
      this.showModal = true;
    },
    handleClickRemoveUnit() {
      const sn = this.modalState.serialNumber;
      this.removeSN(this.ticketId, sn);
      // console.log("remove");
    },
    handleClickViewUnit() {
      const { xmOID } = this.modalState.serialData;
      const link = "/ticketing/viewDetails?id=" + xmOID;
      console.log(link);
      this.$api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          this.details = response.data.data[0];
          this.showDetailModal = true;
          console.log(this.showDetailModal);
        })
        .catch(function (error) {
          // handle error
          console.log(error);

          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    handleUpdateSerial() {
      console.log("update serial");
    },
  },
};
</script>

<style>
.popup-button-group {
  position: absolute;
  height: max-content;
  justify-content: space-between;
  width: max-content;
  display: flex;
  flex-direction: row;

  .remove-unit {
    position: relative;
    z-index: 1200;
  }

  .edit-unit {
    position: relative;
    z-index: 1200;
  }
}
</style>
