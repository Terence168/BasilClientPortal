<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Ticketing Queue
      </div>

      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="text-subtitle1 text-weight-medium">
          Search within the table
        </div>
        <FilterOptions
          :filterFields="filterFields"
          @submitted="onSearchSubmitted"
          @reset="onSearchReset"
        />
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg">
        <div class="row items-center text-subtitle1 text-weight-medium">
          Click on a column to sort the content of the table
          <q-space />
          <div class="text-red">{{ total }} Open tickets</div>
        </div>
        <div class="row q-gutter-sm q-mt-sm">
          <q-btn
            :color="actionFilter === 1 ? 'primary' : 'grey-5'"
            :text-color="actionFilter === 1 ? 'white' : 'dark'"
            unelevated
            label="RMA Action Required"
            @click="setActionFilter(1)"
          />
          <q-btn
            :color="actionFilter === 2 ? 'primary' : 'grey-5'"
            :text-color="actionFilter === 2 ? 'white' : 'dark'"
            unelevated
            label="Customer Action Required"
            @click="setActionFilter(2)"
          />
        </div>
        <div v-if="total !== 0" class="q-pt-md">
          <div class="row justify-center">
            <GenericTable
              style="max-width: 100%"
              :tableData="tableData"
              @row-action="handleRowAction"
            />
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
      showModal: false,

      exportInProgress: false,

      modalFormOptions: {
        id: null,
        action: "",
        submitting: false,
      },

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
          {
            id: "customer",
            label: "Customer",
            sortable: true,
          },
          {
            id: "createdDate",
            label: "Created Date",
            sortable: true,
          },
          { id: "responder", label: "Responder", sortable: true },
          { id: "action", label: "Action", sortable: false },
        ],
        rows: [],
      },
      total: 0,
      actionFilter: 0,
      searchSubmitted: false,
    };
  },

  computed: {
    totalPages() {
      const perPage = this.$route.query.per_page || 10;
      return Math.ceil(this.total / perPage);
    },
  },

  created() {
    this.normalizeSearchSubmittedQuery();
    this.syncActionFilterFromRoute();
    const queryWasNormalized = this.normalizeDefaultTypeQuery();
    if (!queryWasNormalized) {
      this.queryData();
    }
  },

  watch: {
    $route(newRoute, oldRoute) {
      if (newRoute.path === oldRoute.path) {
        this.syncActionFilterFromRoute();
        this.queryData();
      }
    },
  },

  methods: {
    onSearchSubmitted() {
      this.searchSubmitted = true;
    },
    onSearchReset() {
      this.searchSubmitted = false;
    },
    normalizeSearchSubmittedQuery() {
      if (this.$route.query?.searchSubmitted == null) {
        return;
      }
      const query = { ...this.$route.query };
      delete query.searchSubmitted;
      this.$router.replace({ path: this.$route.path, query });
    },
    syncActionFilterFromRoute() {
      const acknowledged = Number(this.$route.query?.acknowledged);
      this.actionFilter = acknowledged === 1 || acknowledged === 2 ? acknowledged : 0;
    },
    hasQueueFiltersExceptType() {
      const filterKeys = [
        "ticketId",
        "department",
        "responder",
        "status",
        "createdDate",
        "serialNumber",
        "customerId",
      ];
      return filterKeys.some((key) => {
        const value = this.$route.query[key];
        return value != null && String(value).trim() !== "";
      });
    },
    normalizeDefaultTypeQuery() {
      const typeValue = this.$route.query?.type;
      if (String(typeValue) !== "3") {
        return false;
      }
      if (this.hasQueueFiltersExceptType()) {
        return false;
      }

      const query = { ...this.$route.query };
      delete query.type;
      this.$router.replace({ path: this.$route.path, query });
      return true;
    },
    setActionFilter(filterValue) {
      const query = { ...this.$route.query };
      if (this.actionFilter === filterValue) {
        delete query.acknowledged;
      } else {
        query.acknowledged = String(filterValue);
      }
      this.$router.push({
        path: this.$route.path,
        query,
      });
    },
    toActionLabel(acknowledged) {
      if (Number(acknowledged) === 1) {
        return "RMA";
      }
      if (Number(acknowledged) === 2) {
        return "Customer";
      }
      return "";
    },
    handleRowAction(row) {
      if (Number(row?.acknowledged) !== 1) {
        return;
      }
      this.$router.push({
        name: "edit-ticket",
        params: { ticketId: row.ticketId },
      });
    },
    queryData() {
      const vm = this;
      const params = { ...this.$route.query };
      delete params.searchSubmitted;
      if (this.searchSubmitted) {
        params.searchSubmitted = 1;
      }

      this.$api
        .get("/ticketing/queue", { params })
        .then(function (response) {
          const rows = Array.isArray(response?.data?.data)
            ? response.data.data
            : [];
          const mappedRows = rows.map((row) => {
            const acknowledged = Number(row.acknowledged);
            return {
              ...row,
              acknowledged,
              action: vm.toActionLabel(acknowledged),
              actionClickable: acknowledged === 1,
            };
          });
          vm.tableData.rows = mappedRows;
          vm.total = Number(response?.data?.total || 0);
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
