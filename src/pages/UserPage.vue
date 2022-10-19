<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        User
      </div>

      <q-separator />

      <div class="q-px-lg q-py-md">
        <div class="row space-between items-center">
          <div class="col">
            <div class="text-subtitle1 text-weight-medium">
              General Information
            </div>
          </div>
        </div>

        <div class="q-mt-md">
          <div class="row justify-center">
            <div class="col-4">
              <q-card>
                <q-card-section>
                  <div class="text-body2 text-weight-medium q-mb-sm">
                    User Summary
                  </div>

                  <q-separator />

                  <div
                    class="row justify-center items-center"
                    style="height: 130px"
                  >
                    <div class="col text-center">
                      <div class="text-h4 text-weight-medium text-primary">
                        {{ total }}
                      </div>
                      <div class="text-body2 text-grey-6">Active Users</div>
                    </div>

                    <!-- <div class="col text-center">
                      <div class="text-h4 text-weight-medium text-primary">
                        {{ summary.disabledUsers }}
                      </div>
                      <div class="text-body2 text-grey-6">
                        Disabled Users
                      </div>
                    </div> -->
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="q-mt-lg generic-container">
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
              @update-data="updateData"
              style="width: 1000px"
              :tableData="tableData"
            />
          </div>

          <GenericPagination :pages="totalPages" :total="total" />
        </div>
      </div>
    </div>

    <BaseModal
      v-model:show="showModal"
      :title="`${modalFormOptions.action} User`"
      :width="600"
    >
      <q-form ref="modalForm" @submit="onSubmit(modalFormOptions.id)">
        <q-input
          class="q-mb-sm"
          outlined
          v-model="modalFormData.userName"
          label="First and Last Name"
          :readonly="modalFormOptions.action === 'View'"
          dense
        />

        <q-input
          class="q-mb-sm"
          outlined
          v-model="modalFormData.email"
          label="Pax Email"
          :readonly="modalFormOptions.action === 'View'"
          dense
        />

        <q-select
          class="q-mb-sm"
          outlined
          :options="division"
          v-model="modalFormData.division"
          label="Division"
          :readonly="modalFormOptions.action === 'View'"
          map-options
          emit-value
          dense
        />

        <q-select
          class="q-mb-sm"
          outlined
          :options="title"
          v-model="modalFormData.title"
          label="Title"
          :readonly="modalFormOptions.action === 'View'"
          map-options
          emit-value
          dense
        />

        <q-select
          class="q-mb-sm"
          outlined
          :options="status"
          v-model="modalFormData.employeeStatus"
          label="Employee Status"
          :readonly="modalFormOptions.action === 'View'"
          map-options
          emit-value
          dense
        />

        <q-input
          class="q-mb-sm"
          outlined
          v-model.number="modalFormData.burdenRate"
          mask="#.##"
          reverse-fill-mask
          label="Burden Rate"
          :readonly="modalFormOptions.action === 'View'"
          dense
        />

        <div class="q-mb-sm text-weight-bold">Assign Roles</div>

        <div class="q-mb-sm">
          <q-list bordered class="rounded-borders">
            <q-expansion-item
              v-for="roleType in filteredRoles"
              :key="roleType.roleTypeName"
              class="text-subtitle1 text-weight-medium"
              expand-separator
              icon="perm_identity"
              :label="roleType.roleTypeName"
            >
              <q-card class="text-body2 text-weight-regular">
                <q-card-section>
                  <div
                    v-for="role in roleType.roles"
                    :key="roleType.roleTypeName + '-' + role.id"
                    class="q-mb-sm"
                  >
                    <q-checkbox
                      v-model="rolesSelected"
                      :val="role.id"
                      :label="role.roleName"
                    />
                  </div>
                </q-card-section>
              </q-card>
            </q-expansion-item>
          </q-list>
        </div>

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
import GenericTable from "src/components/GenericTable.vue";
import GenericPagination from "src/components/GenericPagination.vue";
import BaseModal from "src/components/BaseModal.vue";
import FilterOptions from "src/components/FilterOptions.vue";

export default {
  components: { FilterOptions, GenericTable, GenericPagination, BaseModal },

  data() {
    return {
      showModal: false,

      modalFormOptions: {
        id: null,
        action: "",
        submitting: false,
      },

      modalFormData: {
        userName: null,
        email: null,
        division: null,
        title: null,
        employeeStatus: null,
        burdenRate: null,
      },

      allRoles: [],

      rolesSelected: [],

      division: [],

      title: [],

      status: [],

      filterFields: [
        { id: "userName", label: "User Name" },
        { id: "email", label: "Email" },
        { id: "lastLogin", label: "Last Login", type: "dateRange" },
      ],

      tableData: {
        columns: [
          { id: "user", label: "User Name", sortable: true },
          { id: "email", label: "Email", sortable: true },
          { id: "registerTime", label: "Register Time", sortable: true },
          { id: "lastLogin", label: "Last Login", sortable: true },
          { id: "statusStr", label: "User Status", sortable: true },

          { id: "userActions", label: "Actions", sortable: false },
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

    filteredRoles() {
      return this.allRoles.filter((roleType) => roleType.roles.length > 0);
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
        .get("privilege/user/query" + window.location.search)
        .then(function (response) {
          vm.tableData.rows = response.data.data;
          vm.total = response.data.total;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    populateFields(id) {
      this.modalFormOptions.id = id;

      const link = "/basil/privilege/user/view/query?id=" + id;

      this.$api
        .get(link)
        .then((response) => {
          const userData = response.data.data[0];
          this.modalFormData.userName = userData.userName;
          this.modalFormData.email = userData.email;
          this.modalFormData.division = userData.division;
          this.modalFormData.title = userData.title;
          this.modalFormData.employeeStatus = userData.employeeStatus;
          this.modalFormData.burdenRate = userData.burdenRate;
          this.rolesSelected = userData.roles;
        })
        .then(() => {
          this.showModal = true;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    updateData(id) {
      this.modalFormOptions.action = "Update";

      this.populateFields(id);
    },

    onSubmit(id) {
      if (this.modalFormOptions.action === "View") return;

      let actionURL;
      if (this.modalFormOptions.action === "Add")
        actionURL = "/basil/privilege/user/add";
      else if (this.modalFormOptions.action === "Update")
        actionURL = "/basil/privilege/user/update";
      else console.log("Should not be here :(");

      const vm = this;

      this.$api
        .post(actionURL, { id, ...this.modalFormData, roles: vm.rolesSelected })
        .then(function (response) {
          vm.showModal = false;
          vm.queryData();
        });
    },

    addData() {
      this.modalFormOptions.action = "Add";
      this.modalFormOptions.id = null;

      Object.keys(this.modalFormData).forEach((prop) => {
        this.modalFormData[prop] = null;
      });

      this.rolesSelected = [];

      this.showModal = true;
    },

    populateRoles() {
      const vm = this;

      this.$api
        .get("/basil/privilege/user/all-roles")
        .then(function (response) {
          vm.allRoles = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    populateDivisionDropdown() {
      const link = "/basil/privilege/division/drop-down";
      return this.$api
        .get(link)
        .then((response) => {
          this.division = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    populateTitleDropdown() {
      const link = "/basil/privilege/title/drop-down";
      return this.$api
        .get(link)
        .then((response) => {
          this.title = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    populateStatusDropdown() {
      const link = "/basil/privilege/status/drop-down";
      return this.$api
        .get(link)
        .then((response) => {
          this.status = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },
  },
};
</script>

<style lang="sass">
.part-container
  border: 1px solid $grey-4
  border-radius: 20px
.unit-item-container
  border: 2px solid $grey-6
  border-radius: 20px
  padding-left: 20px
  padding-right: 20px
  padding-bottom: 20px
  margin-top: 20px
</style>
