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
          v-if="partSummary.length > 0"
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
              <div class="col-2">Inventory</div>
              <div class="col-2">Out for Repair</div>
              <div class="col-2">Quarantine</div>
              <div class="col-2">Awaiting QA/CA</div>
              <div class="col-2">Ready to Ship</div>
              <div class="col-2 text-weight-bold">Total</div>
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
        { id: "partNumber", label: "Model Number Short" },
        { id: "rmaNumber", label: "Ticket Number" },
        { id: "serialNumber", label: "Serial Number" },
        { id: "customerId", label: "Customer", type: "customerSelect" },
      ],
    };
  },

  computed: {
    totalPages() {
      const perPage = this.$route.query.per_page || 10;
      return Math.ceil(this.total / perPage);
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
