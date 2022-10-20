<template>
  <div class="q-mx-lg">
    <div class="generic-container">
      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Role
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
          <div class="row justify-center q-mb-md">
            <div class="col-8">
              <q-card>
                <q-card-section>
                  <div class="text-body2 text-weight-medium q-mb-sm">
                    Number of Users by Roles
                  </div>

                  <q-separator />

                  <div
                    class="row justify-center items-center"
                    style="height: 130px"
                  >
                    <div
                      v-for="role in roles"
                      :key="role.name"
                      class="col text-center"
                    >
                      <div class="text-h4 text-weight-medium text-primary">
                        {{ role.users.length }}
                      </div>
                      <div class="text-body2 text-grey-6">
                        {{ role.name }}
                      </div>
                    </div>
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </div>
          <q-separator />
          <div class="row justify-end q-mt-md">
            <q-btn
              icon="add"
              label="Add"
              color="primary"
              style="width: 100px"
              @click="addData"
            />
          </div>
        </div>
      </div>
    </div>

    <BaseModal
      v-model:show="showModal"
      :title="`${modalFormOptions.action} Role`"
      :width="600"
    >
      <q-form ref="modalForm" @submit="onSubmit(modalFormOptions.id)">
        <q-input
          class="col q-mb-sm"
          outlined
          v-model="modalFormData.roleName"
          label="Role Name"
          :readonly="modalFormOptions.action === 'View'"
          dense
        />

        <q-select
          class="col q-mb-sm"
          outlined
          v-model="modalFormData.roleTypeID"
          :options="roleTypes"
          label="Role Type"
          :readonly="modalFormOptions.action === 'View'"
          emit-value
          map-options
          option-value="id"
          option-label="roleType"
          dense
        />

        <div class="col q-mb-sm text-weight-bold">Privileges</div>

        <div class="row">
          <div class="col">
            <q-tree
              class="col-12 col-sm-6"
              :nodes="permissions"
              node-key="id"
              tick-strategy="leaf"
              v-model:ticked="ticked"
            />
          </div>
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

    <div class="q-my-md q-ml-sm text-subtitle1 text-weight-medium">
      Active Users by Role
    </div>

    <div class="row q-col-gutter-md">
      <div v-for="role in roles" :key="`role_${role.id}`" class="col-4">
        <div class="generic-container">
          <RoleCard
            @update-data="updateData"
            :id="role.id"
            :title="role.name"
            :role="kebabRole(role.name)"
            :users="role.users"
          />
        </div>
      </div>
      <!-- <div class="col-4">
        <div class="generic-container">
          <RoleCard title="Administrators" role="admin" />
        </div>
      </div>
      <div class="col-4">
        <div class="generic-container">
          <RoleCard title="Technicians" role="technician" />
        </div>
      </div>
      <div class="col-4">
        <div class="generic-container">
          <RoleCard title="QA/CA Technicians" role="qacaTech" />
        </div>
      </div>
      <div class="col-4">
        <div class="generic-container">
          <RoleCard title="Spare Parts Clerk" role="sparePartClerk" />
        </div>
      </div> -->
    </div>
  </div>
</template>

<script>
import RoleCard from "src/components/RoleCard.vue";
import BaseModal from "src/components/BaseModal.vue";

import { kebabCase } from "lodash";

import { useUserStore } from "stores/user";

export default {
  components: { RoleCard, BaseModal },

  data() {
    return {
      showModal: false,

      modalFormOptions: {
        id: null,
        action: "",
        submitting: false,
      },

      modalFormData: {
        roleName: null,
        roleTypeID: null,
      },

      summary: {
        adminTotal: 4,
        techTotal: 16,
        qacaTotal: 5,
        sparePartsClerk: 3,
      },

      permissions: [
        {
          id: 0,
          label: "All Permissions",
          children: [
            {
              id: 1,
              label: "User Control Access",
              children: [
                {
                  id: 2,
                  label: "Role Type List",
                  children: [
                    {
                      id: 5,
                      label: "Add - Role Type",
                    },
                    {
                      id: 6,
                      label: "Update - Role Type",
                    },
                  ],
                },
                {
                  id: 3,
                  label: "Role List",
                  children: [
                    {
                      id: 7,
                      label: "Add - Role",
                    },
                    {
                      id: 8,
                      label: "Update - Role",
                    },
                  ],
                },
                {
                  id: 4,
                  label: "User List",
                  children: [
                    {
                      id: 9,
                      label: "Add - User",
                    },
                    {
                      id: 10,
                      label: "Update - User",
                    },
                  ],
                },
              ],
            },
          ],
        },
      ],

      ticked: [],

      roles: [],

      roleTypes: [],
    };
  },

  created() {
    this.queryData();

    this.populateRoleTypeDropdown();
  },

  computed: {
    totalPages() {
      const perPage = this.$route.query.per_page || 10;
      return Math.ceil(this.total / perPage);
    },
  },

  methods: {
    queryData() {
      const vm = this;

      this.$api
        .get("/privilege/role/query")
        .then(function (response) {
          vm.roles = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    populateFields(id) {
      this.modalFormOptions.id = id;

      const link = "/privilege/role/view/query?id=" + id;

      this.$api
        .get(link)
        .then((response) => {
          const roleData = response.data.data[0];
          this.modalFormData.roleTypeID = roleData.roleTypeID;
          this.modalFormData.roleName = roleData.roleName;
          this.ticked = roleData.permissions;
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
        actionURL = "/privilege/role/add";
      else if (this.modalFormOptions.action === "Update")
        actionURL = "/privilege/role/update";
      else console.log("Should not be here :(");

      const vm = this;

      const permissions = this.generatePermissionIDs(
        [...this.permissions],
        [...this.ticked]
      );

      this.$api
        .post(actionURL, { id, ...this.modalFormData, permissions })
        .then(function (response) {
          console.log(response);
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

      this.ticked = [];

      this.showModal = true;
    },

    populateRoleTypeDropdown() {
      const vm = this;

      this.$api
        .get("/privilege/role-type/query")
        .then(function (response) {
          vm.roleTypes = response.data.data;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    generatePermissionIDs(nodes, ticked) {
      const ids = [];

      const generatePermissionIDsRec = function (nodes, ticked) {
        let flag = false;
        for (const node of nodes) {
          if (!node.children || node.children.length === 0) {
            if (ticked.includes(node.id)) {
              return true;
            }
          } else {
            if (generatePermissionIDsRec(node.children, ticked)) {
              if (node.id !== 0) {
                ids.push(node.id);
              }
              flag = true;
            }
          }
        }
        return flag;
      };

      generatePermissionIDsRec(nodes, ticked);

      return [...ids, ...ticked].sort((a, b) => a - b);
    },

    kebabRole(role) {
      return kebabCase(role);
    },

    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
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
