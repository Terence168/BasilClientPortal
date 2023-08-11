<template>
  <BaseModal
    v-model:show="showDetailModal"
    title="View repair details"
    :width="800"
  >
    <div class="q-mb-lg">
      <div class="row justify-center">
        <div class="col-auto">
          <q-card flat bordered style="width: 700px">
            <q-card-section>
              <div class="text-body2 text-weight-medium q-mb-sm">
                Unit Summary
              </div>
              <q-separator />
              <div
                class="row justify-center items-center"
                style="height: 130px"
              >
                <div class="col text-center">
                  <div class="text-h4 text-weight-medium text-primary">
                    {{ details.status }}
                  </div>
                  <div class="text-body2 text-grey-6">United Status</div>
                </div>

                <div class="col text-center">
                  <div class="text-h4 text-weight-medium text-primary">
                    {{ getParseDate(details.scheduledDate) }}
                  </div>
                  <div class="text-body2 text-grey-6">Scheduled Date</div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>
      <div class="q-mt-sm text-body1 text-weight-medium">Timeline</div>
      <ul class="timeline row justify-center">
        <li
          class="col relative-position text-center"
          v-for="(title, index) in statusTitles"
          :key="index"
        >
          <div class="text-subtitle1 text-weight-medium">
            {{ title }}
          </div>
          <div
            v-if="details.statusItems[index].completed"
            class="text-caption text-grey-6"
          >
            {{ getParseDateTime(details.statusItems[index].completeTime) }}
          </div>

          <div
            class="timeline-dot row justify-center items-center"
            :class="
              details.statusItems[index].completed ? 'bg-primary' : 'bg-grey-6'
            "
          >
            <div class="col-auto">
              <q-icon
                class="text-white"
                :name="details.statusItems[index] ? 'done' : 'access_time'"
                size="md"
              ></q-icon>
            </div>
          </div>

          <div
            class="connector"
            :class="details.statusItems[index] ? 'bg-primary' : 'bg-grey-6'"
          ></div>
        </li>
      </ul>
    </div>
    <div class="row q-col-gutter-x-md text-body1">
      <div class="col-6 q-gutter-y-md">
        <div class="row">
          <div class="text-grey-6">Department:&nbsp;</div>
          <div>{{ details.department }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">RMA Customer Name:&nbsp;</div>
          <div>{{ details.customerName }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">RMA Number:&nbsp;</div>
          <div>{{ details.rmaNumber }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Part Number:&nbsp;</div>
          <div>{{ details.partNumber }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Serial Number:&nbsp;</div>
          <div>{{ details.serialNumber }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Version Number:&nbsp;</div>
          <div>{{ details.versionNumber }}</div>
        </div>

        <template v-if="details.partNumber2">
          <div class="row">
            <div class="text-grey-6">Part Number 2:&nbsp;</div>
            <div>{{ details.partNumber2 }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Serial Number 2:&nbsp;</div>
            <div>{{ details.serialNumber2 }}</div>
          </div>

          <div class="row">
            <div class="text-grey-6">Version Number 2:&nbsp;</div>
            <div>{{ details.versionNumber2 }}</div>
          </div>
        </template>

        <div class="row">
          <div class="text-grey-6">Date Received:&nbsp;</div>
          <div>{{ getParseDateTime(details.receivedDate) }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Warranty Status:&nbsp;</div>
          <div>{{ details.warrantyStatus }}</div>
        </div>
      </div>
      <div class="col-6 q-gutter-y-md">
        <div class="row">
          <div class="text-grey-6">Repaired by:&nbsp;</div>
          <div>{{ details.assignee }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">ESD Kit Included:&nbsp;</div>
          <div>{{ details.esdKit }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Tamper Log Interpretation:&nbsp;</div>
          <div>{{ details.tamperLog }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Battery Voltage:&nbsp;</div>
          <div>{{ details.batteryVoltage }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Error Message:&nbsp;</div>
          <div>{{ details.errorMessage }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Physical Damage Present:&nbsp;</div>
          <div>{{ details.physicalDamagePresent }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Warranty Voided Date:&nbsp;</div>
          <div>{{ details.warrantyVoidedDate }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Quarantine Date:&nbsp;</div>
          <div>{{ getParseDateTime(details.quarantineDate) }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Repair Date:&nbsp;</div>
          <div>{{ getParseDateTime(details.repairDate) }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Date Shipped:&nbsp;</div>
          <div>{{ getParseDateTime(details.shipDate) }}</div>
        </div>

        <div class="row">
          <div class="text-grey-6">Tracking Number:&nbsp;</div>
          <div>{{ details.trackingNumber }}</div>
        </div>
      </div>
    </div>
    <div class="row q-mt-lg text-body1">
      <div class="text-grey-6">Customer Reported Issue Reproduced:&nbsp;</div>
      <div>{{ details.customerIssueReproduced }}</div>
    </div>

    <div class="row q-mt-md text-body1">
      <div class="text-grey-6">Customer Reported Issue:&nbsp;</div>
      <div>{{ details.customerReportedIssue }}</div>
    </div>

    <div class="row q-mt-md text-body1">
      <div class="text-grey-6">Customer Reported Issue External:&nbsp;</div>
      <div>{{ details.reportedIssueExt }}</div>
    </div>

    <div class="row q-mt-md text-body1">
      <div class="text-grey-6">Technician Notes:&nbsp;</div>
      <div>{{ details.techNotes }}</div>
    </div>

    <div class="row justify-center q-mt-md">
      <div class="col-auto">
        <q-btn
          label="Close"
          color="primary"
          style="min-width: 150px"
          @click="showDetailModal = false"
        />
      </div>
    </div>
  </BaseModal>
</template>

<script>
import { parseDateTime, parseDate } from "../utils/timeUtils.js";
export default {
  props: ["details"],
  data() {
    return {
      showDetailModal: false,
    };
  },
  methods: {
    displayDetailModal() {
      this.showDetailModal = true;
    },
    hideDetailModal() {
      this.showDetailModal = false;
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
