<template>
  <div class="q-mx-lg">
    <div class="generic-container">

      <div class="q-px-lg q-py-md text-h6 text-weight-bold filtering-header">
        Notification Settings
      </div>
      <q-separator />
      <div class="q-px-lg q-py-md">
        <q-checkbox v-model="checkAll" color="green" @update:model-value="unsubscribeAll" label="Click here to unsubscribe to all emails" />
        <div class="text-subtitle1 text-weight-medium q-pt-sm">Repair/Rework:</div>
        <span>Notify me when each serial number has been:</span>
        <div class="q-pa-xs">
            <q-option-group
            :options="repairOptions"
            color="green"
            type="checkbox"
            v-model="repairGroups"
            ></q-option-group>
        </div>

        <div class="text-subtitle1 text-weight-medium q-pt-sm">Returns:</div>
        <span>Notify me when each serial number has been:</span>
        <div class="q-pa-xs">
            <q-option-group
            :options="returnOptions"
            color="green"
            type="checkbox"
            v-model="returnGroups"
            ></q-option-group>
        </div>

        <div class="row justify-center q-pb-sm">
          <q-btn
            class="col-auto"
            color="primary"
            @click="save"
            style="min-width: 200px"
            :loading="updating"
            >Save
          </q-btn>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
export default {
  data: () => {
    return{
      updating: false,
      checkAll:false,
      returnGroups: [], //['contact', 'invoice', 'report']
      returnOptions: [
          { label: 'Received', value: 'received' },
          { label: 'Scheduled', value: 'scheduled'},
          { label: 'Shipped', value: 'shipped'},
          { label: 'Invoiced', value: 'invoiced'},
          { label: 'Any Status Change', value: 'changed'},
      ],
      repairGroups: [], //['contact', 'invoice', 'report']
      repairOptions: [
          { label: 'Received', value: 'received' },
          { label: 'Scheduled', value: 'scheduled'},
          { label: 'Shipped', value: 'shipped'},
          { label: 'Invoiced', value: 'invoiced'},
          { label: 'Any Status Change', value: 'changed'},
      ],
    }
  },
  methods :{
    save(){
      //call backend api to save it
    },
    unsubscribeAll(){
      this.returnGroups = [];
      this.repairGroups = [];
    }
  }
};
</script>
