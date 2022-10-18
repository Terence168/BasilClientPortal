<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        RMA Status
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
        <div class="q-pt-md">
          <div class="row justify-center">
            <GenericTable style="max-width: 100%" :tableData="tableData" />
          </div>

          <GenericPagination :pages="totalPages" :total="total" />
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
        { id: "rma", label: "RMA Number" },
        { id: "serial", label: "Serial Number" },
        { id: "model", label: "Model Number Short" },
      ],

      tableData: {
        columns: [
          { id: "shipDate", label: "Ship Date", sortable: false },
          { id: "partNumber", label: "Part Number Short", sortable: true },
          { id: "serialNumber", label: "Serial Number", sortable: false },
          { id: "rmaNumber", label: "Ticket Number", sortable: true },
          { id: "trackingNumber", label: "Ticket Number", sortable: false },
          {
            id: "reportedIssue",
            label: "Customer Reported Issue",
            sortable: false,
          },
          { id: "faultCode", label: "Fault Code", sortable: false },
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
    $route() {
      this.queryData();
    },
  },

  methods: {
    queryData() {
      const vm = this;

      this.$api
        .get("/rma/shipped" + window.location.search)
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
