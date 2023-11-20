<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Sales Order Status
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
        <q-tab-panels v-model="panel" animated>
          <q-tab-panel name="salesOrderTable">
            <div v-if="total !== 0" class="q-pt-md">
              <div class="row justify-center">
                <GenericTable
                  @sales-order-details="salesOrderDetails"
                  style="max-width: 100%"
                  :tableData="tableData"
                />
              </div>

              <GenericPagination :pages="totalPages" :total="total" />
            </div>
            <div
              v-else
              class="q-pt-md text-subtitle1 text-weight-medium text-grey-6 text-center"
            >
              No data found for the current customer
            </div>
          </q-tab-panel>

          <q-tab-panel name="salesOrderLinePanel">
            <q-table
              :rows="salesOrderDetailData"
              :columns="columns"
              :loading="salesOrderDetailData.length === 0"
              row-key="salesOrderLine"
            >
              <template v-slot:top-left>
                <div class="row items-center">
                  <q-btn
                    @click="backToMainTable"
                    round
                    color="primary"
                    icon="arrow_back"
                  />
                  <div class="q-ml-md q-table__title">
                    {{ salesOrderLineTitle }}
                  </div>
                </div>
              </template>
            </q-table>
          </q-tab-panel>
        </q-tab-panels>
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

      panel: "salesOrderTable",

      selectedSalesOrder: null,

      columns: [
        {
          id: "salesOrderLine",
          label: "Line",
          field: "salesOrderLine",
          sortable: true,
        },
        {
          id: "materialNumber",
          label: "Material Number",
          field: "materialNumber",
          sortable: false,
        },
        {
          id: "productClass",
          label: "Product Class",
          field: "productClass",
          sortable: false,
        },
        {
          id: "stockDescription",
          label: "Stock Description",
          field: "stockDescription",
          sortable: false,
        },
        {
          id: "soLineShipDate",
          label: "Ship Date",
          field: "soLineShipDate",
          sortable: false,
        },
        {
          id: "comment",
          label: "Comment",
          field: "comment",
          sortable: false,
        },
        {
          id: "lineType",
          label: "Line Type",
          field: "lineType",
          sortable: false,
        },
        {
          id: "documentType",
          label: "Document Type",
          field: "documentType",
          sortable: false,
        },
        {
          id: "orderQty",
          label: "Order Qty",
          field: "orderQty",
          sortable: false,
        },
        {
          id: "shippedQty",
          label: "Shipped Qty",
          field: "shippedQty",
          sortable: false,
        },
        {
          id: "backOrderQty",
          label: "Back Order Qty",
          field: "backOrderQty",
          sortable: false,
        },
        {
          id: "lastRefresh",
          label: "Last Refresh",
          field: "lastRefresh",
          sortable: false,
        },
      ],

      salesOrderDetailData: [],

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
          id: "salesOrder",
          label: "Sales Order Number",
          tooltip:
            "Enter Sales Order Number you are searching for.\nIf searching for multiples, you can separate them with a comma.\nMust be the exact Sales Order Number, no partials.\nExample: (159123, 236555)",
        },
        {
          id: "shipDate",
          label: "Ship Date",
          type: "dateRange",
          tooltip:
            "The date the order is expected to be shipped to its assigned destination.\nDate Range: To view a date range, click the calendar icon.\nClick on the first date you want your date range to start, then click on the date you want it to end.\nOnce you have selected your date range, click “SELECT RANGE” at the bottom of the calendar.\nThen click the SEARCH button for the query to run.\n\nSingle Date: Click on the calendar icon in the field and select the date on the calendar.\nClick the date twice and click “SELECT RANGE” at the bottom of the calendar.\nThen click the SEARCH button for the query to run.",
        },
        {
          id: "createDate",
          label: "Created Date",
          type: "dateRange",
          tooltip:
            "The date the order was created.\nDate Range: To view a date range, click the calendar icon.\nClick on the first date you want your date range to start, then click on the date you want it to end.\nOnce you have selected your date range, click “SELECT RANGE” at the bottom of the calendar.\nThen click the SEARCH button for the query to run.\n\nSingle Date: Click on the calendar icon in the field and select the date on the calendar.\nClick the date twice and click “SELECT RANGE” at the bottom of the calendar.\nThen click the SEARCH button for the query to run.",
        },
        {
          id: "customerId",
          label: "Customer",
          type: "customerSelect",
        },
      ],

      tableData: {
        columns: [
          { id: "salesOrderDetails", label: "", sortable: false },
          { id: "salesOrder", label: "Sales Order", sortable: false },
          { id: "customer", label: "Customer", sortable: false },
          { id: "orderStatus", label: "Order Status", sortable: false },
          { id: "orderDate", label: "Order Date", sortable: false },
          {
            id: "customerPONumber",
            label: "PO Number",
            sortable: false,
          },
          { id: "description", label: "Description", sortable: false },
          {
            id: "specialInstructions",
            label: "Special Instructions",
            sortable: false,
          },
          { id: "salesPerson", label: "Sales Person", sortable: false },
          { id: "contact", label: "Contact", sortable: false },
          {
            id: "sysproCustomerName",
            label: "Syspro Customer Name",
            sortable: false,
          },
          { id: "shipAddress", label: "Ship Address", sortable: false },
          { id: "reqShipDate", label: "Requested Ship Date", sortable: false },
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

    salesOrderLineTitle() {
      return `Sales Order #${this.selectedSalesOrder} Details`;
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
        .get("/sales-order" + window.location.search)
        .then(function (response) {
          vm.tableData.rows = response.data.data.salesOrders;
          vm.total = response.data.data.total;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    excelExport() {
      this.exportInProgress = true;
      this.$api
        .get("/sales-order/excel-export" + window.location.search, {
          responseType: "blob",
        })
        .then((response) => {
          exportFile(
            "sales-order-" +
              date.formatDate(Date.now(), "YYYY-MM-DD") +
              ".xlsx",
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

    salesOrderDetails(id) {
      this.panel = "salesOrderLinePanel";

      this.selectedSalesOrder = id;

      const vm = this;

      this.$api
        .get(`/sales-order/${id}`)
        .then(function (response) {
          vm.salesOrderDetailData = response.data.data.salesOrderDetails;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    backToMainTable() {
      this.panel = "salesOrderTable";
      this.selectedSalesOrder = null;
      this.salesOrderDetailData = [];
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style></style>
