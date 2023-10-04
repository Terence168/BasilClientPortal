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
          <div class="col">Ticket Status: Open</div>
          <div class="col-auto" @click="resetTicket">
            <q-btn color="red">Refresh Data</q-btn>
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Order Type:&nbsp;</div>
          <div class="col-auto">
            <q-select
              ref="orderTypeSelect"
              style="min-width: 200px"
              label="Please select"
              v-model="ticketInfo.typeOfRepair"
              :options="orderTypeOpt"
              :disable="true"
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
            <q-input
              style="min-width: 200px"
              dense
              v-model="ticketInfo.originalRMA"
            />
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
        <div class="row items-center">
          Encrypt:&nbsp;
          <input
            type="radio"
            v-model="ticketInfo.encrypt"
            value="yes"
            disabled
          />&nbsp;Yes&nbsp;&nbsp;
          <input
            type="radio"
            v-model="ticketInfo.encrypt"
            value="no"
            disabled
          />&nbsp;No&nbsp;
        </div>
        <div class="row items-center" v-show="isEncrypted">
          <div class="col-auto q-mr-sm">Test Key Type:&nbsp;</div>
          <div class="col-auto">
            <q-select
              ref="testKeyTypeSelect"
              style="min-width: 200px"
              label="Please select"
              v-model="ticketInfo.testKeyType"
              :options="keyTypeOpt"
              @filter="populateKeyTypeOpt"
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

        <div class="q-my-sm">Shipping Address:</div>
        <div class="row">
          <div class="col-auto">
            <AddressBlock :address="address" @click="showAddressGrid" />
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
              @click="addTrackingNum(ticketId)"
            />
          </div>
        </div>
        <div
          v-for="(trackingNum, index) in getTrackingNumsByTicketId(ticketId)"
          :key="index"
          class="row q-mb-sm items-center"
        >
          <q-input
            class="q-mr-sm"
            v-model="trackingNum.num"
            style="min-width: 300px"
            dense
            outlined
            @update:model-value="trackingNum.isUpdate = true"
          />
          <q-btn
            label="Remove"
            outline
            rounded
            color="primary"
            @click="deleteTrackingNum(ticketId, index)"
          />
        </div>
        <!-- Ticket serials -->
        <TicketEditTable
          ref="editTable"
          :isFromMaster="ticketInfo.isFromMaster"
          :containsXrefMaterials="ticketInfo.containsXrefMaterials"
          :orderType="ticketInfo.typeOfRepair"
          :rows="getSerialsByTicketId(ticketId)"
          :inputValue="inputValue"
          :encrypt="ticketInfo.encrypt"
          @add-sn="handleAddSN"
          @update-sn="handleUpdateSN"
          @remove-sn="handleRemoveSN"
        />
        <!-- Button for submit ticket -->
        <div class="row justify-center">
          <q-btn
            class="col-auto"
            color="primary"
            @click="handleEditTicket"
            style="min-width: 200px"
            :loading="ticketEditing"
          >
            Submit Ticket
          </q-btn>
        </div>
      </div>
    </div>
    <div class="row justify-between">
      <div
        class="col-auto text-weight-bold text-subtitle1"
        style="text-decoration-line: underline"
      >
        Comments
      </div>
      <div class="col-2 text-weight-bold q-mb-sm q-mt-sm">
        Acknowledged:&nbsp;&nbsp;
        <input
          type="radio"
          v-model="ticketInfo.acknowledged"
          value="1"
          @update:model-value="ackComment()"
          :disabled="!ackPermission"
        />&nbsp;Yes&nbsp;&nbsp;
        <input
          type="radio"
          v-model="ticketInfo.acknowledged"
          value="0"
          @update:model-value="unackComment()"
          :disabled="!ackPermission"
        />&nbsp;No&nbsp;
      </div>
    </div>
    <MessageBoard
      :ticketId="ticketId"
      :comments="comments"
      @add-comment="addComment"
      ref="messageBoard"
    />
    <BaseModal
      :show="showAddressModal"
      title="Select Shipping Address"
      :width="972"
      @update:show="showAddressModal = false"
    >
      <AddressGrid @selectShippingAddress="selectShippingAddress" />
    </BaseModal>
  </div>
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState, mapStores } from "pinia";
import MessageBoard from "src/components/MessageBoard.vue";
import TicketEditTable from "src/components/TicketEditTable.vue";
import AddressBlock from "src/components/AddressBlock.vue";
import AddressGrid from "src/components/AddressGrid.vue";
import BaseModal from "src/components/BaseModal.vue";
import { Notify, TouchSwipe } from "quasar";
import { api } from "src/boot/axios";
import { useEditTicketStore } from "src/stores/editTicket";
import { useUserStore } from "stores/user";
import { batchSerialNumberQuery } from "src/utils/ticketUtils";

export default {
  components: {
    MessageBoard,
    TicketEditTable,
    AddressBlock,
    AddressGrid,
    BaseModal,
  },
  data: () => {
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
        serials: [],
        trackingNumbers: [],
        encrypt: null,
        testKeyType: null,
        acknowledged: null,
      },
      comments: [],
      editInfo: {
        updateTrackingNums: [],
        removeTrackingNums: [],
        removeSerials: [],
        updateSerials: [],
      },
      isLoading: false,
      ticketEditing: false,

      file: null,
      fileUploading: false,
      inputValue: null,

      showAddressModal: false,
    };
  },
  created() {
    // watch the params of the route to fetch the data again
    this.$watch(
      () => this.$route.params,
      () => {
        if (this.$route.name !== "edit-ticket") {
          return;
        }

        this.isLoading = true;
        Promise.all([
          this.getTicket(this.ticketId),
          this.fetchComments(this.ticketId),
          this.fetchAckStatus(this.ticketId),
          //ensure it been populated
        ])
          .then((values) => {
            const ticketInfo = values[0];
            const comments = values[1];
            const ack = values[2];

            if (ticketInfo != null) {
              this.ticketInfo = ticketInfo;
            }
            if (comments != null) {
              this.comments = comments;
            }
            if (ack != null) {
              this.ticketInfo.acknowledged = ack;
            }
            this.comments.forEach((c) => {
              //is the replyer == current user, mark it as green
              if (c.email == this.email) {
                c.bgColor = "bg-green-3";
              }
            });

            this.$nextTick(() => this.$refs.messageBoard.scrollToBottom());
          })
          .catch((e) => {
            console.log(e);
          })
          .finally(() => {
            this.isLoading = false;
          });
      },
      // fetch the data when the view is created and the data is
      // already being observed
      { immediate: true }
    );

    this.populateOrderTypeOptOnce();
    this.populateKeyTypeOptOnce();
  },
  mounted() {},
  computed: {
    ...mapState(useUserStore, ["email"]),
    ...mapState(useCreateTicketStore, ["orderTypeOpt", "keyTypeOpt"]),
    ...mapWritableState(useEditTicketStore, [
      "getTrackingNumsByTicketId",
      "getSerialsByTicketId",
      "getTicketbyId",
    ]),
    isEncrypted() {
      return (
        this.ticketInfo.encrypt != null && this.ticketInfo.encrypt === "yes"
      );
    },
    isReRepair() {
      return this.ticketInfo.typeOfRepair === 4;
    },
    ticketId() {
      return this.$route.params.ticketId;
    },
    address() {
      return this.ticketInfo.address;
    },
    ackPermission() {
      return useUserStore().checkPermission("ticketing.edit.acknowledge");
    },
  },
  methods: {
    ...mapActions(useCreateTicketStore, [
      "populateOrderTypeOpt",
      "populateOrderTypeOptOnce",
      "populateKeyTypeOpt",
      "populateKeyTypeOptOnce",
    ]),
    ...mapActions(useEditTicketStore, [
      "fetchTicket",
      "getTicket",
      "addTrackingNum",
      "deleteTrackingNum",
      "getEditInfo",
      "findEditTrackingNums",
      "findEditSN",
      "addSN",
      "addSnList",
      "updateSN",
      "removeSN",
      "updateAddress",
    ]),
    handleEditTicket() {
      //valid serials and update it
      this.ticketEditing = true;
      const editTracking = this.findEditTrackingNums(this.ticketId);
      const editSerial = this.findEditSN(this.ticketId);

      //If user didn't choose order type, don't allow user to submit the ticket
      if (this.ticketInfo.orderType === null) {
        this.$q.notify({
          type: "negative",
          message: "Please Select Order Type before Submitting.",
        });
        this.ticketEditing = false;
        return;
      }

      //if SN isn't found. Don't let customer submit ticket before Remove the record.
      for (const serial of this.getSerialsByTicketId(this.ticketId)) {
        if (serial.valid === false) {
          this.$q.notify({
            type: "negative",
            message: "Please Delete Invalid SN before Submiting",
          });
          this.ticketEditing = false;
          return;
        }
      }
      const payload = {
        ...editTracking,
        ...editSerial,
        isFromMaster: this.ticketInfo.isFromMaster,
        orderType: this.ticketInfo.orderType,
        address: this.ticketInfo.address,
        typeOfRepair: this.ticketInfo.typeOfRepair,
        originalRMA: this.ticketInfo.originalRMA,
        clientGroup: this.clientGroup,
        mcOID: this.ticketInfo.mcOID,
        encrypt: this.ticketInfo.encrypt,
        testKeyType: this.isEncrypted
          ? this.keyTypeOpt[this.ticketInfo.testKeyType].label
          : null,
      };
      if (payload.orderType === 3 || payload.orderType === 7) {
        payload.originalRMA = null;
      }
      const actionURL = "/ticketing/" + this.ticketId;

      api
        .put(actionURL, payload, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then(function (response) {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          Notify.create({
            type: "positive",
            message: "Update Ticket Successfully",
          });
        })
        .catch((e) => {
          Notify.create({
            type: "negative",
            message: e.message,
          });
        })
        .finally(() => {
          this.resetTicket();
          this.ticketEditing = false;
        });
    },
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
    handleAddSN({ serial }) {
      this.addSN(this.ticketId, serial);
    },
    handleUpdateSN({ oldSN, serial }) {
      this.updateSN(this.ticketId, oldSN, serial);
    },
    handleRemoveSN({ sn }) {
      this.removeSN(this.ticketId, sn);
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
          this.addSnList(this.ticketId, serials);
        })
        .finally(() => {
          this.fileUploading = false;
        });
    },
    /**
     *
     * For Message board
     */
    fetchComments(ticketId) {
      const vm = this;
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
          newComment.bgColor = "bg-green-3";
          this.comments.push(newComment);
          if (this.ackPermission) {
            //rma clerk
            this.ackComment();
            this.ticketInfo.acknowledged = 1;
          } else {
            //customer
            this.unackComment();
            this.ticketInfo.acknowledged = 0;
          }
          this.$nextTick(() => this.$refs.messageBoard.scrollToBottom());
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    ackComment() {
      //can backend to ack the ticket
      const link = `/ticketing/${this.ticketId}/acknowledged`;
      const vm = this;
      api
        .put(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    unackComment() {
      //can backend to unack the ticket
      const link = `/ticketing/${this.ticketId}/unacknowledged`;
      const vm = this;
      api
        .put(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    fetchAckStatus() {
      const link = `/ticketing/${this.ticketId}/acknowledged`;
      const vm = this;
      return api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          return response.data.data.acknowledged;
        })
        .catch((error) => {
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },
    selectShippingAddress(address) {
      this.updateAddress(address, this.ticketId);
      this.showAddressModal = false;
    },
    showAddressGrid() {
      this.showAddressModal = true;
    },
  },
};
</script>
<style></style>
