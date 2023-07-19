<template>
  <div class="container">
    <div class="ticket-grid text-body2">
      <div class="row grid-header text-weight-medium text-center items-center">
        <div class="col-4">
          <div class="row">
            <div class="col-1">Cosmetic</div>
            <div class="col-4" @click="sort('serialNumber')">SN</div>
            <div class="col-5" @click="sort('model')">Model</div>
            <div class="col-2" @click="sort('version')">Version</div>
          </div>
        </div>

        <div class="col-4">Reported Issue</div>

        <div class="col-4">
          <div class="row items-center">
            <div class="col-3">Customer ID</div>
            <div class="col-4">Warranty Exp. Date</div>
            <div class="col-3">Warranty Status</div>
            <div class="col-2">Invoice Amt.</div>
          </div>
        </div>
      </div>

      <div v-for="(serialData, index) in getSerials" :key="index">
        <div class="row grid-row text-center items-center">
          <div class="col-4">
            <div class="row items-center">
              <div class="col-1">
                <q-checkbox v-model="serialData.cosmetic" />
              </div>
              <div class="col-4">{{ serialData.serialNumber }}</div>
              <div class="col-5">{{ serialData.model }}</div>
              <div class="col-2">{{ serialData.version }}</div>
            </div>
          </div>

          <div class="col-4 text-left">
            {{ serialData.customerReportedIssue }}
          </div>

          <div class="col-4">
            <div class="row items-center">
              <div class="col-3">{{ serialData.terminalID }}</div>
              <div class="col-4">{{ serialData.warrantyExpDate }}</div>
              <div class="col-3">{{ serialData.warrantyStatus }}</div>
              <div class="col-2">{{ serialData.repairPrice }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="button-group">
      <q-btn class="remove-unit" size="sm" color="red" icon="close" round />
      <q-btn class="edit-unit" size="sm" color="primary" icon="edit" round />
    </div>
  </div>

   <div class="q-pa-lg flex flex-center">
      <div class="col-auto">
         <span class="q-mr-xs">Show</span>
         <q-select
          class="inline"
          style="padding: 0"
          outlined
          v-model="perPage"
          :options="perPageOptions"
          @update:model-value="changeToPage(this.perPage,this.page)"
          dense
          />
         <span class="q-ml-xs">records per page</span>
         </div>


      <div class="col text-right q-mr-md">
        Showing {{ dataRange }} of {{ getTotal }} records
      </div>

          <q-pagination
              v-model="page"
              :min="1"
              :max="getTotalPages"
              :max-pages="0"
              ellipsess
              :direction-links="true"
              @click="getNextPages(this.page)"
            >
            </q-pagination>


                <div class="col-auto">
                  <span class="q-ml-md">Go to</span>
                  <q-input
                    class="inline q-mx-sm"
                    style="max-width: 40px"
                    outlined
                    v-model.number="pageInput"
                    dense
                  />
                  <q-btn outline label="Go" @click="goToPage(this.pageInput)" />
                </div>

     </div>

</template>

<script>

import { mapState } from "pinia";
import { useCreateTicketStore } from "stores/createTicket";
export default {

  props: ["pages", "total"],

  data() {
      return {
        page: 1,
        pageInput: 1,
        perPage: 10,
        perPageOptions: [10, 25, 50, 100],
        serials: [],
      };
  },

  created() {
    this.page = Number(this.$route.query.page) || 1;
    this.perPage = Number(this.$route.query.per_page) || 10;
  },

  watch: {
      page(newPage) {
        this.buildQuery(newPage);
      },
      perPage() {
        this.buildQuery(this.page);
      },
  },

  methods:{
          buildQuery(page) {
               const query = Object.assign({}, this.$route.query);

               if (page !== 1) {
                 query.page = page;
               } else if (query.page) {
                 delete query.page;
               }

               if (this.perPage !== 10) {
                 query.per_page = this.perPage;
               } else if (query.per_page) {
                 delete query.per_page;
               }

               this.changeRouteByQuery(query);
             },

             changeRouteByQuery(query) {
               const resolved = this.$router.resolve({ path: this.$route.path, query });

               if (resolved.href !== this.$route.fullPath) {
                 this.$router.push({ path: this.$route.path, query });
               }
          }
  },
  computed: {

     dataRange() {
        const rangeFrom = (this.page - 1) * this.perPage + 1;
        const rangeTo = Math.min(this.page * this.perPage, this.getTotal);

        return rangeFrom + "-" + rangeTo;
     },

    ...mapState(useCreateTicketStore, ["getSerials","getTotal","getTotalPages","getNextPages","changeToPage","goToPage","sort"]),
  },
};
</script>

<style lang="scss" scoped>
.container {
  max-width: 100%;
  margin: 10px auto;
  overflow-x: auto;
}
.ticket-grid {
  min-width: 1000px;
}
.grid-header {
  cursor: pointer;
}
.grid-row {
  position: relative;
  padding: 10px 0;
  cursor: pointer;
}
.grid-row:hover {
  box-shadow: inset 0 0 5px 0 rgba(0, 0, 0, 0.5);
}
.grid-row:nth-child(even) {
  background-color: #ececec;
}
.button-group {
  position: absolute;
  top: 50%;
  right: 50%;
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  .remove-unit {
    position: relative;
    z-index: 1200;
  }
  .edit-unit {
    position: relative;
    z-index: 1200;
  }
}
</style>
