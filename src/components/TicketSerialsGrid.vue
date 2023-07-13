<template>
  <div class="container">
    <div class="ticket-grid text-body2">
      <div class="row grid-header text-weight-medium text-center items-center">
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
</template>

<script>
import { useCreateTicketStore } from "stores/createTicket";
import { mapState } from "pinia";

export default {
  computed: {
    ...mapState(useCreateTicketStore, ["getSerials"]),
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
