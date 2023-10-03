<template>
  <div class="q-pa-md">
    <q-table
      :ref="'role' + role"
      flat
      class="transparent"
      :title="title"
      :rows="users"
      :columns="columns"
      :row-key="(row) => user + '-' + row.id"
      v-model:pagination="pagination"
      hide-pagination
      :filter="filter"
    >
      <template v-slot:top>
        <div class="row items-center q-mb-sm">
          <div class="col-auto q-table__title q-mr-xs">{{ title }}</div>
          <q-btn
           v-if="checkPermission('privilege.role.update')"
            flat
            round
            color="primary"
            icon="edit_note"
            @click="$emit('update-data', id)"
          />
        </div>

        <q-input
          class="col-12"
          outlined
          dense
          debounce="300"
          v-model="filter"
          placeholder="Search"
          @input="updateUserNumber"
        >
          <template v-slot:append><q-icon name="search" /></template>
        </q-input>
      </template>
    </q-table>

    <div class="row justify-end q-mt-md">
      <q-pagination
        v-model="pagination.page"
        ref="pagination"
        color="grey-5"
        active-color="primary"
        :max="pagesNumber"
        :max-pages="5"
        :boundary-numbers="false"
        size="md"
        direction-links
      />
    </div>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";
export default {
  name: "RoleCard",

  props: { id: Number, title: String, role: String, users: Array },

  created() {
    this.$nextTick(function () {
      this.filteredUserNumber =
        this.$refs["role" + this.role].computedRowsNumber;
    });
  },

  data() {
    return {
      filter: null,

      filteredUserNumber: null,

      pagination: {
        sortBy: "userName",
        descending: false,
        page: 1,
        rowsPerPage: 5,
        // rowsNumber: xx if getting data from a server
      },

      columns: [
        {
          name: "userName",
          align: "center",
          label: "User Name",
          field: "userName",
          sortable: true,
        },
        {
          name: "email",
          align: "center",
          label: "Email",
          field: "email",
          sortable: true,
        },
      ],

    };
  },

  computed: {
    pagesNumber() {
      return Math.ceil(this.filteredUserNumber / this.pagination.rowsPerPage);
    },
  },

  methods: {
    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
    updateUserNumber() {
      this.$nextTick(function () {
        this.filteredUserNumber =
          this.$refs["role" + this.role].computedRowsNumber;
      });
    },
  },
};
</script>

<style></style>
