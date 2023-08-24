<template>
  <div>
    <div class="row justify-center">
      <div class="col-auto">
        <q-card flat bordered style="width: 700px">
          <q-card-section>
            <div class="text-body2 text-weight-medium q-mb-sm">
              Unit Summary
            </div>
            <q-separator />
            <div class="row justify-center items-center" style="height: 130px">
              <div class="col text-center">
                <div class="text-h4 text-weight-medium text-primary">
                  Awaiting QA/CA
                </div>
                <div class="text-body2 text-grey-6">Unit Status</div>
              </div>

              <div class="col text-center">
                <div class="text-h4 text-weight-medium text-primary">
                  2022/08/06
                </div>
                <div class="text-body2 text-grey-6">Scheduled Date</div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
    <div class="q-mt-md text-body1 text-weight-medium">Timeline</div>
    <ul class="timeline row justify-center">
      <li
        class="col relative-position text-center"
        v-for="status in statusItems"
        :key="status.status"
      >
        <div class="text-subtitle1 text-weight-medium">
          {{ status.status }}
        </div>
        <div v-if="status.completed" class="text-caption text-grey-6">
          {{ status.completeTime }}
        </div>

        <div
          class="timeline-dot row justify-center items-center"
          :class="status.completed ? 'bg-primary' : 'bg-grey-6'"
        >
          <div class="col-auto">
            <q-icon
              class="text-white"
              :name="status.completed ? 'done' : 'access_time'"
              size="md"
            ></q-icon>
          </div>
        </div>

        <div
          class="connector"
          :class="status.completed ? 'bg-primary' : 'bg-grey-6'"
        ></div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      statusItems: [
        {
          status: "Unit Received",
          completed: true,
          completeTime: "2021-07-19 03:42:24 PM",
        },
        {
          status: "Out for Repair",
          completed: true,
          completeTime: "2021-07-20 03:42:24 PM",
        },
        {
          status: "Repair Completed",
          completed: true,
          completeTime: "2021-07-21 03:42:24 PM",
        },
        {
          status: "QA/CA",
          completed: false,
          completeTime: null,
        },
        {
          status: "Unit Shipped",
          completed: false,
          completeTime: null,
        },
      ],
    };
  },
};
</script>
<style scoped>
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

</style>