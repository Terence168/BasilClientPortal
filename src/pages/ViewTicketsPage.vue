<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        View Tickets
      </div>

      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="row">
          <q-btn
            :to="{ name: 'create-ticket' }"
            class="q-mb-md"
            color="primary"
            label="Create a New Ticket"
          />
        </div>
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
          No data found
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

import { exportFile, date } from "quasar";

export default {
  components: { FilterOptions, GenericTable, GenericPagination },

  data() {
    return {
      filterFields: [
        { id: "ticketId", label: "Ticket ID" },
        { id: "department", label: "Department", type: "select" },
        { id: "responder", label: "Responder" },
        { id: "status", label: "Status", type: "select" },
        { id: "type", label: "Type", type: "select" },
        {
          id: "createdDate",
          label: "Created Date",
          type: "dateRange",
        },
        {
          id: "serialNumber",
          label: "Serial Number",
        },
        { id: "customerId", label: "Customer", type: "customerSelect" },
      ],

      tableData: {
        columns: [
          { id: "ticketId", label: "TICKET ID", sortable: true },
          { id: "status", label: "Status", sortable: true },
          { id: "department", label: "Department", sortable: true },
          { id: "type", label: "Type", sortable: true },
          //   {
          //     id: "customer",
          //     label: "Customer",
          //     sortable: true,
          //   },
          {
            id: "createdDate",
            label: "Created Date",
            sortable: true,
          },
          { id: "responder", label: "Responder", sortable: true },
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
    queryData() {},

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style></style>
