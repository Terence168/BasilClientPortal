<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Edit Ticket
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg text-body1">
        <div class="row q-mb-md text-weight-medium">
          <!-- todo:get from ticket info -->
          <div class="col">Ticket Status: Open</div>
          <!-- <div class="col-auto" @click="resetTicket">
            <q-btn color="red">Clear Data</q-btn>
          </div> -->
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
        <div class="row items-center">
          <div class="col-auto q-mr-sm">Ticket Submitter:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="submitterName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>
        <div class="row items-center">
          <div class="col-auto q-mr-sm">Submitter Organization:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="submitterOrg"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Submitter Email:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="submitterEmail"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>
        <!-- tracking number section -->
        <div class="row q-my-sm items-center">
          <div class="col-auto q-mr-sm">Incoming Tracking Number:&nbsp;</div>
          <div class="col">
            <q-btn
              label="Add"
              outline
              rounded
              color="primary"
              @click="handleAddTrackingNum"
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
            @click="handleDeleteTrackingNum"
          />
        </div>
<!-- 
        <div class="q-py-md text-subtitle1 text-weight-bold">
          Ticket Serial Numbers
        </div> -->
        <div class="q-pa-md">
          <q-table
            title="Ticket Serial Numbers"
            :rows="serials"
            :columns="columns"
            row-key="name"
          ></q-table>
        </div>
        <!-- Button for submit ticket -->
        <div class="row justify-center">
          <q-btn
            class="col-auto"
            color="primary"
            @click="handleEditTicket"
            style="min-width: 200px"
            :loading="ticketSubmitting"
          >
            Submit
          </q-btn>
        </div>
      </div>
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
            <q-btn
              class="q-mr-md"
              type="submit"
              v-bind:label="modalState.btnLable"
              color="primary"
              style="min-width: 150px"
            >
              <template v-slot:loading>
                <q-spinner-facebook />
              </template>
            </q-btn>
          </div>
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
    <MessageBoard>
    </MessageBoard>

  </div>
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState } from "pinia";
import BaseModal from "src/components/BaseModal.vue";
import TicketSerialsGrid from "src/components/TicketSerialsGrid.vue";
import MessageBoard from "src/components/MessageBoard.vue";
import { Notify } from "quasar";
import { watchArray } from "@vueuse/core";
import { api } from "src/boot/axios";


export default {
  components: { BaseModal, MessageBoard },
  data: () => {
    return {
      address: null,
      originalRMA: null,
      submitterOrg: null,
      submitterName: null,
      submitterEmail: null,
      serials: [],
      comments:[],
      trackingNums: [],

      columns: [
        {name: "SN",align: "center", label: "Serial Number", field: "serialNumber", sortable: true, sort:(a, b)=> a<=b?1:-1 },
        { name: "Model", align: "center",label: "Model", field: "model", sortable: true, sort:(a, b)=> a<=b?1:-1},
        { name: "Version", align: "center",label: "Version", field: "version", sortable:false},
        { name: "Reported Issue", align: "center",label: "Reported Issue", field: "customerReportedIssue", sortable:false},
        { name: "Customer ID", align: "center",label: "Customer ID", field: "terminalID", sortable:false},
        { name: "Warranty Status", align: "center",label: "Warranty Status", field: "warrantyStatus", sortable: false},
        { name: "Warranty Exp. Date", align: "center",label: "Warranty Expire Date", field: "warrantyExpDate", sortable: false},],
      
        modalState: {
        title: "Update Device to Ticket",
        btnLable: "Update Device",
        serialData: {},
        serialNumber: null,
      },
      ticketSubmitting: false,
      showModal: false,
      pageLoading: false,
      updateOrAddLoading: false, //to control the update/add button's loading
    };
  },
  mounted() {
    // this.serials = [{serialNumber: 111}, {serialNumber: 222}, {serialNumber: 333}, 
    //   {serialNumber: 444}, {serialNumber: 555}, {serialNumber: 666},
    //   {serialNumber: 777}, {serialNumber: 888}, {serialNumber: 999},
    // ];
    const initPromises = [this.fetchComments(), this.fetchTicketInfo()];
    Promise.all(initPromises).then((results)=> {
      // const [comments, ticketInfo] =  results;
      // this.address = ticketInfo.address;
      // this.originalRMA = ticketInfo.originalRMA;
      // this.submitterOrg = ticketInfo.submitterOrg;
      // this.submitterName = ticketInfo.submitterName;
      // this.submitterEmail=ticketInfo.submitterEmail;
      // this.comments = comments;
      // this.trackingNums = ticketInfo.trackingNumbers;
      // this.serials = ticketInfo.serials;
    })

    // watchArray(this.serials, (newList, oldList, added, removed) => {
    //   console.log(newList); // [1, 2, 3, 4]
    //   console.log(oldList); // [1, 2, 3]
    //   console.log(added); // [4]
    //   console.log(removed); // []
    // });
  },
  computed: {
    ...mapWritableState(useCreateTicketStore, [
      "orderType",
    ]),
    ...mapState(useCreateTicketStore, [
      "orderTypeOpt",
    ]),
    isReRepair() {
      return this.orderType === 4;
    },
  },
  methods: {
    ...mapActions(useCreateTicketStore, [
      "populateOrderTypeOpt",
    ]),
    handleDeleteTrackingNum() {},
    handleAddTrackingNum() {},
    handleEditTicket() {},
    handleUpdateSerial() {},
    resetModalState() {
      this.modalState = null;
      this.showModal = false;
    },
    fetchTicketInfo(){
      const id = this.$route.params.ticketId;
      // const id = 1;
      const actionURL = "/ticketing/viewEditTicket?id="+id;
      const vm = this;
    
      return new Promise(() => this.$api
        .get(actionURL,{
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          return response.data.data;
        })
        .catch((error) => {
          console.log(error);
          this.$q.notify({
            type: "negative",
            message: error.message,
          });
        })
      )
    },
  
    fetchComments(){
      const id = this.$route.params.ticketId;
      // const id = 1;
      const vm = this;
      const link = "/ticketing/comments?id="+id;
      return new Promise(() =>
        api
          .get(link)
          .then((response) => {
            if (response.data.resultCode !== 0) {
              throw new Error(response.data.errorMessage);
            }
            return response.data.data;
          })
          .catch((error)=>{
            console.log(error);
            Notify.create({
              type: "negative",
              message: error.message,
            });
        }));
    }
  },
};
</script>
<style></style>