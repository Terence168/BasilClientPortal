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
          />

          <q-select
            v-else-if="field.type === 'select'"
            outlined
            v-model="filter[field.id]"
            :input-debounce="field.id !== 'location' ? 500 : 0"
            :options="field.id !== 'location' ? options[field.id] : locationOpt"
            :label="field.label"
            :use-input="field.id === 'customerId' || field.id === 'location'"
            @filter="filterFn(field.id, ...arguments)"
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

          <q-select
            v-else-if="field.type === 'customerSelect' && !clientUser"
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
            v-else-if="field.type === 'dateRange'"
            outlined
            v-model="filter[field.id]"
            :label="field.label"
            dense
          >
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

    filterClientGroupFn(val, update, abort) {
      const currentOptions = this.options["clientGroup"];

      if (currentOptions.length === 0) {
        const options = [
          { label: "TPG Canada", value: "457" },
          { label: "Small Markets", value: "458" },
          { label: "Large Markets", value: "459" },
          { label: "Fiserv", value: "460" },
        ];

        update(() => {
          currentOptions.push(...options);
        });
      } else {
        update();
      }
    },

    filterStageFn(val, update, abort) {
      const currentOptions = this.options["stage"];

      if (currentOptions.length === 0) {
        const options = [
          { value: 1085, label: "Destruction Bin" },
          { value: 1086, label: "Assign to Bin ID" },
          { value: 1087, label: "Out for Destruction" },
          { value: 1088, label: "Destroyed" },
        ];

        update(() => {
          currentOptions.push(...options);
        });
      } else {
        update();
      }
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

    filterStatusSchedulingFn(val, update, abort) {
      const currentOptions = this.options["status"];

      if (currentOptions.length === 0) {
        const link = "/basil/rma-scheduling/status/drop-down";

        this.$api
          .get(link)
          .then((response) => {
            update(() => {
              this.options["status"] = response.data.data;
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
