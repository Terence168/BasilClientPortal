<template>
  <div>
    <q-markup-table>
      <thead>
        <tr>
          <th v-if="bulkAssign"><q-checkbox v-model="selectAll" /></th>
          <th
            v-for="column in tableData.columns"
            :key="column.id"
            :class="{
              sortable: column.sortable,
              sorted: sort[column.id],
              'sort-desc': sort[column.id] && sort[column.id].desc,
            }"
            @click="sortField(column.id)"
          >
            {{ column.label }}
            <q-icon
              v-if="column.sortable"
              name="arrow_upward"
              class="q-table__sort-icon q-table__sort-icon--left"
            />
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(row, index) in tableData.rows"
          :key="`${row.id}${index}`"
          :class="{
            attention: row.materialSort !== undefined && !row.materialSort,
          }"
        >
          <td v-if="bulkAssign">
            <q-checkbox v-model="bulkAssignIDs" :val="row.id" />
          </td>
          <td
            class="text-center"
            v-for="column in tableData.columns"
            :key="column.id"
          >
            <span v-if="column.id === 'actions' || column.id === 'actionView'">
              <template v-if="!permissions">None</template>
              <template v-else>
                <q-btn
                  v-if="permissions.view"
                  flat
                  round
                  color="grey-6"
                  icon="search"
                  @click="$emit('detailed-view', row.id)"
                ></q-btn>
                <q-btn
                  v-if="permissions.update && column.id !== 'actionView'"
                  flat
                  round
                  color="grey-6"
                  icon="edit"
                  @click="$emit('update-data', row.id)"
                ></q-btn>
                <q-btn
                  v-if="column.deleteAction"
                  flat
                  round
                  color="grey-6"
                  icon="delete"
                  @click="$emit('delete-data', row.id)"
                ></q-btn>
              </template>
            </span>

            <span v-if="column.id === 'userActions'">
              <q-btn
                flat
                round
                color="grey-6"
                icon="manage_accounts"
                @click="$emit('update-data', row.id)"
              ></q-btn>
              <!-- <q-btn
                flat
                round
                color="grey-6"
                icon="lock"
                @click="$emit('disable-user', row.id)"
              ></q-btn> -->
            </span>

            <span
              style="cursor: pointer"
              v-if="column.id === 'techNotes' || column.id === 'reportedIssue'"
            >
              {{ shortenText(row[column.id]) }}
              <q-tooltip
                class="bg-primary text-body2 shadow-4"
                max-width="500px"
                :offset="[10, 10]"
              >
                {{ row[column.id] }}
              </q-tooltip>
            </span>

            <span
              v-else-if="column.id === 'faultCode'"
              style="white-space: pre"
            >
              {{ row[column.id].split(",").join("\n") }}
            </span>

            <span v-else>{{ row[column.id] }}</span>
          </td>
        </tr>
      </tbody>
    </q-markup-table>
  </div>
</template>

<script>
export default {
  props: ["tableData", "permissions", "bulkAssign", "ids"],

  data() {
    return {
      bulkAssignIDs: [],
      sort: {},
    };
  },

  watch: {
    bulkAssignIDs: function (val) {
      this.$emit("update:ids", val);
    },
  },

  computed: {
    selectAll: {
      // getter
      get: function () {
        let allSelected = true;
        let someSelected = false;
        let selectedRow;

        for (let { id } of this.tableData.rows) {
          selectedRow = this.bulkAssignIDs.includes(id);

          allSelected &&= selectedRow;
          someSelected ||= selectedRow;
        }

        return allSelected === someSelected ? allSelected : null;
      },
      // setter
      set: function (checkAll) {
        if (checkAll) {
          for (let { id } of this.tableData.rows) {
            if (!this.bulkAssignIDs.includes(id)) {
              this.bulkAssignIDs.push(id);
            }
          }
        } else {
          for (let { id } of this.tableData.rows) {
            const index = this.bulkAssignIDs.indexOf(id);
            if (index > -1) {
              this.bulkAssignIDs.splice(index, 1);
            }
          }
        }
      },
    },
  },

  created() {
    const initialSort = this.$route.query.sort
      ? this.$route.query.sort.split(",")
      : [];

    for (const sortingOption of initialSort) {
      const [field, desc] = sortingOption.split(".");

      this.sort[field] = { desc };
    }
  },

  methods: {
    sortField(id) {
      if (!this.tableData.columns.find((row) => id === row.id).sortable) return;

      if (!this.sort[id]) {
        this.sort[id] = { desc: false };
      } else if (!this.sort[id].desc) {
        this.sort[id].desc = true;
      } else {
        delete this.sort[id];
      }
      this.buildQuery();
    },

    buildQuery() {
      const query = Object.assign({}, this.$route.query);

      const sortedFields = [];

      for (const sortedField in this.sort) {
        sortedFields.push(
          this.sort[sortedField].desc ? `${sortedField}.desc` : `${sortedField}`
        );
      }

      if (sortedFields.length > 0) {
        query["sort"] = sortedFields.join(",");
      } else {
        delete query["sort"];
      }

      this.changeRouteByQuery(query);
    },

    changeRouteByQuery(query) {
      const resolved = this.$router.resolve({ path: this.$route.path, query });

      if (resolved.href !== this.$route.fullPath) {
        this.$router.push({ path: this.$route.path, query });
      }
    },

    shortenText(text, maxLength = 30) {
      if (!text) return text;

      let output = text.trim();

      if (output.length > maxLength) {
        return text.slice(0, maxLength).trim() + "...";
      } else {
        return output;
      }
    },
  },
};
</script>

<style lang="sass">
.attention
  background-color: #FFBCBC
</style>
