<template>
  <div class="container">
    <div class="ticket-grid text-body2">
      <div class="row grid-header text-weight-medium text-center items-center">
        <!-- Header of Serials -->
        <div class="col-4">
          <div class="row">
            <div class="col-1">Cosmetic</div>
            <div class="col-4" @click="sort('serialNumber')"> <img style="width: 20px" v-bind:src="getIconPath" /> SN</div>
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

      <div v-for="(serialData, index) in getSerials" :key="index" id="serials">
        <div class="row grid-row text-center items-center" @click.stop="handleClickOnSerials(index, $event)">
          <div class="col-4">
            <div class="row items-center">
              <!-- Where to add btn group -->
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
      <div v-if="getSerials === undefined || getSerials.length == 0" class="row grid-row text-center items-center">
        <div class="col-12 text-center">
          No Serial Number Input
        </div>
      </div>
    </div>
    <!-- button group: delete and update-->
    <div class="popup-button-group" v-show="withClient" :style="positionStyle">
      <q-btn class="remove-unit" size="sm" color="red" icon="close" round @click.stop="handleRemoveSerial" />
      <q-btn class="edit-unit" size="sm" color="primary" icon="edit" round @click.stop="handleUpdateSerial" />
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
        Showing {{ getRangeForm }} - {{getRangeTo}} of {{ getTotal }} records
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
  emits: ["updateSerial"],
  data() {
        return {
          withClient: false,
          serialIndex: null,
          mouseX: 0,
          mouseY: 0,
          positionStyle: { top: '0px', left: '0px' },
          timeOutId: null,
          removeUnitWidth: null,
          spaceInBtnGroup: 6,
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
    /*
      1. When first click within serial grid, get the width of remove-unit/update-unit
      2. Use the width to set the top, left, and width for popup-button-groups
      3. forceUpdate to render it
     */
    updated() {
      if (this.withClient === true && this.removeUnitWidth == null) {
        const btn = document.querySelector(".remove-unit");
        this.removeUnitWidth = btn.getBoundingClientRect().width;

        const left = parseInt(this.positionStyle.left);
        const top = parseInt(this.positionStyle.top);
        const width = this.removeUnitWidth * 2 + this.spaceInBtnGroup;


        this.positionStyle = {
          "top": top - this.removeUnitWidth / 2 + "px",
          "left": left - this.removeUnitWidth - this.spaceInBtnGroup / 2 + "px",
          "width": width + "px",
        };
        this.$forceUpdate();
      }
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
           },

           /*
                 1. use withClient to control the btn group
                 2. update the position of the btn group
                 3. set a 5 seconds time out for the btn group
                 4. If the timeout already exists, refresh it
                */
               handleClickOnSerials(index, event) {
                 if (this.withClient === false) {
                   this.withClient = true;
                 }
                 this.addPopupBtns(event);
                 this.serialIndex = index;

                 //set 5000 timeout for popup buttons
                 if (this.timeOutId != null) {
                   //clear the old timeout and set a new one
                   window.clearTimeout(this.timeOutId);
                 }
                 this.timeOutId = window.setTimeout(this.removePopupBtns, "3000");
                 window.addEventListener("click", this.handleClickOutSerials);
               },
               /*
                 1. check if the popup buttons is with Client
                 2. get the event dom
                 3. check if the dom is within #serials
                 4. if not, remove the popupBtns
               */
               handleClickOutSerials(event) {
                 if (this.withClient === true) {
                   //popupBtns is withClient
                   const clickedDom = event.target;
                   const serials = document.getElementById("serials");
                   if (serials != null && !serials.contains(clickedDom)) {
                     //click out of serials
                     this.removePopupBtns();
                   }
                 }
               },
               /*
                 1. disappear the btn group by set withClient to false
                */
               removePopupBtns() {
                 if (this.withClient === true) {
                   this.withClient = false;
                 }
               },
               /*
                 1. show up the btn group by set withCleint to true
                 2. set the btns group position using the click event position
                */
               addPopupBtns(event) {
                 if (this.withClient === false) {
                   this.withClient = true;
                 }
                 this.mouseX = event.pageX;
                 this.mouseY = event.pageY;

                 if (this.removeUnitWidth === null) {
                   this.positionStyle = { "top": this.mouseY + 'px', "left": this.mouseX + 'px' };
                 }
                 else {
                   this.positionStyle["top"] = this.mouseY - this.removeUnitWidth / 2 + 'px';
                   this.positionStyle["left"] = this.mouseX - this.removeUnitWidth - this.spaceInBtnGroup / 2 + 'px';
                 };
               },

               /*
                 1. Remove the serial by index using ticketStore
                 2. When all serials are delete, the pop-up btns disappear directly.
                */
               handleRemoveSerial() {
                 const serials = this.getSerials;
                 const { serialNumber } = serials[this.serialIndex];
                 this.removeSerial(serialNumber);
                 if (this.serials === undefined || this.serials.length == 0) {
                   this.removePopupBtns();
                 }
               },

               /*
                 1. show BaseModal in CreateTicketPage by notify parent node
                 2. Render serials and index to BaseModal
                */
               handleUpdateSerial() {
                 const ticketStore = useCreateTicketStore();
                 const index = this.serialIndex;
                 const serialData = ticketStore.getSerials[index];
                 const { serialNumber } = serialData;
                 this.$emit("updateSerial", { serialData, serialNumber });
                 this.removePopupBtns();
               },
   },

  computed: {

    ...mapState(useCreateTicketStore, ["getSerials","removeSerial","getTotal","getTotalPages","getNextPages","changeToPage","goToPage","sort","getRangeForm","getRangeTo","getIconPath"]),
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

.popup-button-group {
  position: absolute;
  height: max-content;
  justify-content: space-between;
  width: max-content;
  display: flex;
  flex-direction: row;

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
