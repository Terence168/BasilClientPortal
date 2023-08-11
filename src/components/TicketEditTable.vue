<template>
  <div>
    <div class="q-py-md text-subtitle1 text-weight-bold">
          Ticket Serial Numbers
        </div>
        <div class="row items-start">
          <!-- Add Serial Number -->
          <q-btn class="col-auto" color="primary" @click="handleClickAddUnit">
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
    <div class="q-pa-md">
      <q-table 
        title="Ticket Serial Numbers" 
        row-key="name" 
        :columns="columns" 
        :rows="getSerialsByTicketId(ticketId)"
      >
        <template v-slot:body="props" >
          <q-tr :prop="props" :class="props.row.bgColor" @click="handleRowClick($event, props.row)">
            <q-td key="sn" :props="props">
              {{props.row.serialNumber}}
            </q-td>
            <q-td key="model" :props="props">
              {{props.row.model}}
            </q-td>
            <q-td key="version" :props="props">
              {{props.row.versionNumber}}
            </q-td>
            <q-td key="customerReportedIssue" :props="props">
              {{props.row.customerReportedIssueExt}}
            </q-td>
            <q-td key="terminalID" :props="props" >
              {{props.row.customerTerminalID}}
            </q-td>
            <q-td key="warrantyStatus">
              {{props.row.warrantyStatus}}
            </q-td>
            <q-td key="warrantyExpDate">
              {{getParseDate(props.row.warrantyEndDate)}}
            </q-td>
            </q-tr>
      </template>
      </q-table>
    </div>  
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
                    {{getParseDate(details.scheduledDate)}}
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
            {{ getParseDateTime(details.statusItems[index].completeTime)}}
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
            <div>{{ getParseDateTime(details.receivedDate) }}</div>
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
            <div>{{ getParseDateTime(details.quarantineDate) }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Repair Date:&nbsp;</div>
            <div>{{ getParseDateTime(details.repairDate) }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Date Shipped:&nbsp;</div>
            <div>{{ getParseDateTime(details.shipDate) }}</div>
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

    <!-- Pop-up window: add or Update Device to Ticket Window -->
    <EditModal 
      ref="editModal"
      :serial="modalState.serialData" 
      :title="modalState.title" 
      :btnLable="modalState.btnLable"
      :action="modalState.submitAction"
      @add-serial="handleAddSerial"
      @update-serial = "handleUpdateSerial"
    />
  </div>
</template>

<script>
import { mapActions,  mapState, mapWritableState } from "pinia";
import { useEditTicketStore } from "src/stores/editTicket";
import PopUpBtns from "./PopUpBtns.vue";
import BaseModal from "./BaseModal.vue";
import EditModal from "./EditModal.vue";
import { DateTime } from "luxon";
import { Notify } from "quasar";
import {parseDateTime, parseDate} from "../utils/timeUtils.js"
import { batchSerialNumberQuery, serialNumberUpdateQuery } from "src/utils/ticketUtils";

export default {
  props: ["ticketId",  "isFromMaster"],
  components: { PopUpBtns, BaseModal, EditModal},
  emits: ["clickOnSerial"],
  data() {
    return {
      columns: [
        {
          name: "sn",
          align: "center",
          label: "Serial Number",
          field: "serialNumber",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "model",
          align: "center",
          label: "Model",
          field: "model",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "version",
          align: "center",
          label: "Version",
          field: "version",
          sortable: false,
        },
        {
          name: "customerReportedIssue",
          align: "center",
          label: "Reported Issue",
          field: "customerReportedIssue",
          sortable: false,
        },
        {
          name: "terminalID",
          align: "center",
          label: "Customer ID",
          field: "terminalID",
          sortable: false,
        },
        {
          name: "warrantyStatus",
          align: "center",
          label: "Warranty Status",
          field: "warrantyStatus",
          sortable: false,
        },
        {
          name: "warrantyExpDate",
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
        title: "Add Device to Ticket",
        btnLable: "Add Device",
        serialData: {
          oldSerialNumber:null,
          serialNumber:null,
          terminalID:null,
          customerReportedIssue:null,
        },
        submitAction:"add",
      },
      details: {},
      statusTitles: ["Unit Received", "Out for Repair", "Repair Completed",  "QA/CA", "Unit Shipped"],
      editTicket:{
        removeSN:[],
      },
      file: null,
      fileUploading: false,
    };
  },
  mounted() {
  },
  computed:{
    ...mapWritableState(useEditTicketStore, [
      "getSerialsByTicketId",
    ]),
    showBtns(){
      return{
        showRemoveUnit : !this.isFromMaster,
        showUpdateUnit : !this.isFromMaster,
        showViewUnit : this.isFromMaster,
      }
    }
  },
  methods: {
    ...mapActions(useEditTicketStore, [
      "removeTicket",
      "addTicket",
      "updateSN",
      "removeSN",
      "addSN",
    ]),
    handleRowClick(evt, row) {
      //display popup buttons
      this.$refs.popupBtns.addPopupBtns(evt);
      this.modalState.serialData = row;
    },
    handleClickAddUnit(){
      this.modalState.title = "Add Device to Ticket";
      this.modalState.btnLable = "Add Device"
      this.modalState.submitAction = "add"
      this.modalState.serialData = {
          serialNumber:null,
          terminalID:null,
          customerReportedIssue:null,
      };
      this.$refs.editModal.displayEditModal();
    },
    handleClickUpdateUnit() {
      this.modalState.title = "Update Device to Ticket";
      this.modalState.btnLable = "Update Device"
      this.modalState.submitAction = "update"
      this.modalState.oldSerialNumber = this.modalState.serialData.serialNumber
      this.$refs.editModal.displayEditModal();
    },
    handleClickRemoveUnit() {
      const sn = this.modalState.serialData.serialNumber;
      this.removeSN(this.ticketId, sn);
    },
    handleClickViewUnit() {
      const { xmOID } = this.modalState.serialData;
      const link = "/ticketing/viewDetails?id=" + xmOID;
      this.$api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          this.details = response.data.data[0];

          this.computeStatusItem();
          this.showDetailModal = true;
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
    /**
     * Handler for child component: EditModal
     */
    handleAddSerial(serial){
      //todo: go to backend api to valid it
      this.addSN(this.ticketId, serial);
      this.$refs.editModal.hideEditModal();
    },
    handleUpdateSerial(serial) {
      const oldSN = this.modalState.serialData.serialNumber;
      this.updateSN(this.ticketId, oldSN, serial);
      //todo: go to backend api to valid it
      this.$refs.editModal.hideEditModal();
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
          serials.forEach((s) => {
            ///needs to valid in addSN
            this.addSN(this.ticketId, s)
          });
        })
        .finally(() => {
          this.fileUploading = false;
        });
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
    getParseDate(timeStr){
      return parseDate(timeStr);
    },
    getParseDateTime(timeStr){
      return parseDateTime(timeStr);
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
