<template>
  <q-list class="q-pa-lg text-secondary text-subtitle1">
    <div class="q-ml-md q-mb-sm section-title">Sales Options</div>

    <q-item
      v-if="checkPermission('sales.order')"
      class="list-item"
      active-class="active-link"
      :to="{ name: 'sales-order' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="grading" />
      </q-item-section>
      <q-item-section> Sales Order </q-item-section>
    </q-item>
  </q-list>
</template>

<script>
import { useUserStore } from "src/stores/user";

export default {
  methods: {
    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>
