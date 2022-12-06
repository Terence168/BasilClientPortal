<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        General Information
      </div>

      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="text-subtitle1 text-weight-medium">
          Search within the table
        </div>
        <FilterOptions :filterFields="filterFields" />
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg">
        <div class="row items-center text-subtitle1 text-weight-medium q-mb-md">
          Click on model item to see more details
        </div>

        <div
          v-if="partSummary && partSummary.length > 0"
          class="row"
          style="
            max-width: 1100px;
            margin: 0 auto;
            position: sticky;
            top: 0;
            background-color: white;
            z-index: 1000;
          "
        >
          <div class="col-3"></div>
          <div class="col-9">
            <div class="row shadow-2 text-body1 text-center q-py-xs">
              <div class="col-2">
                <span>Received</span>
                <q-tooltip
                  class="bg-primary text-body2 shadow-4"
                  max-width="500px"
                  :offset="[10, 10]"
                >
                  The terminal has been received, checked in, and placed in the
                  repair queue awaiting repair.
                </q-tooltip>
              </div>
              <div class="col-2">
                <span>Out for Repair</span>
                <q-tooltip
                  class="bg-primary text-body2 shadow-4"
                  max-width="500px"
                  :offset="[10, 10]"
                >
                  The terminal is assigned to a repair technician to evaluate
                  and complete the repair.
                </q-tooltip>
              </div>
              <div class="col-2">
                <span>Quarantine</span>
                <q-tooltip
                  class="bg-primary text-body2 shadow-4"
                  max-width="500px"
                  :offset="[10, 10]"
                >
                  The terminal has been placed in a secure location awaiting
                  part(s) that are not in stock at this time.
                </q-tooltip>
              </div>
              <div class="col-2">
                <span>Awaiting QA/CA</span>
                <q-tooltip
                  class="bg-primary text-body2 shadow-4"
                  max-width="500px"
                  :offset="[10, 10]"
                >
                  The terminal repair has been completed and awaiting the final
                  inspection by our Quality Assurance/Certification
                  Authorization team before being approved for shipping.
                </q-tooltip>
              </div>
              <div class="col-2">
                <span>Ready to Ship</span>
                <q-tooltip
                  class="bg-primary text-body2 shadow-4"
                  max-width="500px"
                  :offset="[10, 10]"
                >
                  The terminal has passed QA/CA inspection and is awaiting a
                  shipping label and/or UPS to pick it up.
                </q-tooltip>
              </div>
              <div class="col-2 text-weight-bold">Total</div>
            </div>
          </div>

          <div
            class="col-3 text-body1 text-right q-py-xs q-pr-sm text-weight-bold"
          >
            Summary
          </div>
          <div class="col-9">
            <div class="row shadow-2 bg-grey-3 text-body1 text-center q-py-xs">
              <div class="col-2">{{ summary.inventory }}</div>
              <div class="col-2">{{ summary.outForRepair }}</div>
              <div class="col-2">{{ summary.quarantine }}</div>
              <div class="col-2">{{ summary.awaitingQaCa }}</div>
              <div class="col-2">{{ summary.readyToShip }}</div>
              <div class="col-2 text-weight-bold">{{ summary.total }}</div>
            </div>
          </div>
        </div>

        <div
          v-else
          class="q-pt-md text-subtitle1 text-weight-medium text-grey-6 text-center"
        >
          No data found for the current customer
        </div>

        <StatusBox
          v-for="part in partSummary"
          :key="part.partNumber + '-' + Date.now()"
          category="partNumber"
          :level="1"
          :data="part"
        />
      </div>
    </div>
  </div>
</template>

<script>
import FilterOptions from "src/components/FilterOptions.vue";

import { useUserStore } from "stores/user";
import StatusBox from "src/components/StatusBox.vue";

export default {
  components: { FilterOptions, StatusBox },

  data() {
    return {
      showModal: false,

      modalFormOptions: {
        id: null,
        action: "",
        submitting: false,
      },

      partSummary: [],

      filterFields: [
        {
          id: "partNumber",
          label: "Model Number Short",
          tooltip:
            "Enter the model number prefix you are searching for.\nExample: A77",
        },
        {
          id: "rmaNumber",
          label: "Ticket Number",
          tooltip:
            "Enter the RMA ticket number you are searching for.\nMust be an exact number, no partials.",
        },
        {
          id: "serialNumber",
          label: "Serial Number",
          tooltip:
            "Enter the terminal serial number you are searching for.\nIf searching for multiples, you can separate them with a comma.\nMust be the exact serial number, no partials.\nExample: (300123456, 300123457)",
        },
        { id: "customerId", label: "Customer", type: "customerSelect" },
      ],
    };
  },

  computed: {
    totalPages() {
      const perPage = this.$route.query.per_page || 10;
      return Math.ceil(this.total / perPage);
    },

    summary() {
      const initSummary = {
        inventory: 0,
        quarantine: 0,
        outForRepair: 0,
        awaitingQaCa: 0,
        readyToShip: 0,
        total: 0,
      };

      return this.partSummary.reduce(
        (sum, el) => ({
          inventory: sum.inventory + el.inventory,
          quarantine: sum.quarantine + el.quarantine,
          outForRepair: sum.outForRepair + el.outForRepair,
          awaitingQaCa: sum.awaitingQaCa + el.awaitingQaCa,
          readyToShip: sum.readyToShip + el.readyToShip,
          total: sum.total + el.total,
        }),
        initSummary
      );
    },
  },

  created() {
    this.queryData();
  },

  watch: {
    $route(newRoute, oldRoute) {
      if (newRoute.path === oldRoute.path) {
        this.queryData();
      }
    },
  },

  methods: {
    queryData() {
      const vm = this;

      this.$api
        .get("/rma/status/tier1" + window.location.search)
        .then(function (response) {
          vm.partSummary = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style></style>
