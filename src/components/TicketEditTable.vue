<template>
  <div class="q-mx-lg">
    <div class="q-pa-md">
      <q-table
        title="Ticket Serial Numbers"
        :rows="getSerialsByTicketId(ticketId)"
        :columns="columns"
        row-key="serialNumber"
        @row-click="handleRowClick"
      ></q-table>
    </div>
    <!-- Pop-up window: add or Update Device to Ticket Window -->
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
    <div class="q-mb-lg">
      <div class="row justify-center">
        <div class="col-auto">
          <q-card flat bordered style="width: 700px">
            <q-card-section>
              <div class="text-body2 text-weight-medium q-mb-sm">
                Unit Summary
              </div>
              <q-separator />
              <div
                class="row justify-center items-center"
                style="height: 130px"
              >
                <div class="col text-center">
                  <div class="text-h4 text-weight-medium text-primary">
                    {{details.status}}
                  </div>
                  <div class="text-body2 text-grey-6">United Status</div>
                </div>

                <div class="col text-center">
                  <div class="text-h4 text-weight-medium text-primary">
                    {{parseDate(details.scheduledDate)}}
                  </div>
                  <div class="text-body2 text-grey-6">Scheduled Date</div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div> 
      <div class="q-mt-sm text-body1 text-weight-medium">Timeline</div>
      <ul class="timeline row justify-center">
        <li
          class="col relative-position text-center"
          v-for="(title, index) in statusTitles"
          :key="index"
        >
          <div class="text-subtitle1 text-weight-medium">
            {{ title }}
          </div>
          <div v-if="details.statusItems[index].completed" class="text-caption text-grey-6">
            {{ parseDateTime(details.statusItems[index].completeTime)}}
          </div>

          <div
            class="timeline-dot row justify-center items-center"
            :class="details.statusItems[index].completed? 'bg-primary' : 'bg-grey-6'"
          >
            <div class="col-auto">
              <q-icon
              class="text-white"
              :name="details.statusItems[index] ? 'done' : 'access_time'"
              size="md"
              ></q-icon>
            </div>
          </div>

          <div
            class="connector"
            :class="details.statusItems[index]? 'bg-primary' : 'bg-grey-6'"
          ></div>
        </li>
      </ul>
    </div>
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
            <div>{{ parseDateTime(details.receivedDate) }}</div>
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
            <div>{{ parseDateTime(details.quarantineDate) }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Repair Date:&nbsp;</div>
            <div>{{ parseDateTime(details.repairDate) }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Date Shipped:&nbsp;</div>
            <div>{{ parseDateTime(details.shipDate) }}</div>
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
import { mapActions,  mapState } from "pinia";
import { useEditTicketStore } from "src/stores/editTicket";
import PopUpBtns from "./PopUpBtns.vue";
import BaseModal from "./BaseModal.vue";
import { DateTime } from "luxon";
import { Notify } from "quasar";

export default {
  props: ["ticketId",  "isFromMaster"],
  components: { BaseModal, PopUpBtns},
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
      statusTitles: ["Unit Received", "Out for Repair", "Repair Completed",  "QA/CA", "Unit Shipped"],
      editTicket:{
        removeSN:[],
      }
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
  computed:{
    ...mapState(useEditTicketStore, [
      "getSerialsByTicketId",
    ]),
  },
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
      const sn = this.modalState.serialData.serialNumber;
      const xmOID = this.modalState.serialData.xmOID;
      this.editTicket.removeSN.push(xmOID);
      //then remove it from front-end
      this.removeSN(this.ticketId, sn);
      // console.log(this.modalState);
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
          console.log(this.details);

          this.computeStatusItem();
          this.showDetailModal = true;

          console.log(this.details);
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
    computeStatusItem(){
      const now = DateTime.now();

      const receive = DateTime.fromISO(this.details.receivedDate);
      const repair =  DateTime.fromISO(this.details.repairDate);
      const complete = DateTime.fromISO(this.details.completedDate);
      const qa = DateTime.fromISO(this.details.quarantineDate);
      const ship = DateTime.fromISO(this.details.shipDate);

      const statusItems = [];
      statusItems.push({
        completed: receive < now,
        completeTime: this.details.receivedDate});
      
      statusItems.push({
        completed: repair < now,
        completeTime: this.details.repairDate});
      
      statusItems.push(statusItems[1]={
        completed: complete < now,
        completeTime: this.details.completedDate});
      statusItems.push({
        completed: qa < now,
        completeTime: this.details.quarantineDate});
      
      statusItems.push({
        completed: ship < now,
        completeTime: this.details.shipDate});

      this.details.statusItems = statusItems;
    },
    parseDate(timeStr){
      if(timeStr === null || timeStr === ""){
        return "N/A"
      }
      const timeFormat = 'yyyy-LL-dd';
      const time = DateTime.fromISO(timeStr);
      return time.toFormat(timeFormat);
    },
    parseDateTime(timeStr){
      if(timeStr === null || timeStr === ""){
        return "N/A"
      }
      const timeFormat = 'yyyy-LL-dd tt';
      const time = DateTime.fromISO(timeStr);
      return time.toFormat(timeFormat);
    }
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

ul {
  padding: 0;
  width: 100%;
  list-style: none;
}

ul > li {
  padding-top: 50px;
}

.timeline-dot {
  position: absolute;
  top: 0;
  left: 50%;
  width: 46px;
  height: 46px;

  z-index: 1;

  border-radius: 100%;
  transform: translateX(-50%);
  border: 3px solid white;
}


.connector {
  position: absolute;
  top: 23px;
  left: 0;
  width: 100%;
  height: 3px;

  transform: translateY(-50%);
}

</style>
