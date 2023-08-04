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
          <div class="col-auto" @click="resetTicket">
            <q-btn color="red">Clear Data</q-btn>
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Order Type:&nbsp;</div>
          <div class="col-auto">
            <q-select
              style="min-width: 200px"
              label="Please select"
              v-model="ticketInfo.typeOfRepair"
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
              :model-value="ticketInfo.submitterName"
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
              :model-value="ticketInfo.submitterOrg"
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
              :model-value="ticketInfo.submitterEmail"
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
          v-for="(trackingNum, index) in ticketInfo.trackingNumbers"
          :key="index"
          class="row q-mb-sm items-center"
        >
          <q-input
            class="q-mr-sm"
            v-model="ticketInfo.trackingNumbers[index]"
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
        <!-- ticket serials -->
        <TicketEditTable
          :ticketId="ticketId"
          :serials="ticketInfo.serials"
          :isFromMaster="ticketInfo.isFromMaster"
        />
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
    <MessageBoard
      :ticketId="ticketId"
      :comments="comments"
      @add-comment="addComment"
      ref="messageBoard"
    />
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
import { useUserStore } from "stores/user";

export default {
  components: { MessageBoard, TicketEditTable },
  data: () => {
    return {
      ticketInfo: {
        address: null,
        originalRMA: null,
        isFromMaster: false,
        orderStatus: null,
        typeOfRepair: null,
        //not sure about submitter info
        submitterOrg: null,
        submitterName: null,
        submitterEmail: null,

        serials: [],
        trackingNumbers: [],
      },
      comments: [],
      ticketSubmitting: false,
      showModal: false,
      isLoading: false,
      withClient: false,
      updateOrAddLoading: false, //to control the update/add button's loading
    };
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
      () => this.$route.params,
      () => {
        this.isLoading = true;

        const ticket = this.getTicket(this.ticketId);
        console.log(ticket);
        // if (ticket.isFromMaster === true) {
        //   //the order has been received, can't be changed
        // }
        // console.log(ticket);
        this.ticketInfo = ticket;
        this.fetchComments(this.ticketId);

        this.isLoading = false;
      },
      // fetch the data when the view is created and the data is
      // already being observed
      { immediate: true }
    );
  },
  computed: {
    ...mapWritableState(useCreateTicketStore, ["orderType"]),
    ...mapState(useCreateTicketStore, ["orderTypeOpt"]),
    isReRepair() {
      return this.ticketInfo.typeOfRepair === 4;
    },
    ticketId() {
      return this.$route.params.ticketId;
    },
  },
  methods: {
    ...mapActions(useCreateTicketStore, ["populateOrderTypeOpt"]),
    ...mapActions(useEditTicketStore, ["fetchTicket", "getTicket"]),
    handleEditTicket() {},
    handleUpdateSerial() {},
    resetTicket() {
      this.isLoading = true;
      this.fetchTicket(this.ticketId)
        .then((ticket) => {
          this.ticketInfo = ticket;
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    handleAddTrackingNum() {
      this.ticketInfo.trackingNumbers.push("");
    },
    handleDeleteTrackingNum(index) {
      this.ticketInfo.trackingNumbers.splice(index, 1);
    },
    fetchComments(ticketId) {
      const vm = this;
      const link = "/ticketing/" + ticketId + "/response";
      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          this.comments = response.data.data;
          return response.data.data;
        })
        .catch((error) => {
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
          this.comments.push(newComment);
          this.$refs.messageBoard.scrollToBottom();
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
  },
};
</script>
<style></style>
