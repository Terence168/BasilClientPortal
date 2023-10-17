


<template>
  <q-list class="q-pa-lg text-secondary text-subtitle1">
    <div class="q-ml-md q-mb-sm section-title">Ticketing</div>

    <q-item v-if="!clientUser && checkPermission('ticketing.queue')"
      class="list-item"
      active-class="active-link"
      :to="{ name: 'ticketing-queue' }"
      style="height: 50px"
    >
      <q-item-section avatar >
        <q-icon name="confirmation_number" />
      </q-item-section>
      <q-item-section > Ticketing Queue </q-item-section>
    </q-item>

    <q-item
    v-if="checkPermission('ticketing.view')"
      class="list-item"
      active-class="active-link"
      :to="{ name: 'view-tickets' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="list" />
      </q-item-section>
      <q-item-section> View Tickets </q-item-section>
    </q-item>

    <q-item
    v-if="checkPermission('ticketing.add')"
      class="list-item"
      active-class="active-link"
      :to="{ name: 'create-ticket' }"
      style="height: 50px"
    >
      <q-item-section avatar>
        <q-icon name="add_circle" />
      </q-item-section>
      <q-item-section> Create Ticket </q-item-section>
    </q-item>
  </q-list>
</template>
<script>
import { useUserStore } from "stores/user";

export default {
  computed: {
    clientUser() {
      return useUserStore().isClientUser;
    },
  },
  methods: {
    checkPermission(permission) {
      return useUserStore().checkPermission(permission);
    },
  },
};
</script>