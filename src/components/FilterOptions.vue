<template>
  <div>
    <q-form @submit="onSubmit" @reset="onReset" class="q-mt-md">
      <div class="row q-col-gutter-x-lg q-col-gutter-y-md">
        <div v-for="field in filterFields" :key="field.id" class="col-4">
          <q-input
            v-if="!field.type"
            outlined
            v-model="filter[field.id]"
            :label="field.label"
            dense
          >
            <q-tooltip
              v-if="field.tooltip"
              class="bg-primary text-body2 shadow-4"
              style="white-space: pre"
              max-width="500px"
              :offset="[10, 10]"
            >
              {{ field.tooltip }}
            </q-tooltip>
          </q-input>

          <q-select
            v-if="field.type === 'select'"
            outlined
            v-model="filter[field.id]"
            :input-debounce="field.id !== 'location' ? 500 : 0"
            :options="field.id !== 'location' ? options[field.id] : locationOpt"
            :label="field.label"
            :use-input="field.id === 'customerId' || field.id === 'location'"
            @filter="
              (val, update, abort) => filterFn(field.id, val, update, abort)
            "
            dense
            emit-value
            map-options
          >
            <q-tooltip
              v-if="field.tooltip"
              class="bg-primary text-body2 shadow-4"
              style="white-space: pre"
              max-width="700px"
              :offset="[10, 10]"
            >
              {{ field.tooltip }}
            </q-tooltip>
            <template v-slot:no-option>
              <q-item>
                <q-item-section class="text-grey"> No results </q-item-section>
              </q-item>
            </template>
          </q-select>

          <q-select
            v-if="field.type === 'customerSelect' && !clientUser"
            outlined
            v-model="filter[field.id]"
            :input-debounce="500"
            :options="options[field.id]"
            :label="field.label"
            use-input
            @filter="filterCustomerFn"
            dense
            emit-value
            map-options
          >
            <template v-slot:no-option>
              <q-item>
                <q-item-section class="text-grey"> No results </q-item-section>
              </q-item>
            </template>
          </q-select>

          <q-input
            v-if="field.type === 'dateRange'"
            outlined
            v-model="filter[field.id]"
            :label="field.label"
            dense
            readonly
          >
            <q-tooltip
              v-if="field.tooltip"
              class="bg-primary text-body2 shadow-4"
              style="white-space: pre"
              max-width="700px"
              :offset="[10, 10]"
            >
              {{ field.tooltip }}
            </q-tooltip>
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy
                  ref="qDateProxy"
                  transition-show="scale"
                  transition-hide="scale"
                >
                  <q-date v-model="dateRange[field.id]" range>
                    <div class="row items-center justify-end">
                      <q-btn
                        v-close-popup
                        @click="setDateRange(field.id)"
                        label="Set Range"
                        color="primary"
                        flat
                      />
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>
      </div>

      <div class="row q-mt-md">
        <div class="row q-col-gutter-md">
          <div class="col-auto">
            <q-btn
              type="submit"
              :loading="submitting"
              label="Search"
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
              type="reset"
              :disable="submitting"
              label="Clear"
              color="grey-4"
              text-color="grey-6"
              style="min-width: 150px"
            />
          </div>
        </div>
      </div>
    </q-form>
  </div>
</template>

<script>
import { useUserStore } from "stores/user";

export default {
  props: ["filterFields"],

  data() {
    return {
      locationOpt: [],
      filter: {},
      dateRange: {},
      options: {},
      submitting: false,
    };
  },

  created() {
    for (const { id, type } of this.filterFields) {
      this.filter[id] = this.$route.query[id] || "";
      if (type === "dateRange") this.dateRange[id] = null;
      if (type === "select") this.options[id] = [];
      if (type === "customerSelect") this.options[id] = [];
    }
  },

  computed: {
    clientUser() {
      return useUserStore().isClientUser;
    },
  },

  methods: {
    onSubmit() {
      this.changeRouteByQuery(this.buildQuery());
    },

    onReset() {
      for (const fieldId in this.filter) {
        this.filter[fieldId] = "";
      }
      this.onSubmit();
    },

    buildQuery() {
      const query = Object.assign({}, this.$route.query);

      for (const fieldId in this.filter) {
        // if (typeof this.filter[fieldId] === "object")
        if (String(this.filter[fieldId]).trim() !== "") {
          query[fieldId] = this.filter[fieldId];
        } else if (query[fieldId]) {
          delete query[fieldId];
        }
      }

      return query;
    },

    changeRouteByQuery(query) {
      const resolved = this.$router.resolve({ path: this.$route.path, query });

      if (resolved.href !== this.$route.fullPath) {
        this.$router.push({ path: this.$route.path, query });
      }
    },

    setDateRange(id) {
      if (typeof this.dateRange[id] === "object") {
        this.filter[id] =
          this.dateRange[id].from + " ~ " + this.dateRange[id].to;
      } else {
        this.filter[id] = this.dateRange[id] + " ~ " + this.dateRange[id];
      }
    },

    filterFn(id, _val, update, _abort) {
      if (id === "customerId") {
        this.filterCustomerFn(_val, update, _abort);
        return;
      }

      if (id === "contact") {
        this.filterContactFn(_val, update, _abort);
        return;
      }

      if (id === "department") {
        this.filterDepartmentFn(_val, update, _abort);
        return;
      }

      if (id === "type") {
        this.filterTypeFn(_val, update, _abort);
        return;
      }

      if (id === "status") {
        this.filterStatusFn(_val, update, _abort);
        return;
      }

      const currentOptions = this.options[id];

      const link = "/basil/rma-setting/" + id + "/drop-down";

      if (currentOptions.length === 0) {
        this.$api
          .get(link)
          .then(function (response) {
            update(() => {
              currentOptions.push(...response.data.data);
            });
          })
          .catch(function (error) {
            // handle error
            console.log(error);
          });
      } else {
        update();
      }
    },

    filterCustomerFn(val, update, abort) {
      if (val.length < 3) {
        abort();
        return;
      }

      const link = "user/customers?customerName=" + val;

      this.$api
        .get(link)
        .then((response) => {
          update(() => {
            this.options.customerId = response.data.data;
          });
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    },

    filterContactFn(val, update, abort) {
      const currentOptions = this.options["contact"];

      if (currentOptions.length === 0) {
        const options = [
          {
            label: "No",
            value: "455",
          },
          {
            label: "Yes",
            value: "454",
          },
          {
            label: "Contacted",
            value: "456",
          },
          {
            label: "Responded",
            value: "673",
          },
        ];

        update(() => {
          currentOptions.push(...options);
        });
      } else {
        update();
      }
    },

    filterStatusFn(val, update, abort) {
      const currentOptions = this.options["status"];

      if (currentOptions.length === 0) {
        const link = "/ticketing/dropdown/status";

        this.$api
          .get(link)
          .then((response) => {
            update(() => {
              const data = response.data.data;

              // BCP-60 - filter out all status except for "Open" and "Closed" (needs to be deleted once the logistics department is ready)
              const filteredData = data.filter(
                (item) => item.label === "Open" || item.label === "Closed"
              );

              this.options["status"] = filteredData;
            });
          })
          .catch(function (error) {
            // handle error
            console.log(error);
          });
      } else {
        update();
      }
    },

    filterTypeFn(val, update, abort) {
      const currentOptions = this.options["type"];

      if (currentOptions.length === 0) {
        const link = "/ticketing/dropdown/order_type";

        this.$api
          .get(link)
          .then((response) => {
            update(() => {
              this.options["type"] = response.data.data;
            });
          })
          .catch(function (error) {
            // handle error
            console.log(error);
          });
      } else {
        update();
      }
    },

    filterDepartmentFn(val, update, abort) {
      const currentOptions = this.options["department"];

      if (currentOptions.length === 0) {
        const link = "/ticketing/dropdown/department";

        this.$api
          .get(link)
          .then((response) => {
            update(() => {
              this.options["department"] = response.data.data;
            });
          })
          .catch(function (error) {
            // handle error
            console.log(error);
          });
      } else {
        update();
      }
    },
  },
};
</script>

<style></style>
