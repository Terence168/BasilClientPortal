<template>
  <div class="container">
    <div class="ticket-grid text-body2">
      <div class="row grid-header text-weight-medium text-center items-center" >
        <!-- Header of Serials -->
        <div class="col-4">
          <div class="row">
            <div class="col-1">Cosmetic</div>
            <div class="col-4">SN</div>
            <div class="col-5">Model</div>
            <div class="col-2">Version</div>
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
    </div>
    <!-- button group: delete -->
    <div class="popup-button-group" v-if="withClient" :style="positionStyle">
      <q-btn class="remove-unit" size="sm" color="red" icon="close" round @click.stop="handleRemoveSerial"/>
      <q-btn class="edit-unit" size="sm" color="primary" icon="edit" round @click.stop="handleUpdateSerial"/>
    </div>
  </div>
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapState } from "pinia";


export default {
  computed: {
    ...mapState(useCreateTicketStore, ["getSerials"]),
  },

  data(){
    return {
      withClient: false,
      serialIndex: null,
      mouseX:0,
      mouseY:0,
      positionStyle:{top:'0px', left:'0px'},
      timeOutId:null
    }
  },

  methods:{
    /*
      1. use withClient to control the btn group
      2. update the position of the btn group
      3. set a 5 seconds time out for the btn group
      4. If the timeout already exists, refresh it
     */
    handleClickOnSerials(index, event){
      if(this.withClient === false){
        this.withClient = true;
      }
      this.addPopupBtns(event);
      this.serialIndex = index;
      
      //set 5000 timeout for popup buttons
      if(this.timeOutId != null){
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
    handleClickOutSerials(event){
      if(this.withClient === true ){
        //popupBtns is withClient
        let clickedDom = event.target;
        let serials = document.getElementById("serials");
        if(serials != null && !serials.contains(clickedDom)){
          //click out of serials
          this.removePopupBtns();
        }
      }
    },
    /*
      1. disappear the btn group by set withClient to false
     */
    removePopupBtns(){
      if(this.withClient === true){
          this.withClient = false;
      }
    },
    /*
      1. show up the btn group by set withCleint to true
      2. set the btns group position using the click event position
     */
    addPopupBtns(event){
      if(this.withClient === false){
        this.withClient = true;
      }
      this.mouseX = event.pageX;
      this.mouseY = event.pageY;
      this.positionStyle = {top:this.mouseY+'px', left:this.mouseX+'px'};
    },

    /*
      1. Remove the serial by index using ticketStore
      2. When all serials are delete, the pop-up btns disappear directly.
     */
    handleRemoveSerial(){
      const ticketStore = useCreateTicketStore();
      ticketStore.removeSerial(this.serialIndex);
      const serials = ticketStore.getSerials;
      if(this.serials === undefined || this.serials.length == 0){
        this.removePopupBtns();
      }
    },

    /*
      1. show BaseModal in CreateTicketPage by notify parent node
      2. Render serials and index to BaseModal
     */
    handleUpdateSerial(){
      const ticketStore = useCreateTicketStore();
      const index = this.serialIndex;
      const serialData = ticketStore.getSerials[index];
      this.$emit("updateSerial", {serialData, index});
    },
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
  width: max-content;
  display: flex;
  flex-direction: row;
  justify-content: start;
  overflow: auto;
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
