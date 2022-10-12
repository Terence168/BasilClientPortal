<template>
  <div class="q-pa-md">
    <q-table
      :ref="'role' + role"
      flat
      class="transparent"
      :title="title"
      :data="users"
      :columns="columns"
      :row-key="(row) => role + '-' + row.userName"
      v-model:pagination="pagination"
      hide-pagination
      :filter="filter"
    >
      <template v-slot:top>
        <div class="row items-center q-mb-sm">
          <div class="col-auto q-table__title q-mr-sm">{{ title }}</div>
          <q-btn
            class="col-auto"
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
          <template v-if="text" v-slot:append
            ><q-icon name="search"
          /></template>
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

      // users: [
      //   { id: 1, userName: "John Doe", email: "aaa@gmail.com" },
      //   { id: 2, userName: "John Doe Jr.", email: "bbb@gmail.com" },
      //   { id: 3, userName: "Jane Doe", email: "ccc@gmail.com" },
      //   { id: 4, userName: "Will Smith", email: "ddd@gmail.com" },
      //   { id: 5, userName: "Michael Bates", email: "eee@gmail.com" },
      //   { id: 6, userName: "Mark Bowman", email: "ggg@gmail.com" }
      // ]
    };
  },

  computed: {
    pagesNumber() {
      return Math.ceil(this.filteredUserNumber / this.pagination.rowsPerPage);
    },
  },

  methods: {
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
