<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Edit Ticket {{ ticketId }}
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
        <!-- <TicketEditSerials :ticketId="ticketId"/> -->
        <!-- <TicketEditSerialsGrid :ticketId="ticketId"></TicketEditSerialsGrid> -->
        <TicketEditTable
          :ticketId="ticketId"
          :serials="serials"
        ></TicketEditTable>
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
    <MessageBoard :ticketId="ticketId" />
  </div>
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState } from "pinia";
import MessageBoard from "src/components/MessageBoard.vue";
import TicketEditTable from "src/components/TicketEditTable.vue";
import { Notify, TouchSwipe } from "quasar";
import { api } from "src/boot/axios";
import { useEditTicketStore } from "src/stores/editTicket";

export default {
  components: { MessageBoard, TicketEditTable },
  data: () => {
    return {
      address: null,
      originalRMA: null,
      submitterOrg: null,
      submitterName: null,
      submitterEmail: null,
      // serials: [{'serialNumber':'1111', 'customerReportedIssue':'111'},
      // {'serialNumber':'2222', 'customerReportedIssue':'111'},
      //  {'serialNumber':'3333', 'customerReportedIssue':'111'},
      //   {'serialNumber':'4444', 'customerReportedIssue':'111'}, ],
      comments: [],
      trackingNums: [],

      modalState: {
        title: "Update Device to Ticket",
        btnLable: "Update Device",
        serialData: {},
        serialNumber: null,
      },
      ticketSubmitting: false,
      showModal: false,
      pageLoading: false,
      withClient: false,
      updateOrAddLoading: false, //to control the update/add button's loading
    };
  },
  async mounted() {
    console.log("====== mounted=========");
    const ticket = await this.fetchTicket(this.ticketId);
    console.log(ticket);
    console.log("====== mounted=========");
  },
  computed: {
    ...mapWritableState(useCreateTicketStore, ["orderType"]),
    ...mapState(useCreateTicketStore, ["orderTypeOpt"]),
    ...mapState(useEditTicketStore, ["getTicket"]),
    isReRepair() {
      return this.orderType === 4;
    },
    ticketId() {
      return this.$route.params.ticketId;
    },
    // serials(){
    //   const ticket = this.getTicket(this.ticketId);
    //   return ticket.serials;
    //   // // const serials = ticket.serials;
    //   // // return serials;
    //   // return [];
    // }
  },
  methods: {
    ...mapActions(useCreateTicketStore, ["populateOrderTypeOpt"]),
    ...mapActions(useEditTicketStore, ["fetchTicket"]),
    handleDeleteTrackingNum() {},
    handleAddTrackingNum() {},
    handleEditTicket() {},
    handleUpdateSerial() {},
    fetchComments() {
      const id = this.$route.params.ticketId;
      // const id = 1;
      const vm = this;
      const link = "/ticketing/comments?id=" + id;
      return new Promise(() =>
        api
          .get(link)
          .then((response) => {
            if (response.data.resultCode !== 0) {
              throw new Error(response.data.errorMessage);
            }
            return response.data.data;
          })
          .catch((error) => {
            console.log(error);
            Notify.create({
              type: "negative",
              message: error.message,
            });
          })
      );
    },
  },
};
</script>
<style>
.button-group {
  position: absolute;
  top: 50%;
  right: 50%;
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
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
