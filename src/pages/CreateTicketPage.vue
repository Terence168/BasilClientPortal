<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Create Ticket
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg text-body1">
        <div class="row q-mb-md text-weight-medium">
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
            <q-input style="min-width: 200px" dense />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Customer Organization:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="companyName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Customer Email:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="userEmail"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row items-center">
          <div class="col-auto q-mr-sm">Ticket Submitter:&nbsp;</div>
          <div class="col-auto">
            <q-input
              :model-value="userName"
              disable
              style="min-width: 200px"
              dense
            />
          </div>
        </div>

        <div class="row q-my-sm items-center">
          <div class="col-auto q-mr-sm">Incoming Tracking Number:&nbsp;</div>
          <div class="col">
            <q-btn
              label="Add"
              outline
              rounded
              color="primary"
              @click="addTrackingNum"
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
            @click="deleteTrackingNum(index)"
          />
        </div>

        <div class="q-py-md text-subtitle1 text-weight-bold">
          Ticket Serial Numbers
        </div>

        <div class="row items-start">
          <q-btn class="col-auto" color="primary">Add Serial Number</q-btn>
          <div style="margin-top: 6px" class="q-mx-sm">AND / OR</div>
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
              >
                <template v-slot:loading>
                  <q-spinner-facebook />
                </template>
              </q-btn>
            </div>
          </q-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";
import { useCreateTicketStore } from "stores/createTicket";
import { mapWritableState, mapActions } from "pinia";
import { mapState } from "pinia";

const user = useUserStore();

export default {
  components: {},

  data() {
    return {
      file: null,
    };
  },

  computed: {
    ...mapWritableState(useCreateTicketStore, ["orderType", "trackingNums"]),

    ...mapState(useCreateTicketStore, ["orderTypeOpt"]),

    isReRepair() {
      return this.orderType === 4;
    },

    userName() {
      return user.username || "Guest";
    },

    userEmail() {
      return user.email || "N/A";
    },

    companyName() {
      return user.companyName || "";
    },
  },

  created() {},

  methods: {
    ...mapActions(useCreateTicketStore, [
      "addTrackingNum",
      "deleteTrackingNum",
      "resetTicket",
      "populateOrderTypeOpt",
    ]),
  },
};
</script>

<style></style>
