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
            <GenericTable
              @detailed-view="detailedView"
              @update-data="updateData"
              style="width: 400px"
              :tableData="tableData"
              :permissions="permissions"
            />
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
          { id: "rmaNumber", label: "Ticket Number", sortable: true },
          { id: "rmaNumber", label: "Ticket Number", sortable: true },
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

    permissions() {
      const view = this.checkPermission("privilege.role-type.view");
      const update = this.checkPermission("privilege.role-type.update");

      if (view || update) {
        return { view, update };
      }

      return null;
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
        .get("/basil/privilege/role-type/query" + window.location.search)
        .then(function (response) {
          vm.tableData.rows = response.data.data;
          vm.total = response.data.total;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    onSubmit(id) {
      if (this.modalFormOptions.action === "View") return;

      let actionURL;
      if (this.modalFormOptions.action === "Add")
        actionURL = "/basil/privilege/role-type/add";
      else if (this.modalFormOptions.action === "Update")
        actionURL = "/basil/privilege/role-type/update";
      else console.log("Should not be here :(");

      const vm = this;
      this.$api
        .post(actionURL, { id, ...this.modalFormData })
        .then(function (response) {
          console.log(response);
          vm.showModal = false;
          vm.queryData();
        });
    },

    pickData(id) {
      return this.tableData.rows.find((row) => row.id === id);
    },

    populateFields(id) {
      this.modalFormOptions.id = id;
      const originalData = this.pickData(id);

      console.log(originalData);

      this.modalFormData.name = originalData.roleType;

      this.showModal = true;
    },

    addData() {
      this.modalFormOptions.action = "Add";
      this.modalFormOptions.id = null;

      this.modalFormData.name = null;

      this.showModal = true;
    },

    detailedView(id) {
      this.modalFormOptions.action = "View";

      this.populateFields(id);
    },

    updateData(id) {
      this.modalFormOptions.action = "Update";

      this.populateFields(id);
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>

<style></style>
