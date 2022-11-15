<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Quarantine
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
        <div class="row items-center text-subtitle1 text-weight-medium">
          Click on a column to sort the content of the table
        </div>
        <div v-if="total !== 0" class="q-pt-md">
          <div class="row justify-center">
            <GenericTable style="max-width: 100%" :tableData="tableData" />
          </div>

          <GenericPagination :pages="totalPages" :total="total" />
        </div>
        <div
          v-else
          class="q-pt-md text-subtitle1 text-weight-medium text-grey-6 text-center"
        >
          No data found for the current customer
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FilterOptions from "src/components/FilterOptions.vue";
import GenericTable from "src/components/GenericTable.vue";
import GenericPagination from "src/components/GenericPagination.vue";

import { useUserStore } from "stores/user";

export default {
  components: { FilterOptions, GenericTable, GenericPagination },

  data() {
    return {
      showModal: false,

      modalFormOptions: {
        id: null,
        action: "",
        submitting: false,
      },

      modalFormData: {
        name: null,
      },

      filterFields: [
        { id: "rmaNumber", label: "Ticket Number" },
        { id: "serialNumber", label: "Serial Number" },
        { id: "partNumber", label: "Model Number Short" },
        { id: "contact", label: "Customer Contact Needed", type: "select" },
        { id: "customerId", label: "Customer", type: "customerSelect" },
      ],

      tableData: {
        columns: [
          { id: "quarantineDate", label: "Quarantine Date", sortable: true },
          { id: "partNumber", label: "Model Number Short", sortable: true },
          { id: "serialNumber", label: "Serial Number", sortable: true },
          { id: "rmaNumber", label: "Ticket Number", sortable: true },
          {
            id: "customerContact",
            label: "Customer Contact Needed",
            sortable: true,
          },
          {
            id: "techNotes",
            label: "Tech Notes",
            sortable: true,
          },
          { id: "faultCode", label: "Fault Code(s)", sortable: true },
          { id: "partsNeeded", label: "Part(s) Needed", sortable: true },
        ],
        rows: [],
      },
      total: 0,
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
        .get("/rma/quarantine" + window.location.search)
        .then(function (response) {
          vm.tableData.rows = response.data.data;
          vm.total = response.data.total;
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
