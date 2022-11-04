<script setup>
import { ref, reactive } from "vue";
import { api } from "boot/axios";
import StatusBox from "src/components/StatusBox.vue";

const props = defineProps({
  level: Number,
  category: String,
  data: Object,
  parent: [String, Number],
});

let category;

switch (props.level) {
  case 1:
    category = "partNumber";
    break;
  case 2:
    category = "rmaNumber";
    break;
  case 3:
    category = "serialNumber";
    break;
  default:
    category = null;
}

const children = ref([]);
const expanded = ref(false);
const loading = ref(false);

const serialDetails = reactive({ reportedIssue: null, faultCodes: null });

const expandCategory = function (categoryValue) {
  if (props.level >= 4) {
    return;
  }

  if (props.level === 3) {
    if (!expanded.value) {
      expanded.value = true;

      if (
        serialDetails.reportedIssue === null &&
        serialDetails.faultCodes === null
      ) {
        loading.value = true;

        api
          .get(`/rma/status/tier4?id=${props.data.id}`)
          .then(function (response) {
            const resData = response.data.data;
            if (resData == null) {
              serialDetails.reportedIssue = "None";
              serialDetails.faultCodes = "None";
            } else {
              serialDetails.reportedIssue = resData[0].reportedIssue;
              serialDetails.faultCodes = resData[0].faultCodes;
            }
            loading.value = false;
          })
          .catch(function (error) {
            // handle error
            console.log(error);
          });
      }
    } else {
      expanded.value = false;
    }
    return;
  }

  const params = new URLSearchParams(window.location.search);

  params.set(category, categoryValue);

  if (props.level === 2) {
    params.set("partNumber", props.parent);
  }

  if (!expanded.value) {
    expanded.value = true;
    if (children.value.length === 0) {
      loading.value = true;

      api
        .get(`/rma/status/tier${props.level + 1}?${params}`)
        .then(function (response) {
          children.value = response.data.data;
          loading.value = false;
        })
        .catch(function (error) {
          // handle error
          console.log(error);
        });
    }
  } else {
    expanded.value = false;
  }
};
</script>

<template>
  <div
    @click="expandCategory(data[category])"
    class="row q-py-xs category"
    style="max-width: 1100px; margin: 0 auto"
  >
    <div class="col-3 text-body1 text-weight-bold" style="padding-left: 10px">
      <span v-if="level === 1" class="text-subtitle2 text-grey-5">Model:</span>
      <span v-if="level === 2" class="q-ml-md text-subtitle2 text-grey-5"
        >Ticket#:</span
      >
      <span v-if="level === 3" class="q-ml-xl text-subtitle2 text-grey-5"
        >SN:</span
      >
      {{ data[category] }}

      <q-circular-progress
        v-if="loading"
        indeterminate
        rounded
        color="primary"
        size="24px"
      />
      <template v-else-if="level < 4">
        <q-icon
          v-if="!expanded"
          name="expand_more"
          size="24px"
          color="grey"
        ></q-icon>
        <q-icon v-else name="expand_less" size="24px" color="grey"></q-icon>
      </template>
    </div>
    <div class="col-9">
      <div class="row text-body1 text-center">
        <div v-if="level < 3" class="col-2">{{ data.inventory }}</div>
        <div v-else class="col-2">
          <q-icon
            v-if="data.inventory == 1"
            size="sm"
            color="green-4"
            name="done"
          />
        </div>
        <div v-if="level < 3" class="col-2">{{ data.outForRepair }}</div>
        <div v-else class="col-2">
          <q-icon
            v-if="data.outForRepair == 1"
            size="sm"
            color="green-4"
            name="done"
          />
        </div>
        <div v-if="level < 3" class="col-2">{{ data.quarantine }}</div>
        <div v-else class="col-2">
          <q-icon
            v-if="data.quarantine == 1"
            size="sm"
            color="yellow-4"
            name="pending"
          />
        </div>
        <div v-if="level < 3" class="col-2">{{ data.awaitingQaCa }}</div>
        <div v-else class="col-2">
          <q-icon
            v-if="data.awaitingQaCa == 1"
            size="sm"
            color="green-4"
            name="done"
          />
        </div>
        <div v-if="level < 3" class="col-2">{{ data.readyToShip }}</div>
        <div v-else class="col-2">
          <q-icon
            v-if="data.readyToShip == 1"
            size="sm"
            color="green-4"
            name="done"
          />
        </div>
        <div v-if="level < 3" class="col-2 text-weight-bold">
          {{ data.total }}
        </div>
        <div v-else class="col-2">-</div>
      </div>
    </div>
  </div>

  <template v-if="level < 3 && expanded">
    <StatusBox
      v-for="child in children"
      :key="child[category]"
      category="partNumber"
      :level="level + 1"
      :data="child"
      :parent="data[category]"
  /></template>

  <template v-if="level === 3 && expanded && !loading">
    <div
      class="q-py-xs category text-subtitle2"
      style="max-width: 1100px; margin: 0 auto; padding-left: 100px"
    >
      <div>
        <span class="text-red">Customer Reported issue:</span>
        {{ serialDetails.reportedIssue }}
      </div>
      <div>
        <span class="text-red">Fault Code(s):</span>
        {{ serialDetails.faultCodes }}
      </div>
    </div>
  </template>
</template>

<style lang="scss" scoped>
.category {
  border-bottom: 1px solid $grey-3;
  max-width: 1100px;
  margin: 0 auto;
  cursor: pointer;
}

.category:hover {
  background-color: $grey-3;
}
</style>
