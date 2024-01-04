<template>
  <div>
    <div class="q-pa-md">
      <q-table
        title="Ticket Serial Numbers"
        row-key="serialNumber"
        :columns="columns"
        :rows="serials"
        :rows-per-page-options="[10, 25, 50, 100]"
        id="serials"
      >
        <template v-slot:header="props">
          <q-tr :props="props">
            <q-th key="cosmetic">
              <q-checkbox
                v-model="comesticAll"
                @update:model-value="selectAllCosmetic"
                :disable="showViewUnit"
              >
              </q-checkbox>
              Cosmetic
            </q-th>
            <q-th key="action">Action</q-th>
            <q-th key="serialNumber">Serial Number</q-th>
            <q-th key="model">Model</q-th>
            <q-th key="version">Version</q-th>
            <q-th key="customerReportedIssue">Reported Issue</q-th>
            <q-th key="terminalID">Customer ID</q-th>
            <q-th key="customerRMA">Customer RMA</q-th>
            <q-th key="warrantyStatus">Warranty Status</q-th>
            <q-th key="warrantyExpDate">Warranty Expire Date</q-th>
          </q-tr>
        </template>
        <template v-slot:bottom-row>
          <q-tr>
            <q-td colspan="100%">
              <div class="text-h6 text-right">
                Total Estimated Cost: $ {{ totalInvoice }}
              </div>
            </q-td>
          </q-tr>
        </template>
        <template v-slot:body="props">
          <q-tr
            v-if="!props.row.errorMsg"
            :props="props"
            :class="{
              'verified-unit': props.row.xmOID != null,
              [props.row.bgColor]: true,
            }"
            @click="handleRowClick($event, props.row)"
            :key="props.row.serialNumber"
          >
            <q-td key="cosmetic" :props="props">
              <q-checkbox
                v-model="props.row.cosmetic"
                @update:model-value="selectCosmetic"
                :disable="
                  containsXrefMaterials === null ? false : containsXrefMaterials
                "
              >
              </q-checkbox>
            </q-td>
            <q-td key="actions" :props="props">
              <q-btn
                v-if="showRemoveUnit"
                flat
                round
                color="red-6"
                icon="close"
                @click="handleClickRemoveUnit"
              ></q-btn>
              <q-btn
                v-if="showUpdateUnit"
                flat
                round
                color="green-6"
                icon="edit"
                @click="handleClickUpdateUnit"
              ></q-btn>
              <q-btn
                v-if="showViewUnit"
                flat
                round
                color="yellow-9"
                icon="visibility"
                @click="handleClickViewUnit($event, props.row)"
              ></q-btn>
            </q-td>
            <q-td key="serialNumber" :props="props">
              {{ props.row.serialNumber }}
            </q-td>
            <q-td key="model" :props="props">
              {{ props.row.model }}
            </q-td>
            <q-td key="version" :props="props">
              {{ props.row.version }}
            </q-td>
            <q-td key="customerReportedIssue" :props="props">
              {{ props.row.customerReportedIssueExt }}
            </q-td>
            <q-td key="terminalID" :props="props">
              {{ props.row.customerTerminalID }}
            </q-td>
            <q-td key="customerRMA" :props="props">
              {{ props.row.customerRMA }}
            </q-td>
            <q-td key="warrantyStatus" :props="props">
              {{ props.row.warrantyStatus }}
            </q-td>
            <q-td key="warrantyExpDate" :props="props">
              {{
                props.row.warrantyExpDate === "N/A"
                  ? "N/A"
                  : this.getParseDate(props.row.warrantyExpDate)
              }}
            </q-td>
          </q-tr>

          <q-tr
            v-else
            :props="props"
            :class="props.row.bgColor"
            @click="handleRowClick($event, props.row)"
          >
            <q-td key="serialNumber" colspan="100%" :props="props">
              <div class="text-h6 text-negative">
                SN: {{ props.row.serialNumber }} - {{ props.row.errorMsg }}
              </div>
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>

    <!-- View Serial Details -->
    <BaseModal
      v-model:show="showDetailModal"
      @update:show="showDetailModal = false"
      title="View repair details"
      :width="800"
    >
      <TicketDetailForm
        :details="details"
        @hide-ticket-detail="showDetailModal = false"
      />
    </BaseModal>
    <!-- Pop-up window: add or Update Device to Ticket Window -->
    <EditModal
      ref="editModal"
      :serial="modalState.serialData"
      :title="modalState.title"
      :btnLable="modalState.btnLable"
      :action="modalState.submitAction"
      :containsXrefMaterials="containsXrefMaterials"
      @add-serial="handleAddSerial"
      @update-serial="handleUpdateSerial"
    />
  </div>
</template>

<script>
import { mapActions, mapState, mapWritableState } from "pinia";
import { useEditTicketStore } from "src/stores/editTicket";
import BaseModal from "./BaseModal.vue";
import EditModal from "./EditModal.vue";
import TicketDetailForm from "./TicketDetailForm.vue";
import { DateTime } from "luxon";
import { Notify } from "quasar";
import { parseDateTime, parseDate } from "../utils/timeUtils.js";

import { api } from "src/boot/axios";
import { useCreateTicketStore } from "src/stores/createTicket";

export default {
  props: [
    "isFromMaster",
    "orderType",
    "rows",
    "containsXrefMaterials",
    "inputValue",
    "encrypt",
    "showViewUnit",
    "showUpdateUnit",
    "showRemoveUnit",
    "ticketId",
  ],
  components: { EditModal, BaseModal, TicketDetailForm },
  emits: ["add-sn", "update-sn", "remove-sn"],
  data() {
    return {
      countCosmeticAll: 0,
      selected: [],
      comesticAll: false,
      columns: [
        {
          name: "cosmetic",
          align: "center",
          label: "Cosmetic",
          field: "cosmetic",
          sortable: false,
        },
        {
          name: "actions",
          align: "center",
          label: "Actions",
          field: "actions",
          sortable: false,
        },
        {
          name: "serialNumber",
          align: "center",
          label: "Serial Number",
          field: "serialNumber",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "model",
          align: "center",
          label: "Model",
          field: "model",
          sortable: true,
          sort: (a, b) => (a <= b ? 1 : -1),
        },
        {
          name: "version",
          align: "center",
          label: "Version",
          field: "version",
          sortable: false,
        },
        {
          name: "customerReportedIssue",
          align: "center",
          label: "Reported Issue",
          field: "customerReportedIssue",
          sortable: false,
        },
        {
          name: "terminalID",
          align: "center",
          label: "Customer ID",
          field: "terminalID",
          sortable: false,
        },
        {
          name: "customerRMA",
          align: "center",
          label: "customerRMA",
          field: "customerRMA",
          sortable: false,
        },
        {
          name: "warrantyStatus",
          align: "center",
          label: "Warranty Status",
          field: "warrantyStatus",
          sortable: false,
        },
        {
          name: "warrantyExpDate",
          align: "center",
          label: "Warranty Expire Date",
          field: "warrantyExpDate",
          sortable: false,
        },
      ],
      showDetailModal: false,
      updateLoading: false,
      //current state of Modal
      modalState: {
        title: "Add Device to Ticket",
        btnLable: "Add Device",
        serialData: {
          oldSerialNumber: null,
          serialNumber: null,
          terminalID: null,
          customerReportedIssue: null,
          customerRMA: null,
        },
        submitAction: "add",
      },
      // showRemoveUnit: false,
      // showUpdateUnit: false,
      // showViewUnit: false,
      details: {},
    };
  },
  mounted() {
    // window.addEventListener("click", this.handleGlobalClick);
    this.rows.forEach((r) => {
      if (r.cosmetic === true) {
        this.countCosmeticAll += 1;
      }
      if (this.countCosmeticAll === this.rows.length) {
        this.comesticAll = true;
      } else if (this.countCosmeticAll === 0) {
        this.comesticAll = false;
      } else {
        this.comesticAll = null;
      }
    });
  },
  computed: {
    serials() {
      let serials = this.rows;
      if (this.inputValue != null) {
        serials = serials.filter(
          (t) =>
            (t.serialNumber != null &&
              t.serialNumber.includes(this.inputValue)) ||
            (t.model != null && t.model.includes(this.inputValue)) ||
            (t.customerReportedIssueExt != null &&
              t.customerReportedIssueExt.includes(this.inputValue))
        );
      }
      return serials;
    },
    totalInvoice() {
      const serials = this.rows;
      let amt = 0;
      if (this.orderType === 3) {
        //repair
        serials.forEach((s) => {
          if (s.valid === true && s.warrantyStatus === "Out Of Warranty") {
            amt += s.minorPrice;
          }
        });
      }
      if (this.orderType === 7) {
        //diagnostic
        serials.forEach((s) => {
          if (s.valid === true) {
            amt = amt + (s.diagnosticPrice == null ? 0 : s.diagnosticPrice);
          }
        });
      }
      // if (
      //   this.orderType === 65 &&
      //   this.encrypt != null &&
      //   this.encrypt === "yes"
      // ) {
      //   //decommissioned
      //   serials.forEach((s) => {
      //     if (s.valid === true) {
      //       amt = amt + (s.keyInjection == null ? 0 : s.keyInjection);
      //     }
      //   });
      // }
      if (this.encrypt === "yes") {
        serials.forEach((s) => {
          amt = amt + (s.keyInjection == null ? 0 : s.keyInjection);
        });
      }
      serials.forEach((s) => {
        {
          //cosmetic
          if (s.valid === true && s.cosmetic === true && this.orderType != 65) {
            amt = amt + s.cosmeticPrice;
          }
        }
      });

      return amt;
    },
  },
  methods: {
    ...mapActions(useCreateTicketStore, ["populateKeyTypeOpt"]),
    ...mapActions(useEditTicketStore, [
      "removeTicket",
      "addTicket",
      "updateSN",
      "removeSN",
      "addSN",
    ]),
    handleRowClick(evt, row) {
      this.modalState.serialData = row;
    },
    handleClickAddUnit() {
      this.modalState.title = "Add Device to Ticket";
      this.modalState.btnLable = "Add Device";
      this.modalState.submitAction = "add";
      this.modalState.serialData = {
        serialNumber: null,
        terminalID: null,
        customerReportedIssue: null,
        customerRMA: null,
      };
      this.$refs.editModal.displayEditModal();
    },
    handleClickUpdateUnit() {
      this.modalState.title = "Update Device to Ticket";
      this.modalState.btnLable = "Update Device";
      this.modalState.submitAction = "update";
      this.modalState.oldSerialNumber = this.modalState.serialData.serialNumber;
      this.$refs.editModal.displayEditModal();
    },
    handleClickRemoveUnit() {
      const sn = this.modalState.serialData.serialNumber;
      this.$emit("remove-sn", { sn });
    },
    handleClickViewUnit(evt, row) {
      const { xmOID, pxmOID } = row;
      const id = xmOID === null ? pxmOID : xmOID;

      const link = "/ticketing/viewDetails?id=" + id +"&ticketId="+this.ticketId;

      api
        .get(link)
        .then((response) => {
          if (response.data.resultCode !== 0) {
            throw new Error(response.data.errorMessage);
          }
          this.details = response.data.data[0];
          this.computeStatusItem();
          this.showDetailModal = true;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
          Notify.create({
            type: "negative",
            message: error.message,
          });
        });
    },

    /**
     * Handler for child component: EditModal
     */
    handleAddSerial(serial) {
      this.$emit("add-sn", { serial });
      this.$refs.editModal.hideEditModal();
    },
    handleUpdateSerial(serial) {
      const oldSN = this.modalState.serialData.serialNumber;
      this.$emit("update-sn", { oldSN, serial });
      this.$refs.editModal.hideEditModal();
    },
    computeStatusItem() {
      const now = DateTime.now();
      const receive = DateTime.fromISO(this.details.receivedDate);
      const repair = DateTime.fromISO(this.details.repairDate);
      const complete = DateTime.fromISO(this.details.completedDate);
      const qa = DateTime.fromISO(this.details.quarantineDate);
      const ship = DateTime.fromISO(this.details.shipDate);

      const statusItems = [];
      statusItems.push({
        completed: receive.invalid != null ? false : receive < now,
        completeTime: this.details.receivedDate,
      });

      statusItems.push({
        completed: repair.invalid != null ? false : repair < now,
        completeTime: this.details.repairDate,
      });

      statusItems.push(
        (statusItems[1] = {
          completed: complete.invalid != null ? false : complete < now,
          completeTime: this.details.completedDate,
        })
      );
      statusItems.push({
        completed: qa.invalid != null ? false : qa < now,
        completeTime: this.details.quarantineDate,
      });

      statusItems.push({
        completed: ship.invalid != null ? false : ship < now,
        completeTime: this.details.shipDate,
      });

      this.details.statusItems = statusItems;
    },
    selectAllCosmetic(value, evt) {
      if (value === true) {
        this.countCosmeticAll = this.rows.length;
      } else {
        this.countCosmeticAll = 0;
      }
      this.rows.forEach((r) => (r.cosmetic = value));
    },
    selectCosmetic(value, evt) {
      if (value === true) {
        this.countCosmeticAll += 1;
      } else {
        this.countCosmeticAll -= 1;
      }
      if (this.countCosmeticAll === this.rows.length) {
        this.comesticAll = true;
        return;
      }
      if (this.countCosmeticAll === 0) {
        this.comesticAll = false;
        return;
      }
      this.comesticAll = null;
    },
    getParseDate(timeStr) {
      return parseDate(timeStr);
    },
    getParseDateTime(timeStr) {
      return parseDateTime(timeStr);
    },
  },
};
</script>

<style lang="scss" scoped>
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

ul {
  padding: 0;
  width: 100%;
  list-style: none;
}

ul > li {
  padding-top: 50px;
}

.timeline-dot {
  position: absolute;
  top: 0;
  left: 50%;
  width: 46px;
  height: 46px;

  z-index: 1;

  border-radius: 100%;
  transform: translateX(-50%);
  border: 3px solid white;
}

.connector {
  position: absolute;
  top: 23px;
  left: 0;
  width: 100%;
  height: 3px;

  transform: translateY(-50%);
}

tr td:first-child {
  border-left: white 5px solid;
}

.verified-unit > td:first-child {
  border-left: 5px solid green;
}
</style>
