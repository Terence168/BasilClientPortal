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
        <PopUpBtns ref="popupBtns" 
        :showBtns="showBtns"
        @popup-remove-sn="handleClickRemoveUnit"
        @popup-update-sn="handleClickUpdateUnit"
        @popup-view-sn="handleClickViewUnit"
        />
    </div>
</template>

<script>
import { mapActions } from "pinia";
import { useEditTicketStore } from "src/stores/editTicket";
import PopUpBtns from "./PopUpBtns.vue";
import BaseModal from "./BaseModal.vue";


export default {
  props: ["ticketId", "serials", "isFromMaster"],
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
          sort: (a, b) => (a <= b ? 1 : -1)},
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
      showModal:false,
      updateLoading:false,
      //current state of Modal
      modalState: {
          title: "Update Device to Ticket",
          btnLable: "Update Device",
          serialData: null,
          oldSerialData: null,
          serialNumber: null,
      }, 
      showBtns:{
        showRemoveUnit:true,
        showViewUnit:true,
        showUpdateUnit:true,
      }
    };
  },
  mounted(){
    if(this.isFromMaster === true){
        this.showBtns.showRemoveUnit = false;
        this.showBtns.showUpdateUnit = false;
        this.showBtns.showViewUnit = true;
    }
    else{
        this.showBtns.showRemoveUnit = true;
        this.showBtns.showUpdateUnit = true;
        this.showBtns.showViewUnit = false;
    }
  },
  computed(){ 
  },
  methods:{
    ...mapActions(useEditTicketStore, ["removeTicket", "addTicket", "updateSN", "removeSN", "addSN"]),
    resetModalState(){
        this.modalState.serialData = null;
        this.modalState.oldSerialData = null;
        this.serialNumber = null;
        this.showModal = false;
    },
    handleRowClick(evt, row, index){
        //display popup buttons
        this.$refs.popupBtns.addPopupBtns(evt);
        this.modalState.serialData = row;
    },
    handleClickUpdateUnit(){
        let serialDataDeepCopy = JSON.parse(JSON.stringify(this.modalState.serialData));
        this.modalState.serialData = serialDataDeepCopy;
        this.modalState.oldSerialData = this.modalState.serialData;
        this.showModal = true;
    },
    handleClickRemoveUnit(){
        const sn = this.modalState.serialNumber;
        this.removeSN(this.ticketId, sn);
        // console.log("remove");
    },
    handleClickViewUnit(){
        console.log("view");
    },
    handleUpdateSerial(){
        console.log("update serial");
    }
  },
}
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