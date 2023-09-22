<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Role Type
      </div>

      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="text-subtitle1 text-weight-medium">Manage Role Types</div>
        <FilterOptions :filterFields="filterFields" />
      </div>
    </div>

    <div class="q-mt-lg generic-container">
      <div class="q-px-lg q-pt-md q-mb-md q-pb-lg">
        <div class="row items-center text-subtitle1 text-weight-medium">
          Click on a column to sort the content of the table
          <q-space />
          <q-btn
            v-if="checkPermission('privilege.role-type.add')"
            icon="add"
            label="Add"
            color="primary"
            style="width: 100px"
            @click="addData"
          />
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

    <BaseModal
      v-model:show="showModal"
      :title="`${modalFormOptions.action} Role Type`"
      :width="500"
    >
      <q-form ref="modalForm" @submit="onSubmit(modalFormOptions.id)">
        <q-input
          class="col"
          outlined
          v-model="modalFormData.name"
          label="Role Type Name"
          :readonly="modalFormOptions.action === 'View'"
          dense
        />

        <div class="row justify-center q-mt-md">
          <div v-if="modalFormOptions.action !== 'View'" class="col-auto">
            <q-btn
              class="q-mr-md"
              type="submit"
              :loading="modalFormOptions.submitting"
              label="Submit"
              color="primary"
              style="min-width: 150px"
            >
              <template v-slot:loading>
                <q-spinner-facebook />
              </template>
            </q-btn>
          </div>
          <div class="col-auto">
            <q-btn
              :disable="modalFormOptions.submitting"
              label="Cancel"
              color="grey-4"
              text-color="grey-6"
              style="min-width: 150px"
              @click="showModal = false"
            />
          </div>
        </div>
      </q-form>
    </BaseModal>
  </div>
</template>

<script>
import FilterOptions from "src/components/FilterOptions.vue";
import GenericTable from "src/components/GenericTable.vue";
import GenericPagination from "src/components/GenericPagination.vue";
import BaseModal from "src/components/BaseModal.vue";

import { useUserStore } from "stores/user";

export default {
  components: { FilterOptions, GenericTable, GenericPagination, BaseModal },
  mounted(){
    if(this.checkPermission("privilege.role-type.update")){
      this.tableData.columns= [
          { id: "roleType", label: "Role Type", sortable: true },
          { id: "actions", label: "Actions", sortable: false },
        ];
    }
    else{
      this.tableData.columns=[
          { id: "roleType", label: "Role Type", sortable: true }];
    }
  },
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

      filterFields: [{ id: "roleType", label: "Role Type" }],

      tableData: {
        columns: [
          { id: "roleType", label: "Role Type", sortable: true },
          { id: "actions", label: "Actions", sortable: false },
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

      return { view: false, update: true };
      // if (view || update) {
      //   return { view, update };
      // }

      // return null;
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
        .get("/privilege/role-type/query" + window.location.search)
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
        actionURL = "/privilege/role-type/add";
      else if (this.modalFormOptions.action === "Update")
        actionURL = "/privilege/role-type/update";
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
