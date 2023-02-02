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
          <q-space />
          <q-btn
            label="Export to Excel"
            color="primary"
            style="width: 150px"
            :loading="exportInProgress"
            @click="excelExport"
            :disable="total == 0"
          >
            <template v-slot:loading>
              <q-spinner-hourglass class="on-left" />
              Exporting...
            </template>
          </q-btn>
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

      modalFormData: {
        name: null,
      },

      filterFields: [
        {
          id: "rmaNumber",
          label: "RMA Ticket Number",
          tooltip:
            "Enter the RMA ticket number you are searching for.\nIf searching for multiples, you can separate them with a comma.\nMust be the exact ticket number, no partials.\nExample: (159123, 236555)",
        },
        {
          id: "serialNumber",
          label: "Serial Number",
          tooltip:
            "Enter the terminal serial number you are searching for.\nIf searching for multiples, you can separate them with a comma.\nMust be the exact serial number, no partials.\nExample: (300123456, 300123457)",
        },
        {
          id: "partNumber",
          label: "Model Number Short",
          tooltip:
            "Enter the model number prefix you are searching for.\nExample: A77",
        },
        {
          id: "contact",
          label: "Customer Contact Needed",
          type: "select",
          tooltip:
            "You can use this filter to drill down to the results you would like to see\nbased on the drop-down menu options.",
        },
        { id: "customerId", label: "Customer", type: "customerSelect" },
      ],

      tableData: {
        columns: [
          { id: "quarantineDate", label: "Quarantine Date", sortable: true },
          { id: "partNumber", label: "Model Number Short", sortable: true },
          { id: "serialNumber", label: "Serial Number", sortable: true },
          { id: "rmaNumber", label: "RMA Ticket Number", sortable: true },
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
          { id: "faultCode", label: "Primary Fault Code(s)", sortable: true },
          { id: "partsNeeded", label: "Part(s) Needed", sortable: true },
          { id: "customerOrganization", label: "Customer", sortable: true },
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

    excelExport() {
      this.exportInProgress = true;
      this.$api
        .get("/rma/excel-export/quarantine" + window.location.search, {
          responseType: "blob",
        })
        .then((response) => {
          exportFile(
            "quarantine_" + date.formatDate(Date.now(), "YYYY-MM-DD") + ".xlsx",
            response.data
          );
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        })
        .finally(() => {
          this.exportInProgress = false;
        });
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style></style>
